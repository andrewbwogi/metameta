import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.CodeFactory;
import spoon.support.reflect.code.CtReturnImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Set;

/**
 * Adds a method call in the beginning of all methods of a class.
 */
public class Spoon {
    private Constructor constructor;
    private CtType originalType;
    private CtType type;

    public Spoon(String sourcePath, String className) {
        originalType = Utils.readClass(sourcePath,className);
        type = originalType.clone();
        this.constructor = new Constructor(type);
    }

    public void addBegin(String outputPath, Integer methodKind) {
        addCallBegin(getInvocation(methodKind),outputPath,"method");
    }

    public void addEnd(String outputPath, Integer methodKind) {
        addCallEnd(getInvocation(methodKind),outputPath,"method");
    }

    private CtInvocation getInvocation(int methodKind){
        CtInvocation inv = null;
        try {
            Method method = constructor.getClass().getMethod("constructCall" + methodKind, String.class);
            inv = (CtInvocation) method.invoke(constructor, "newMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inv;
    }

    private void addCallBegin(CtInvocation call,String outputPath,String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod))
                m.getBody().insertBegin(call.clone());
        }
        Utils.writeClass(type,outputPath);
        reset();
    }

    private void addCallEnd(CtInvocation call,String outputPath,String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod)) {
                if(m.getBody().getLastStatement().getClass() == CtReturnImpl.class) {
                    CtReturn ret = m.getBody().getLastStatement();
                    CtExpression retExpr = ret.getReturnedExpression();

                    // compare types
                    if(call.getType().getSimpleName().equals(m.getType().getSimpleName())){

                        // insert new local variable
                        CodeFactory codeFactory = type.getFactory().Code();
                        CtLocalVariable var = codeFactory.createLocalVariable(retExpr.getType(),"newLocal",retExpr);
                        ret.insertBefore(var);

                        // replace return expression
                        ret.setReturnedExpression(call.clone());
                    }
                }
                else{
                    if(call.getType().getSimpleName().equals(m.getType().getSimpleName()))
                        m.getBody().insertEnd(call.clone());
                }
            }
        }
        Utils.writeClass(type,outputPath);
        reset();
    }

    private void reset(){
        type = originalType.clone();
        this.constructor = new Constructor(type);
    }

    public void setResources(String r){
        constructor.setResources(r);
    }

    public static void main(final String args[]) {
        String resources = Spoon.class.getClassLoader().getResource("").getPath();
        Spoon s = new Spoon(resources+"/A1.java", "A1");
        s.setResources(resources);
        s.addEnd(resources+"/mod/A1.java",4);
    }
}