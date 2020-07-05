import spoon.Launcher;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.MethodFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.List;
import java.util.Set;

public class Constructor {
    private Factory factory;
    private CtType type;
    private CodeFactory codeFactory;
    private MethodFactory methodFactory;
    private String resources;

    public Constructor(CtType type){
        this.type = type;
        initFields();
    }

    public Constructor(){
        type = Launcher.parseClass("class A {}");
        initFields();
    }

    private void initFields(){
        this.factory = type.getFactory();
        this.codeFactory = factory.Code();
        this.methodFactory = factory.Method();
        resources = "./spoon/src/main/resources/";
    }

    // int, int -> int
    private CtMethod constructMethod1(String name) {
        Set<ModifierKind> modifiers = Set.of(ModifierKind.PUBLIC);
        CtTypeReference<Integer> intType = new CtTypeReferenceImpl<>();
        intType.setSimpleName("int");
        CtParameter<Integer> param1 = new CtParameterImpl<>();
        CtParameter<Integer> param2 = new CtParameterImpl<>();
        param1.setSimpleName("x");
        param2.setSimpleName("y");
        param1.setType(intType);
        param2.setType(intType);
        List parameters = List.of(param1,param2);
        Set thrownTypes = Set.of();
        String body = "return x+y";
        CtStatement snippet = factory.createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = factory.createMethod((CtClass<?>)type,modifiers,intType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    public CtInvocation constructCall1(String name) {
        CtMethod method = constructMethod1(name);
        return codeFactory.createInvocation(factory.createThisAccess(type.getReference(),true),
                methodFactory.createReference(method),codeFactory.createLiteral(10),
                codeFactory.createLiteral(10));
    }

    // -> boolean
    private CtMethod constructMethod2(String name) {
        Set<ModifierKind> modifiers = Set.of(ModifierKind.PUBLIC);
        CtTypeReference<Integer> voidType = new CtTypeReferenceImpl<>();
        voidType.setSimpleName("boolean");
        List parameters = List.of();
        Set thrownTypes = Set.of();
        String body = "return true";
        CtStatement snippet = factory.createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = factory.createMethod((CtClass<?>)type,modifiers,voidType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    public CtInvocation constructCall2(String name) {
        CtMethod method = constructMethod2(name);
        return codeFactory.createInvocation(factory.createThisAccess(type.getReference(),true),
                methodFactory.createReference(method));
    }

    // char ->
    private CtMethod constructMethod3(String name) {
        Set<ModifierKind> modifiers = Set.of(ModifierKind.PUBLIC);
        CtTypeReference<Integer> voidType = new CtTypeReferenceImpl<>();
        voidType.setSimpleName("void");
        CtTypeReference<Integer> charType = new CtTypeReferenceImpl<>();
        charType.setSimpleName("char");
        CtParameter<Integer> param1 = new CtParameterImpl<>();
        param1.setSimpleName("x");
        param1.setType(charType);
        List parameters = List.of(param1);
        Set thrownTypes = Set.of();
        String body = "System.out.println(\"The char is: \" + x)";
        CtStatement snippet = factory.createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = factory.createMethod((CtClass<?>)type,modifiers,voidType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    public CtInvocation constructCall3(String name) {
        CtMethod method = constructMethod3(name);
        return codeFactory.createInvocation(factory.createThisAccess(type.getReference(),true),
                methodFactory.createReference(method),codeFactory.createLiteral('c'));
    }

    public CtInvocation constructCall4(String name) {
        CtMethod method = constructMethodX("method4");
        method.setSimpleName(name);
        return codeFactory.createInvocation(factory.createThisAccess(type.getReference(),true),
                methodFactory.createReference(method),codeFactory.createLiteral(100L));
    }

    public CtInvocation constructCall5(String name) {
        return constructCallX("method5",name);
    }

    private CtMethod constructMethodX(String name) {
        CtType t = Utils.readClass(resources+"/Methods.java","Methods");
        Set<CtMethod> methods = t.getMethods();
        CtMethod retM = null;
        for(CtMethod m : methods){
            if(m.getSimpleName().equals(name))
                retM = m;
        }
        type.addMethod(retM);
        return retM;
    }

    private CtInvocation constructCallX(String kind, String name) {
        CtType t = Utils.readClass(resources+"/Methods.java","Methods");
        CtMethod method = t.getMethod("invocations");
        CtInvocation inv = null;
        for(CtStatement m : method.getBody().getStatements()){
            inv = (CtInvocation) m;
            if(inv.getExecutable().getSimpleName().equals(kind))
                break;
        }
        inv.getExecutable().getDeclaration().setSimpleName(name);
        inv.getExecutable().setSimpleName(name);
        type.addMethod((CtMethod) inv.getExecutable().getDeclaration());
        return inv;
    }

    public void setResources(String r){
        resources = r;
    }

    public static void main(final String args[]) {
        String resources = Spoon.class.getClassLoader().getResource("").getPath();
        Constructor s = new Constructor();
        s.setResources(resources);
        System.out.println(s.constructCall5("newMethod"));
    }
}
