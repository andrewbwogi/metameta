
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import java.util.List;
import java.util.Set;

/**
 * Adds a method call in the beginning of all methods of a class.
 */
 public class SpoonTransformers extends AbstractTransformer {
    private String className;

    public SpoonTransformers(String sourcePath, String className) {
        super(sourcePath,className);
        this.className = className;
    }

    public void addMethod1(String methodName, String arg) {
        addCall(methodName,arg);
        constructMethod1(methodName);
    }

    public void addMethod2(String methodName, String arg) {
        addCall(methodName,arg);
        constructMethod2(methodName);
    }

    public void addMethod3(String methodName, String arg) {
        addCall(methodName,arg);
        constructMethod3(methodName);
    }

    public void addCall(String methodName, String arg) {
        String call = methodName + arg;
        CtCodeSnippetStatement snippet = type.getFactory().createCodeSnippetStatement(call);
        //List l = type.getMethodsByName("main");
        //if(!l.isEmpty())
            //System.out.println(l.get(0));
        Set<CtMethod> set = type.getMethods();
        for(CtMethod m : set){
            System.out.println(m);
            m.getBody().insertBegin(snippet.clone());
        }
        System.out.println(type);
    }

    // int, int -> int
    public CtMethod constructMethod1(String name) {
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
        CtStatement snippet = type.getFactory().createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = type.getFactory().createMethod((CtClass<?>)type,modifiers,intType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    // -> boolean
    public CtMethod constructMethod2(String name) {
        Set<ModifierKind> modifiers = Set.of(ModifierKind.PUBLIC);
        CtTypeReference<Integer> voidType = new CtTypeReferenceImpl<>();
        voidType.setSimpleName("boolean");
        List parameters = List.of();
        Set thrownTypes = Set.of();
        String body = "return true";
        CtStatement snippet = type.getFactory().createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = type.getFactory().createMethod((CtClass<?>)type,modifiers,voidType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    // char ->
    public CtMethod constructMethod3(String name) {
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
        CtStatement snippet = type.getFactory().createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = type.getFactory().createMethod((CtClass<?>)type,modifiers,voidType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    public List<CtExpression<?>> constructArguments(String name) {
        return List.of();
    }
        public void execute1(String outputPath){
        addMethod1("method1","(10,10)");
        writeClass(outputPath);
        reset();
    }
}