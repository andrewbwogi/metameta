import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import java.util.Set;

/**
 * Adds a method call in the beginning of all methods of a class.
 */
 public class Spoon extends AbstractTransformer {
    private Constructor constructor;

    public Spoon(String sourcePath, String className) {
        super(sourcePath,className);
        this.constructor = new Constructor(type);
    }

    public void addMethod1() {
        CtInvocation call = constructor.constructCall1("newMethod");
        addCall(call);
    }

    public void addCall(CtInvocation call) {
        Set<CtMethod> set = type.getMethods();
        for(CtMethod m : set){
            if(m.getBody() != null && m.getSimpleName().equals("method"))
                m.getBody().insertBegin(call.clone());
        }
    }

    public void addMethod2(String methodName, String arg) {
        addCall(methodName,arg);
        constructor.constructMethod2(methodName);
    }

    public void addMethod3(String methodName, String arg) {
        addCall(methodName,arg);
        constructor.constructMethod3(methodName);
    }

    public void addCall(String methodName, String arg) {
        String call = methodName + arg;
        CtCodeSnippetStatement snippet = type.getFactory().createCodeSnippetStatement(call);
        Set<CtMethod> set = type.getMethods();
        for(CtMethod m : set){
            if(m.getBody() != null)
                m.getBody().insertBegin(snippet.clone());
        }
    }

    public void execute1(String outputPath){
        addMethod1();
        writeClass(outputPath);
        reset();
    }
}