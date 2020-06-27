import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.code.CtInvocationImpl;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Transformer extends AbstractTransformer {

    Transformer() {
        super(new File("src/main/resources").getAbsolutePath() + "/BaseClass.java","BaseClass");
    }

    void addBegin(CtInvocation call,CtMethod method, String outputName,String fileOut, String modifiedMethod) throws Exception {

        // get and rename baseclasses
        String sourcePath = new File("src/main/resources/ASMTransformer.java").getAbsolutePath();
        CtClass trans = (CtClass) getType(sourcePath,"ASMTransformer");
        trans.setSimpleName(trans.getSimpleName()+outputName);
        sourcePath = new File("src/main/resources/ClassAdapter.java").getAbsolutePath();
        CtClass clAdapter = (CtClass) getType(sourcePath,"ClassAdapter");
        clAdapter.setSimpleName(clAdapter.getSimpleName()+outputName);
        Set<CtConstructor> set = clAdapter.getConstructors();
        for(CtConstructor c : set)
            c.setSimpleName(clAdapter.getSimpleName()+outputName);
        sourcePath = new File("src/main/resources/MethodAdapter.java").getAbsolutePath();
        CtClass mtAdapter = (CtClass) getType(sourcePath,"MethodAdapter");
        mtAdapter.setSimpleName(mtAdapter.getSimpleName()+outputName);
        set = mtAdapter.getConstructors();
        for(CtConstructor c : set)
            c.setSimpleName(mtAdapter.getSimpleName()+outputName);

        // create dummy type
        sourcePath = new File("src/main/resources/Empty.java").getAbsolutePath();
        CtClass dummy = (CtClass) getType(sourcePath,"Empty");

        // put method and call into dummy type
        set = dummy.getConstructors();
        for(CtConstructor c : set)
            c.getBody().insertBegin(call.clone());
        //CtExecutableReference ex = call.getExecutable();
        //CtExecutable e = ex.getDeclaration();
        dummy.addMethod(method);

        // write type
        CtType baseClass = type;
        type = dummy;
        File toCompile = new File("src/main/resources/Dummy/Empty.java");
        writeClass(toCompile.getAbsolutePath());
        type = baseClass;

        // compile type
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, toCompile.getPath());
        File classFile = new File("src/main/resources/Dummy/Empty.class");
        FileInputStream is = new FileInputStream(classFile);

        // run asmifier and retrieve output
        CtClass asmified = Launcher.parseClass(toASM(is));

        // get the second block
        List<CtBlock> list = asmified.filterChildren((CtBlock block)->true).list();
        CtBlock methodDefinition = list.get(list.size()-1);

        // remove first assignment statement
        CtStatement assign = methodDefinition.getStatement(0);
        methodDefinition.removeStatement(assign);

        // set definition
        List<CtMethod> defMethod = clAdapter.getMethodsByName("definition");
        defMethod.get(0).setBody(methodDefinition);

        // get first block
        CtBlock methodCall = list.get(list.size()-2);
        for(int i = 0;i<10;i++)
            methodCall.removeStatement(methodCall.getStatement(0));
        int sizeCount = methodCall.getStatements().size();
        for(int i = sizeCount;i>sizeCount-6;i--) {
            int size = methodCall.getStatements().size();
            methodCall.removeStatement(methodCall.getStatement(size - 1));
        }

        // get description
        List<CtInvocation> invList = methodCall.filterChildren((CtInvocation inv)->
                inv.getExecutable().getSimpleName().equals("visitMethodInsn")).list();
        List descList = (List) invList.get(0).getArguments().stream()
                .filter(e->e.toString().contains("(")).collect(Collectors.toList());
        String desc = descList.get(0).toString();
        desc = desc.substring(1,desc.length()-1);

        // replace literals
        Factory factory = type.getFactory();
        List<CtLiteral> literalList = clAdapter.filterChildren((CtLiteral l)->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("modifiedMethod")).list();
        literalList.get(0).replace(factory.createLiteral(modifiedMethod));
        literalList = clAdapter.filterChildren((CtLiteral l)->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("newMethod")).list();
        literalList.get(0).replace(factory.createLiteral(method.getSimpleName()));
        literalList = clAdapter.filterChildren((CtLiteral l)->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("desc")).list();
        literalList.get(0).replace(factory.createLiteral(desc));

        // modify call
        literalList = methodCall.filterChildren((CtLiteral l)->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("Empty")).list();
        CtVariableRead variableRead = factory.createVariableRead();
        CtVariableReference variableReference = factory.createCatchVariableReference();
        variableReference.setSimpleName("className");
        variableRead.setVariable(variableReference);
        literalList.get(0).replace(variableRead);

        // set call
        List<CtMethod> defCall = mtAdapter.getMethodsByName("call");
        defCall.get(0).setBody(methodCall);

        // write classes
        type = trans;
        writeClass(fileOut + "/asm/ASMTransformer" + outputName + ".java");
        type = clAdapter;
        writeClass(fileOut + "/asm/ClassAdapter" + outputName + ".java");
        type = mtAdapter;
        writeClass(fileOut + "/asm/MethodAdapter" + outputName + ".java");
    }

    void addEnd(CtMethod method) {}

    void addField(CtField field) {}

    CtType getASMProgram() {
        return type;
    }

    private String toASM(FileInputStream is) throws Exception{
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ClassReader cr = new ClassReader(is);
        TraceClassVisitor tcv = new TraceClassVisitor(null, new ASMifier(), pw);
        cr.accept(tcv, ClassReader.EXPAND_FRAMES);
        pw.close();
        return sw.toString();
    }

    public static void main(final String args[]) throws Exception {
        String resources = new File("src/main/resources/").getAbsolutePath();
        Transformer transformer = new Transformer();
        Constructor c = new Constructor();
        transformer.addBegin(c.constructCall1("newMethod"),c.constructMethod1("newMethod"),"1",resources,"method");
    }
    }