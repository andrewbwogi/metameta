import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ASMTransformer {

    public void execute(String fileIn,String fileOut) throws Exception{

        // input
        FileInputStream is = new FileInputStream(fileIn);
        byte[] b;
        ClassReader cr = new ClassReader(is);
        String className = cr.getClassName();
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        ClassAdapter ca = new ClassAdapter(Opcodes.ASM6,cw,className);
        cr.accept(ca, 0);

        // output
        b = cw.toByteArray();
        new File(fileOut).getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(fileOut);
        fos.write(b);
        fos.close();
    }
}
