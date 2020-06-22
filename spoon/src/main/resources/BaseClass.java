import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class BaseClass implements Opcodes {
    ClassNode cn;
    ClassReader cr;
    ClassWriter cw;

    public void execute(String fileIn, String fileOut) throws Exception {
        readClass(fileIn);
        for (MethodNode mn : cn.methods) {
            if(!target(mn))
                continue;
            InsnList il = new InsnList();
            call(il);
            position(mn,il);
        }
        cn.accept(cw);
        definition();
        writeClass(fileOut);
    }

    private void readClass(String fileIn) throws Exception {
        FileInputStream is = new FileInputStream(fileIn);
        cn = new ClassNode();
        cr = new ClassReader(is);
        cr.accept(cn, 0);
        cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
    }

    private void writeClass(String fileOut) throws Exception {
        byte[] b = cw.toByteArray();
        new File(fileOut).getParentFile().mkdirs();
        FileOutputStream fos = new FileOutputStream(fileOut);
        fos.write(b);
        fos.close();
    }

    private boolean target(MethodNode mn){ return true; }

    private void call(InsnList il){}

    private void position(MethodNode mn, InsnList il){}

    private void definition(){}
}