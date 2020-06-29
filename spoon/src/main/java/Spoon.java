import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.support.reflect.code.CtReturnImpl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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

    public void addBegin1(String outputPath) {
        addCallBegin(constructor.constructCall1("newMethod"),outputPath,"method");
    }

    public void addEnd1(String outputPath) {
        addCallEnd(constructor.constructCall1("newMethod"),outputPath,"method");
    }

    public void addBegin(String outputPath, Integer methodKind) {
        CtInvocation inv = null;
        try {
            Method method = constructor.getClass().getMethod("constructCall" + methodKind, String.class);
            inv = (CtInvocation) method.invoke(constructor, "newMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }
        addCallBegin(inv,outputPath,"method");
    }

    public void addEnd(String outputPath, Integer methodKind) {
        CtInvocation inv = null;
        try {
            Method method = constructor.getClass().getMethod("constructCall" + methodKind, String.class);
            inv = (CtInvocation) method.invoke(constructor, "newMethod");
        } catch (Exception e) {
            e.printStackTrace();
        }
        addCallEnd(inv,outputPath,"method");
    }

    private void addCallBegin(CtInvocation call,String outputPath,String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod))
                m.getBody().insertBegin(call.clone());
        }
        writeClass(outputPath);
        reset();
    }

    private void addCallEnd(CtInvocation call,String outputPath,String modifiedMethod) {
        Set<CtMethod> set = type.getMethods();
        for (CtMethod m : set) {
            if (m.getBody() != null && m.getSimpleName().equals(modifiedMethod)) {
                if(m.getBody().getLastStatement().getClass() != CtReturnImpl.class)
                    m.getBody().insertEnd(call.clone());
                else
                    m.getBody().getLastStatement().insertBefore(call.clone());
            }
        }
        writeClass(outputPath);
        reset();
    }

    private CtType readClass(String sourcePath,String className){
        Launcher launcher = new Launcher();
        launcher.addInputResource(sourcePath);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        CtPackage root = model.getRootPackage();
        return root.getType(className);
    }

    private void writeClass(String destinationPath) {
        try {
            File file = new File(destinationPath);
            file.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(file, type.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void reset(){
        type = originalType.clone();
        this.constructor = new Constructor(type);
    }

}