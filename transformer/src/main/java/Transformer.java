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
import spoon.support.reflect.declaration.CtMethodImpl;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Transformer extends AbstractTransformer {
    //String resources = Transformer.class.getClassLoader().getResource("").getPath();
    String resources = "./transformer/src/main/resources/";

    Transformer() {
        //super(Transformer.class.getClassLoader().getResource("").getPath() + "/BaseClass.java","BaseClass");
        super("./transformer/src/main/resources/" + "/BaseClass.java","BaseClass");
    }

    void addBegin(CtInvocation call, String outputName, String modifiedMethod) throws Exception {

        // get method from invocation
        CtExecutable executable = call.getExecutable().getDeclaration();
        if(executable.getClass() != CtMethodImpl.class)
            throw new Exception();
        CtMethod method = (CtMethod) executable;

        // get and rename baseclasses
        String sourcePath = resources + "ASMTransformer.java";
        CtClass trans = (CtClass) getType(sourcePath,"ASMTransformer");
        trans.setSimpleName(trans.getSimpleName()+outputName);
        sourcePath = resources + "ClassAdapter.java";
        CtClass clAdapter = (CtClass) getType(sourcePath,"ClassAdapter");
        clAdapter.setSimpleName(clAdapter.getSimpleName()+outputName);
        Set<CtConstructor> set = clAdapter.getConstructors();
        for(CtConstructor c : set)
            c.setSimpleName(clAdapter.getSimpleName()+outputName);
        sourcePath = resources + "MethodAdapterBegin.java";
        CtClass mtAdapter = (CtClass) getType(sourcePath,"MethodAdapter");
        mtAdapter.setSimpleName(mtAdapter.getSimpleName()+outputName);
        set = mtAdapter.getConstructors();
        for(CtConstructor c : set)
            c.setSimpleName(mtAdapter.getSimpleName()+outputName);

        // create dummy type
        sourcePath = resources + "Empty.java";
        CtClass dummy = (CtClass) getType(sourcePath,"Empty");

        // put method and call into dummy type
        set = dummy.getConstructors();
        for(CtConstructor c : set)
            c.getBody().insertBegin(call.clone());
        dummy.addMethod(method);

        // write type
        CtType baseClass = type;
        type = dummy;
        String toCompile = resources + "dummy/Empty.java";
        writeClass(toCompile);
        type = baseClass;

        // compile type
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, toCompile);
        File classFile = new File(resources+"dummy/Empty.class");
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
        CtVariableReference variableReference = factory.createLocalVariableReference();
        variableReference.setSimpleName("className");
        variableRead.setVariable(variableReference);
        literalList.get(0).replace(variableRead);

        // rename accessed type
        List<CtStatement> stmntList = methodCall.getStatements();
        CtVariableRead variableRead2 = factory.createVariableRead();
        CtVariableReference variableReference2 = factory.createLocalVariableReference();
        variableReference2.setSimpleName("mv");
        variableRead2.setVariable(variableReference2);
        for(CtStatement s : stmntList){
            ((CtInvocation) s).setTarget(variableRead2);
        }

        // set call
        List<CtMethod> defCall = mtAdapter.getMethodsByName("call");
        defCall.get(0).setBody(methodCall);

        // write classes
        type = trans;
        writeClass("./asm/src/main/java/ASMTransformer" + outputName + ".java");
        type = clAdapter;
        writeClass("./asm/src/main/java/ClassAdapter" + outputName + ".java",getImports());
        type = mtAdapter;
        writeClass("./asm/src/main/java/MethodAdapter" + outputName + ".java",getImports());
    }

    void addEnd(CtMethod method) {}

    void addField(CtField field) {}

    CtType getASMProgram() {
        return type;
    }

    private String getImports(){
        return "import org.objectweb.asm.Opcodes;\n" +
                "import static org.objectweb.asm.Opcodes.*;\n";
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
        Transformer transformer = new Transformer();
        Constructor c = new Constructor();
        transformer.addBegin(c.constructCall1("newMethod"),"1","method");
    }
    }