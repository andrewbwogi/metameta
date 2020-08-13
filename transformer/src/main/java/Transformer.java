import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
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

public class Transformer {
    private String resources;
    private String outputName;
    private boolean begin;
    CtClass trans = null;
    CtClass clAdapter = null;
    CtClass mtAdapter = null;

    public Transformer() {
        resources = "./transformer/src/main/resources/";
    }

    public void add(InvocationWrapper inv,int kind) throws Exception {
        begin = inv.isAddBegin();
        if(begin)
            outputName = "Begin" + kind;
        else
            outputName = "End" + kind;
        add(inv.getInvocation(), inv.getModifiedMethod());
    }

    private void add(CtInvocation call, String modifiedMethod) throws Exception {

        // get method definition from invocation
        CtMethod method = getMethod(call);

        // create and rename templates
        createTemplates();

        // run asmifier and retrieve output
        CtClass asmified = runASMifier(call,method);

        // get the asmifier generated method definition and invocation
        CtBlock methodDefinition = getDefinition(asmified);
        CtBlock methodCall = getCall(asmified);

        // get bytecode description of inserted method
        String desc = getDesc(methodCall);

        // set the templates into specific metaprograms
        setClAdapter(method,methodDefinition,desc,modifiedMethod);
        setMtAdapter(methodCall,desc);

        // write classes to disk
        write();
    }

        private CtMethod getMethod(CtInvocation call) throws Exception {

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
        return method;
    }

    private void createTemplates(){
        String sourcePath = resources + "ASMTransformer.java";
        trans = (CtClass) Utils.readClass(sourcePath, "ASMTransformer");
        trans.setSimpleName(trans.getSimpleName() + outputName);
        sourcePath = resources + "ClassAdapter.java";
        clAdapter = (CtClass) Utils.readClass(sourcePath, "ClassAdapter");
        clAdapter.setSimpleName(clAdapter.getSimpleName() + outputName);
        Set<CtConstructor> set = clAdapter.getConstructors();
        for (CtConstructor c : set)
            c.setSimpleName(clAdapter.getSimpleName() + outputName);
        if (begin)
            sourcePath = resources + "MethodAdapterBegin.java";
        else
            sourcePath = resources + "MethodAdapterEnd.java";
        mtAdapter = (CtClass) Utils.readClass(sourcePath, "MethodAdapter");
        mtAdapter.setSimpleName(mtAdapter.getSimpleName() + outputName);
        set = mtAdapter.getConstructors();
        for (CtConstructor c : set)
            c.setSimpleName(mtAdapter.getSimpleName() + outputName);

        // modify type names
        List<CtTypeReference> typeList = clAdapter.filterChildren((CtTypeReference t) ->
                t.getSimpleName().equals("MethodAdapter")).list();
        for (CtTypeReference r : typeList)
            r.setSimpleName(r.getSimpleName() + outputName);
        typeList = trans.filterChildren((CtTypeReference t) -> t.getSimpleName().equals("ClassAdapter")).list();
        for (CtTypeReference r : typeList)
            r.setSimpleName(r.getSimpleName() + outputName);
    }

    private CtClass runASMifier(CtInvocation call, CtMethod method) throws Exception {

        // create dummy type
        String sourcePath = resources + "Empty.java";
        CtClass dummy = (CtClass) Utils.readClass(sourcePath, "Empty");

        // put method, fields and call into dummy type
        Set<CtConstructor> set = dummy.getConstructors();
        for (CtConstructor c : set)
            c.getBody().insertBegin(call.clone());
        dummy.addMethod(method);
        addDummyFields(method, dummy);

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
        return Launcher.parseClass(toASM(is));
    }

