import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;

public abstract class AbstractTransformer {
    private CtType originalType;
    protected CtType type;

    public AbstractTransformer(String sourcePath, String className){
        originalType = getType(sourcePath,className);
        type = originalType.clone();
    }

    protected CtType getType(String sourcePath,String className){
        Launcher launcher = new Launcher();
        launcher.addInputResource(sourcePath);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        CtPackage root = model.getRootPackage();
        return root.getType(className);
    }

    protected void reset(){
        type = originalType.clone();
    }

    protected void writeClass(String destinationPath) {
        writeClass(destinationPath,"");
    }

    protected void writeClass(String destinationPath, String imports) {
        try {
            File file = new File(destinationPath);
            file.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(file, (imports + type.toString()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}