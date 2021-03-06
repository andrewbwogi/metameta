import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.support.reflect.declaration.CtMethodImpl;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Adds a method call in the beginning of all methods of a class.
 */
public class Spoon {
    private Constructor constructor;
    private CtType originalType;
    private CtType type;

    public Spoon(String sourcePath, String className) {
        originalType = Utils.readClass(sourcePath, className);
        type = originalType.clone();
        this.constructor = new Constructor(type);
    }

    public Spoon() {
        this.constructor = new Constructor();
    }

    public void addBegin(String outputPath, Integer methodKind) {
        String modMethod = "method";
        addCallBegin(getInvocation(methodKind), outputPath, modMethod);
    }

    public void addEnd(String outputPath, Integer methodKind) {
        String modMethod = "method";
        addCallEnd(getInvocation(methodKind), outputPath, modMethod);
    }

    private CtInvocation getInvocation(int methodKind) {
        CtInvocation inv = null;
        try {
            Method method = constructor.getClass().getMethod("constructCall" + methodKind, String.class);
            inv = (CtInvocation) method.invoke(constructor, "newMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inv;
    }

    private void addCallBegin(CtInvocation call, String outputPath, String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {

            // is method static?
            boolean isStatic = false;
            Set<ModifierKind> modifierSet = m.getModifiers();
            for(ModifierKind mk : modifierSet){
                if(mk == ModifierKind.STATIC)
                    isStatic = true;
            }

            // compare names and modifier
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod) && !isStatic)
                m.getBody().insertBegin(call.clone());
        }
        Utils.writeClass(type, outputPath);
        reset();
    }

    private void addCallEnd(CtInvocation call, String outputPath, String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {

            // is method static?
            boolean isStatic = false;
            Set<ModifierKind> modifierSet = m.getModifiers();
            for(ModifierKind mk : modifierSet){
                if(mk == ModifierKind.STATIC)
                    isStatic = true;
            }

            // compare return types, names and modifier
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod) &&
                    call.getType().getSimpleName().equals(m.getType().getSimpleName()) && !isStatic) {

                // get all return expressions
                if (!m.getType().getSimpleName().equals("void")) {
                    List<CtReturn> returnSet = m.filterChildren((CtReturn t) -> true).list();
                    
                    // don't insert in inner classes
                    int i = 0;
                    for (CtReturn ret : returnSet) {
                        CtElement parent = ret.getParent();
                        while(parent.getClass() != CtMethodImpl.class)
                            parent = parent.getParent();
                        if(!((CtMethod)parent).getDeclaringType().isTopLevel())
                            continue;
                        CtExpression retExpr = ret.getReturnedExpression();

                        // insert new local variable
                        CodeFactory codeFactory = type.getFactory().Code();
                        CtLocalVariable var = codeFactory.createLocalVariable(retExpr.getType(), "newLocal"+i, retExpr);
                        ret.insertBefore(var);

                        // replace return expression
                        ret.setReturnedExpression(call.clone());
                        i++;
                    }
                }

                // if void, don't do anything
                else {
                }
            }
        }
        Utils.writeClass(type, outputPath);
        reset();
    }

    private void reset() {
        type = originalType.clone();
        this.constructor = new Constructor(type);
    }

    public void setResources(String r) {
        constructor.setResources(r);
    }

    public Wrapper getWrapper(Integer kind, String modifiedMethod, Boolean addBegin) throws Exception {
        Method method = constructor.getClass().getMethod("constructCall" + kind, String.class);
        CtInvocation inv = (CtInvocation) method.invoke(constructor, "newMethod");
        return new Wrapper(inv,modifiedMethod,addBegin);
    }

    public static void main(final String args[]) {
        String resources = Spoon.class.getClassLoader().getResource("").getPath();
        Spoon s = new Spoon(resources + "/A10.java", "A10");
        s.setResources(resources);
        s.addBegin(resources + "/mod/A10.java", 1);
    }
}