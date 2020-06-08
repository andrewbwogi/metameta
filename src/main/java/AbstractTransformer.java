import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.declaration.CtClassImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class AbstractTransformer {
    CtType type;
    CtPackage root;

    AbstractTransformer(String sourcePath, String className){
        Launcher launcher = new Launcher();
        launcher.addInputResource(sourcePath);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        root = model.getRootPackage();
        type = model.getRootPackage().getType(className);
    }

    AbstractTransformer(String className){
        constructBaseType(className);
    }

    private void constructBaseType(String className) {
        type = new CtClassImpl();
    }

    void writeClass(String targetPath) {
        try {
            Files.write(Paths.get(targetPath), type.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}