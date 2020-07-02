import org.apache.commons.io.FileUtils;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.io.IOException;

public class Utils {

    public static CtType readClass(String sourcePath, String className) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(sourcePath);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        CtPackage root = model.getRootPackage();
        return root.getType(className);
    }

    public static void writeClass(CtType type, String destinationPath) {
        writeClass(type, destinationPath, "");
    }

    public static void writeClass(CtType type, String destinationPath, String imports) {
        try {
            File file = new File(destinationPath);
            file.getParentFile().mkdirs();
            FileUtils.writeByteArrayToFile(file, (imports + type.toString()).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
