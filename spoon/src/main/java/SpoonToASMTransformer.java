import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.io.File;
import java.util.List;

public class SpoonToASMTransformer extends AbstractTransformer {

    SpoonToASMTransformer(String inputPath, String className) {
        super(inputPath,className);
    }

    void addBegin(CtMethod method, List arguments) {}

    void addEnd(CtMethod method) {}

    void addField(CtField field) {}

    CtType getASMProgram() {
        return type;
    }

    private String getImportStatements(){
        return "import org.objectweb.asm.ClassReader;\n" +
                "import org.objectweb.asm.ClassWriter;\n" +
                "import org.objectweb.asm.tree.*;\n" +
                "import org.objectweb.asm.*;\n" +
                "import java.io.File;\n" +
                "import java.io.FileInputStream;\n" +
                "import java.io.FileOutputStream;\n\n";
    }

    public static void main(final String args[]) throws Exception {
        String resources = new File("src/main/resources").getAbsolutePath();
        SpoonToASMTransformer transformer = new SpoonToASMTransformer(resources + "/BaseClass.java","BaseClass");
        transformer.writeClass(resources+"/ASMTransformer.java");
    }
    }