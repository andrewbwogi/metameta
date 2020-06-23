import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

public class ASMTransformer2 implements Opcodes {

    /**
     * adds the method constructed by SpoonTransformers.constructMethod2
     * in the beginning of each method except constructors in a class
     * @param fileIn
     * @param fileOut
     * @throws Exception
     */
    public static void addMethod(String fileIn,String fileOut)  throws Exception{

        // the input
        FileInputStream is = new FileInputStream(fileIn);
        ClassNode cn = new ClassNode();
        ClassReader cr = new ClassReader(is);
        cr.accept(cn, 0);

        // add the method call to each method except constructors
        for (MethodNode mn : cn.methods) {
            if(mn.name.equals("<init>"))
                continue;

            InsnList il = new InsnList();
            il.add(new VarInsnNode(ALOAD,0));
            il.add(new MethodInsnNode(INVOKEVIRTUAL,"ModelTest","method2","()Z",false));
            il.add(new InsnNode(POP));
            InsnList insns = mn.instructions;
            AbstractInsnNode in = insns.getFirst();
            insns.insertBefore(in,il);
        }

        // add the method code. generated by ASMifier
        MethodVisitor mv;
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method2", "()Z", null, null);
            mv.visitCode();
            mv.visitInsn(ICONST_1);
            mv.visitInsn(IRETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        cw.visitEnd();

        // the output
        byte[] b = cw.toByteArray();
        FileOutputStream fos = new FileOutputStream(fileOut);
        fos.write(b);
        fos.close();
    }

    public static void main(String[] args) throws Exception {
        ASMTransformer2 c = new ASMTransformer2();
        addMethod("./ModelTest.class","./ModelTest.class");
    }
}
