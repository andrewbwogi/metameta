import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * Adds a method call in the beginning of all methods of a class.
 */
 public class Spoon {
    private Constructor constructor;
    private CtType originalType;
    protected CtType type;

    public Spoon(String sourcePath, String className) {
        originalType = readClass(sourcePath,className);
        type = originalType.clone();
        this.constructor = new Constructor(type);
    }

    public void execute1(String outputPath) {
        execute(constructor.constructCall1("newMethod"),outputPath,"method");
    }

    public void execute2(String methodName, String arg) {
        constructor.constructMethod2(methodName);
    }

    public void execute3(String methodName, String arg) {
        constructor.constructMethod3(methodName);
    }

    public void execute(CtInvocation call,String outputPath,String modifiedMethod) {
        addCall(call,modifiedMethod);
        writeClass(outputPath);
        reset();
    }

    public void addCall(CtInvocation call,String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod))
                m.getBody().insertBegin(call.clone());
        }
    }

    protected CtType readClass(String sourcePath,String className){
        Launcher launcher = new Launcher();
        launcher.addInputResource(sourcePath);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        CtPackage root = model.getRootPackage();
        return root.getType(className);
    }

    protected void writeClass(String destinationPath) {
        try {
            File file = new File(destinationPath);
            file.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(file, type.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void reset(){
        type = originalType.clone();
    }

}