    private void addDummyFields(CtMethod method, CtType dummy) {
        FieldFactory fieldFactory = method.getFactory().Field();
        ArrayList<String> nameList = new ArrayList<>();
        List<CtFieldReference> fields = method.filterChildren((CtFieldReference t) -> true).list();
        for (CtFieldReference r : fields) {
            if (!nameList.contains(r.getSimpleName())) {
                fieldFactory.create(dummy, r.getModifiers(), r.getType(), r.getSimpleName());
                nameList.add(r.getSimpleName());
            }
        }
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

    private String getDesc(CtBlock methodCall) {
        List<CtInvocation> invList = methodCall.filterChildren((CtInvocation inv) ->
                inv.getExecutable().getSimpleName().equals("visitMethodInsn")).list();
        String desc = "";
        for(CtInvocation i : invList) {
            if(i.getArguments().get(1).toString().equals("\"Empty\""))
                desc = i.getArguments().get(3).toString();
        }
        return desc.substring(1, desc.length() - 1);
    }

    private void setClAdapter(CtMethod method, CtBlock methodDefinition, String desc, String modifiedMethodName) {

        // remove first assignment statement
        CtStatement assign = methodDefinition.getStatement(0);
        methodDefinition.removeStatement(assign);

        // modify definition
        Factory factory = clAdapter.getFactory();
        List<CtLiteral> literalList = methodDefinition.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("Empty")).list();
        CtVariableRead variableRead = factory.createVariableRead();
        CtVariableReference variableReference = factory.createLocalVariableReference();
        variableReference.setSimpleName("className");
        variableRead.setVariable(variableReference);
        literalList.get(0).replace(variableRead);

        // add definition to base class
        List<CtMethod> methodDef = clAdapter.getMethodsByName("definition");
        methodDef.get(0).setBody(methodDefinition);

        // replace literals in clAdapter fields
        literalList = clAdapter.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("modifiedMethod")).list();
        literalList.get(0).replace(factory.createLiteral(modifiedMethodName));
        literalList = clAdapter.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("newMethod")).list();
        literalList.get(0).replace(factory.createLiteral(method.getSimpleName()));
        literalList = clAdapter.filterChildren((CtLiteral l) ->
                l.getType().getSimpleName().equals("String") && l.getValue().equals("desc")).list();
        literalList.get(0).replace(factory.createLiteral(desc));

        // add fields to final program
        addASMFields(method, methodDefinition);
    }

    private void setMtAdapter(CtBlock methodCall, String desc) {

        // trim the call statements
        for (int i = 0; i < 10; i++)
            methodCall.removeStatement(methodCall.getStatement(0));
        int sizeCount = methodCall.getStatements().size();
        for (int i = sizeCount; i > sizeCount - 6; i--) {
            int size = methodCall.getStatements().size();
            methodCall.removeStatement(methodCall.getStatement(size - 1));
        }

        // modify call
        Factory factory = mtAdapter.getFactory();
        List<CtLiteral> literalList = methodCall.filterChildren((CtLiteral l) ->
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
    }

    private void addASMFields(CtMethod method, CtBlock methodDefinition) {
        String outputName = "ClassAdapter" + this.outputName;

        // create new name and desc fields, put them in clAdapter
        Factory factory = method.getFactory();
        FieldFactory fieldFactory = factory.Field();
        CodeFactory codeFactory = factory.Code();
        TypeFactory typeFactory = factory.Type();
        ArrayList<String> nameList = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        List<CtNewFieldReference> fields = method.filterChildren((CtNewFieldReference t) -> true).list();
        int i = 0;
        for (CtNewFieldReference r : fields) {
            if (!nameList.contains(r.getSimpleName())) {
                fieldFactory.create(clAdapter, Set.of(ModifierKind.PRIVATE), typeFactory.STRING,
                        "fieldName" + i, codeFactory.createLiteral(r.getSimpleName()));
                fieldFactory.create(clAdapter, Set.of(ModifierKind.PRIVATE), typeFactory.STRING,
                        "fieldDesc" + i, codeFactory.createLiteral(getInternalName(r.getType())));
                nameList.add(r.getSimpleName());
                hashMap.put(r.getSimpleName(), "fieldName" + i);
                i++;
            }
        }

        // set number of new fields
        CtMethod addFields = clAdapter.getMethod("addFields");
        List<CtLocalVariable> locals = addFields.filterChildren((CtLocalVariable t) -> true).list();
        for (CtLocalVariable l : locals) {
            if (l.getSimpleName().equals("NEWFIELDS"))
                l.setAssignment(codeFactory.createLiteral(i));
        }

        // set class name
        List<CtAssignment> assigns = addFields.filterChildren((CtAssignment t) -> true).list();
        for (CtAssignment a : assigns) {
            if (a.getAssigned().toString().equals("field")) {
                CtInvocation inv = (CtInvocation) a.getAssignment();
                CtFieldRead read = (CtFieldRead) inv.getTarget();
                CtTypeAccess access = (CtTypeAccess) read.getTarget();
                access.setAccessedType(typeFactory.createReference(outputName));
            }
        }

        // create missing name fields, put them in clAdapter
        ArrayList<String> missingNameList = new ArrayList<>();
        List<CtFieldReference> missingFields = method.filterChildren((CtFieldReference t) ->
                t.getClass() != CtNewFieldReference.class).list();
        int j = 0;
        for (CtFieldReference r : missingFields) {
            if (!missingNameList.contains(r.getSimpleName())) {
                fieldFactory.create(clAdapter, Set.of(ModifierKind.PRIVATE), typeFactory.STRING,
                        "missingFieldName" + j, codeFactory.createLiteral(r.getSimpleName()));
                fieldFactory.create(clAdapter, Set.of(ModifierKind.PRIVATE), typeFactory.STRING,
                        "missingFieldDesc" + j, codeFactory.createLiteral(getInternalName(r.getType())));
                missingNameList.add(r.getSimpleName());
                j++;
            }
        }

        // set number of missing fields
        CtMethod addMissingFields = clAdapter.getMethod("addMissingFields");
        List<CtLocalVariable> localsMissing = addMissingFields.filterChildren((CtLocalVariable t) -> true).list();
        for (CtLocalVariable l : localsMissing) {
            if (l.getSimpleName().equals("MISSINGFIELDS"))
                l.setAssignment(codeFactory.createLiteral(j));
        }

        // set class name
        List<CtAssignment> missingAssigns = addMissingFields.filterChildren((CtAssignment t) -> true).list();
        for (CtAssignment a : missingAssigns) {
            if (a.getAssigned().toString().equals("field")) {
                CtInvocation inv = (CtInvocation) a.getAssignment();
                CtFieldRead read = (CtFieldRead) inv.getTarget();
                CtTypeAccess access = (CtTypeAccess) read.getTarget();
                access.setAccessedType(typeFactory.createReference(outputName));
            }
        }

        // replace expressions in the method definition
        List<CtInvocation> invs = methodDefinition.filterChildren((CtInvocation t) -> true).list();
        for (CtInvocation in : invs) {
            if (in.getExecutable().getSimpleName().equals("visitFieldInsn")) {
                CtLiteral l = (CtLiteral) in.getArguments().get(1);
                if (l.getValue().equals("Empty")) {

                    // replace class names with variables in asmified definition
                    CtVariableRead variableRead = factory.createVariableRead();
                    CtVariableReference variableReference = factory.createLocalVariableReference();
                    variableReference.setSimpleName("className");
                    variableRead.setVariable(variableReference);
                    l.replace(variableRead);

                    // replace new field names with variables in asmified definition
                    l = (CtLiteral) in.getArguments().get(2);
                    if (nameList.contains(l.getValue())) {
                        variableRead = factory.createVariableRead();
                        variableReference = factory.createLocalVariableReference();
                        variableReference.setSimpleName(hashMap.get(l.getValue()));
                        variableRead.setVariable(variableReference);
                        l.replace(variableRead);
                    }
                }
            }
        }
    }

    public static String getInternalName(CtTypeReference ref) {
        String name = ref.getSimpleName();
        int occurance = StringUtils.countMatches(name, '[');
        name = StringUtils.remove(name,"[]");
        String dim = "";
        for(int i=0;i<occurance;i++)
            dim += "[";
        String type = "";
        if (name.equals("boolean"))
            type = "Z";
        else if (name.equals("byte"))
            type = "B";
        else if (name.equals("char"))
            type = "C";
        else if (name.equals("double"))
            type = "D";
        else if (name.equals("float"))
            type = "F";
        else if (name.equals("int"))
            type = "I";
        else if (name.equals("long"))
            type = "J";
        else if (name.equals("short"))
            type = "S";
        else {
            String qName = ref.getQualifiedName();
            qName = StringUtils.remove(qName,"[]");
            type = "L" + qName.replace('.', '/') + ";";
        }
        return dim+type;
    }

    private CtBlock getDefinition(CtClass asmified) {
        List<CtBlock> blockList = asmified.filterChildren((CtBlock block) -> true).list();
        return blockList.get(blockList.size() - 1);
    }

    private CtBlock getCall(CtClass asmified) {
        List<CtBlock> blockList = asmified.filterChildren((CtBlock block) -> true).list();
        return blockList.get(blockList.size() - 2);
    }

    private void write() {
        Utils.writeClass(trans, "./asm/src/main/java/ASMTransformer" + outputName + ".java");
        Utils.writeClass(clAdapter, "./asm/src/main/java/ClassAdapter" + outputName + ".java", getImports());
        Utils.writeClass(mtAdapter, "./asm/src/main/java/MethodAdapter" + outputName + ".java", getImports());
    }

    private String getImports() {
        return "import org.objectweb.asm.Opcodes;\n" +
                "import static org.objectweb.asm.Opcodes.*;\n" +
                "import org.objectweb.asm.commons.AdviceAdapter;\n";
    }

    public void setResources(String r) {
        resources = r;
    }

    public static void main(final String args[]) throws Exception {
        Transformer transformer = new Transformer();
        String resources = Transformer.class.getClassLoader().getResource("").getPath();
        transformer.setResources(resources);
        Constructor c = new Constructor();
        c.setResources("/home/andrewb/Desktop/metameta/spoon/src/main/resources/");
        //transformer.addEnd(c.constructCall12("newMethod"), "Field1", "method");
    }
}