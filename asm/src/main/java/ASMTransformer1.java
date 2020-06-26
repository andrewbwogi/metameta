import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

public class ASMTransformer1 {

    public void execute(String fileIn,String fileOut) throws Exception{

        // input
        FileInputStream is = new FileInputStream(fileIn);
        byte[] b;
        ClassReader cr = new ClassReader(is);
        String className = cr.getClassName();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassAdapter1 ca = new ClassAdapter1(Opcodes.ASM6,cw,className);
        cr.accept(ca, 0);

        // output
        b = cw.toByteArray();
        new File(fileOut).getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(fileOut);
        fos.write(b);
        fos.close();
    }

    public static void main(final String args[]) throws Exception {
        ASMTransformer1 a = new ASMTransformer1();
        a.execute("/home/andrewb/Desktop/spoon-to-asm/metameta/asm/src/main/resources/ModelTest.class",
                "/home/andrewb/Desktop/spoon-to-asm/metameta/asm/src/main/resources/trans/ModelTest.class");
    }
}
