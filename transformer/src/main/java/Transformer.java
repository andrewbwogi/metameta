import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.ASMifier;
import org.objectweb.asm.util.TraceClassVisitor;
import spoon.Launcher;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FieldFactory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.declaration.CtMethodImpl;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Transformer {
    private String resources;

    public Transformer() {
        resources = "./transformer/src/main/resources/";
    }

    public void addBegin(CtInvocation call, String outputName, String modifiedMethod) throws Exception {
        add(call, outputName, modifiedMethod, true);
    }

    public void addEnd(CtInvocation call, String outputName, String modifiedMethod) throws Exception {
        add(call, outputName, modifiedMethod, false);
    }

    private void add(CtInvocation call, String outputName, String modifiedMethod, boolean begin) throws Exception {

        // get method from invocation
        CtExecutable executable = call.getExecutable().getDeclaration();
        if (executable.getClass() != CtMethodImpl.class)
            throw new Exception();
        CtMethod method = (CtMethod) executable;

        // forbid other modifiers than public
        Set<ModifierKind> modifierSet = method.getModifiers();
        for (ModifierKind m : modifierSet) {
            if (m != ModifierKind.PUBLIC)
                throw new Exception();
        }

        // forbid exception propagation
        Set<CtTypeReference> thrownSet = method.getThrownTypes();
        if (!thrownSet.isEmpty())
            throw new Exception();

        // get and rename baseclasses
        String sourcePath = resources + "ASMTransformer.java";
        CtClass trans = (CtClass) Utils.readClass(sourcePath, "ASMTransformer");
        trans.setSimpleName(trans.getSimpleName() + outputName);
        sourcePath = resources + "ClassAdapter.java";
        CtClass clAdapter = (CtClass) Utils.readClass(sourcePath, "ClassAdapter");
        clAdapter.setSimpleName(clAdapter.getSimpleName() + outputName);
        Set<CtConstructor> set = clAdapter.getConstructors();
        for (CtConstructor c : set)
            c.setSimpleName(clAdapter.getSimpleName() + outputName);
        if (begin)
            sourcePath = resources + "MethodAdapterBegin.java";
        else
            sourcePath = resources + "MethodAdapterEnd.java";
        CtClass mtAdapter = (CtClass) Utils.readClass(sourcePath, "MethodAdapter");
        mtAdapter.setSimpleName(mtAdapter.getSimpleName() + outputName);
        set = mtAdapter.getConstructors();
        for (CtConstructor c : set)
            c.setSimpleName(mtAdapter.getSimpleName() + outputName);

        // modify type names
        List<CtTypeReference> typeList = clAdapter.filterChildren((CtTypeReference t) -> t.getSimpleName().equals("MethodAdapter")).list();
        for (CtTypeReference r : typeList)
            r.setSimpleName(r.getSimpleName() + outputName);
        typeList = trans.filterChildren((CtTypeReference t) -> t.getSimpleName().equals("ClassAdapter")).list();
        for (CtTypeReference r : typeList)
            r.setSimpleName(r.getSimpleName() + outputName);

        // create dummy type
        sourcePath = resources + "Empty.java";
        CtClass dummy = (CtClass) Utils.readClass(sourcePath, "Empty");

        // put method, fields and call into dummy type
        set = dummy.getConstructors();
        for (CtConstructor c : set)
            c.getBody().insertBegin(call.clone());
        dummy.addMethod(method);
        addFields(method,dummy);

        // write type
        String toCompile = resources + "dummy/Empty.java";
        new File(toCompile).getParentFile().mkdirs();
        Utils.writeClass(dummy, toCompile);

        // compile type
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        compiler.run(null, null, null, toCompile);
        File classFile = new File(resources + "dummy/Empty.class");
        FileInputStream is = new FileInputStream(classFile);

        // run asmifier and retrieve output
        CtClass asmified = Launcher.parseClass(toASM(is));

        // get the second block
        List<CtBlock> blockList = asmified.filterChildren((CtBlock block) -> true).list();
        CtBlock methodDefinition = blockList.get(blockList.size() - 1);

        // remove first assignment statement
        CtStatement assign = methodDefinition.getStatement(0);
        methodDefinition.removeStatement(assign);

        // get first block
        CtBlock methodCall = blockList.get(blockList.size() - 2);
        for (int i = 0; i < 10; i++)
            methodCall.removeStatement(methodCall.getStatement(0));
        int sizeCount = methodCall.getStatements().size();
        for (int i = sizeCount; i > sizeCount - 6; i--) {
            int size = methodCall.getStatements().size();
            methodCall.removeStatement(methodCall.getStatement(size - 1));
        }

        // get description
        List<CtInvocation> invList = methodCall.filterChildren((CtInvocation inv) ->
                inv.getExecutable().getSimpleName().equals("visitMethodInsn")).list();
        List descList = (List) invList.get(0).getArguments().stream()
                .filter(e -> e.toString().contains("(")).collect(Collectors.toList());
        String desc = descList.get(0).toString();
        desc = desc.substring(1, desc.length() - 1);

        // add definition
        List<CtMethod> methodDef = clAdapter.getMethodsByName("definition");
        methodDef.get(0).setBody(methodDefinition);

        // replace literals
        Factory factory = dummy.getFactory();
        List<CtLiteral> literalList = clAdapter.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("modifiedMethod")).list();
        literalList.get(0).replace(factory.createLiteral(modifiedMethod));
        literalList = clAdapter.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("newMethod")).list();
        literalList.get(0).replace(factory.createLiteral(method.getSimpleName()));
        literalList = clAdapter.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("desc")).list();
        literalList.get(0).replace(factory.createLiteral(desc));

        // modify call
        literalList = methodCall.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("Empty")).list();
        CtVariableRead variableRead = factory.createVariableRead();
        CtVariableReference variableReference = factory.createLocalVariableReference();
        variableReference.setSimpleName("className");
        variableRead.setVariable(variableReference);
        literalList.get(0).replace(variableRead);

        if (!begin && !desc.endsWith("V")) {

            // set the last pop instruction first
            CtStatement pop = methodCall.getLastStatement();
            methodCall.removeStatement(pop);
            methodCall.insertBegin(pop);
        }

        // rename accessed type
        List<CtStatement> stmntList = methodCall.getStatements();
        CtVariableRead variableRead2 = factory.createVariableRead();
        CtVariableReference variableReference2 = factory.createLocalVariableReference();
        variableReference2.setSimpleName("mv");
        variableRead2.setVariable(variableReference2);
        for (CtStatement s : stmntList) {
            ((CtInvocation) s).setTarget(variableRead2);
        }

        // don't set call if modified method is void and inserted at end
        if (!(!begin && desc.endsWith("V"))) {
            List<CtMethod> callDef = mtAdapter.getMethodsByName("call");
            callDef.get(0).setBody(methodCall);
        }

        // add fields to final program
        addASMFields(method,clAdapter,methodDefinition);

        // write classes
        Utils.writeClass(trans, "./asm/src/main/java/ASMTransformer" + outputName + ".java");
        Utils.writeClass(clAdapter, "./asm/src/main/java/ClassAdapter" + outputName + ".java", getImports());
        Utils.writeClass(mtAdapter, "./asm/src/main/java/MethodAdapter" + outputName + ".java", getImports());
    }

    private void addFields(CtMethod method,CtType dummy) {
        FieldFactory fieldFactory = method.getFactory().Field();
        ArrayList<String> nameList = new ArrayList<>();
        List<CtFieldReference> fields = method.filterChildren((CtFieldReference t) -> true).list();
        for(CtFieldReference r : fields){
            if(!nameList.contains(r.getSimpleName())) {
                fieldFactory.create(dummy,r.getModifiers(),r.getType(),r.getSimpleName());
                nameList.add(r.getSimpleName());
            }
        }
        }

    private void addASMFields(CtMethod method,CtType clAdapter,CtBlock methodDefinition) {

        // create name and desc fields, put them in clAdapter
        Factory factory = method.getFactory();
        FieldFactory fieldFactory = factory.Field();
        CodeFactory codeFactory = factory.Code();
        TypeFactory typeFactory = factory.Type();
        ArrayList<String> nameList = new ArrayList<>();
        HashMap<String,String> hashMap = new HashMap<>();
        List<CtNewFieldReference> fields = method.filterChildren((CtNewFieldReference t) -> true).list();
        int i = 0;
        for(CtNewFieldReference r : fields){
            if(!nameList.contains(r.getSimpleName())) {
                fieldFactory.create(clAdapter,Set.of(ModifierKind.PRIVATE),typeFactory.STRING,
                        "fieldName"+i,codeFactory.createLiteral(r.getSimpleName()));
                fieldFactory.create(clAdapter,Set.of(ModifierKind.PRIVATE),typeFactory.STRING,
                        "fieldDesc"+i,codeFactory.createLiteral(getInternalName(r.getType().getSimpleName())));
                nameList.add(r.getSimpleName());
                hashMap.put(r.getSimpleName(),"fieldName"+i);
                i++;
                System.out.println("r: " + r.getSimpleName());
            }
        }

        // set number of fields
        CtMethod addFields = clAdapter.getMethod("addFields");
        List<CtLocalVariable> locals = addFields.filterChildren((CtLocalVariable t) -> true).list();
        for(CtLocalVariable l : locals){
            if(l.getSimpleName().equals("NEWFIELDS"))
                l.setAssignment(codeFactory.createLiteral(i));
        }

        // set class name
        List<CtAssignment> assigns = addFields.filterChildren((CtAssignment t) -> true).list();
        for(CtAssignment a : assigns){
            if(a.getAssigned().toString().equals("field")) {
                CtInvocation inv = (CtInvocation) a.getAssignment();
                CtFieldRead read = (CtFieldRead) inv.getTarget();
                CtTypeAccess access = (CtTypeAccess) read.getTarget();
                access.setAccessedType(typeFactory.createReference(clAdapter.getSimpleName()));
            }
        }

        // replace expressions in the method definition
        List<CtInvocation> invs = methodDefinition.filterChildren((CtInvocation t) -> true).list();
        for(CtInvocation in : invs){
            if(in.getExecutable().getSimpleName().equals("visitFieldInsn")) {

                // replace class names with variables in asmified definition
                CtLiteral l = (CtLiteral) in.getArguments().get(1);
                CtVariableRead variableRead = factory.createVariableRead();
                CtVariableReference variableReference = factory.createLocalVariableReference();
                variableReference.setSimpleName("className");
                variableRead.setVariable(variableReference);
                l.replace(variableRead);

                // replace new field names with variables in asmified definition
                l = (CtLiteral) in.getArguments().get(2);
                if(nameList.contains(l.getValue())) {
                    variableRead = factory.createVariableRead();
                    variableReference = factory.createLocalVariableReference();
                    variableReference.setSimpleName(hashMap.get(l.getValue()));
                    variableRead.setVariable(variableReference);
                    l.replace(variableRead);
                }
            }
        }
    }

    public static String getInternalName(String name) {
        if(name.equals("boolean"))
            return "Z";
        else if(name.equals("bytec"))
            return "B";
        else if(name.equals("char"))
            return "C";
        else if(name.equals("double"))
            return "D";
        else if(name.equals("float"))
            return "F";
        else if(name.equals("int"))
            return "I";
        else if(name.equals("long"))
            return "J";
        else if(name.equals("short"))
            return "S";
        else
            return "";
    }

    private String getImports() {
        return "import org.objectweb.asm.Opcodes;\n" +
                "import static org.objectweb.asm.Opcodes.*;\n";
    }

    private String toASM(FileInputStream is) throws Exception {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ClassReader cr = new ClassReader(is);
        TraceClassVisitor tcv = new TraceClassVisitor(null, new ASMifier(), pw);
        cr.accept(tcv, ClassReader.EXPAND_FRAMES);
        pw.close();
        return sw.toString();
    }

    public void setResources(String r) {
        resources = r;
    }

    public static void main(final String args[]) throws Exception {
        Transformer transformer = new Transformer();
        String resources = Transformer.class.getClassLoader().getResource("").getPath();
        transformer.setResources(resources);
        Constructor c = new Constructor();
        c.setResources(resources);
        transformer.addEnd(c.constructCall6("newMethod"), "Field1", "method");
    }
}