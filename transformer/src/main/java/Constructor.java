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
import spoon.support.reflect.declaration.CtClassImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.List;
import java.util.Set;

public class Constructor {
    Factory factory;
    CtType type;
    
    public Constructor(CtType type){
        this.factory = type.getFactory();
        this.type = type;
    }

    public Constructor(){
        type = Launcher.parseClass("class A {}");
        this.factory = type.getFactory();
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
        CtStatement snippet = factory.createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = factory.createMethod((CtClass<?>)type,modifiers,intType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }

    public CtInvocation constructCall1(String name) {
        CodeFactory codeFactory = factory.Code();
        MethodFactory methodFactory = factory.Method();
        CtMethod method = constructMethod1(name);
        return codeFactory.createInvocation(factory.createThisAccess(type.getReference(),true),
                methodFactory.createReference(method),codeFactory.createLiteral(10),
                codeFactory.createLiteral(10));
    }

    // -> boolean
    public CtMethod constructMethod2(String name) {
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
        CtStatement snippet = factory.createCodeSnippetStatement(body);
        CtBlock block = new CtBlockImpl();
        block.insertEnd(snippet);
        CtMethod newMethod = factory.createMethod((CtClass<?>)type,modifiers,voidType,
                name,parameters,thrownTypes,block);
        return newMethod;
    }
}
