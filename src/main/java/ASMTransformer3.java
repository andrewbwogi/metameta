import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

public class ASMTransformer3 implements Opcodes {

    /**
     * adds the method constructed by SpoonTransformers.constructMethod3
     * in the end of each method except constructors in a class
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
            il.add(new IntInsnNode(BIPUSH,99));
            il.add(new MethodInsnNode(INVOKEVIRTUAL,"ModelTest","method3","(C)V",false));
            InsnList insns = mn.instructions;
            AbstractInsnNode last = insns.getLast();
            insns.insertBefore(last,il);
        }

        // add the method code. generated by ASMifier
        MethodVisitor mv;
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        {
            mv = cw.visitMethod(ACC_PUBLIC, "method3", "(C)V", null, null);
            mv.visitCode();
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
            mv.visitLdcInsn("The char is: ");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
            mv.visitVarInsn(ILOAD, 1);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(3, 2);
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
        ASMTransformer3 c = new ASMTransformer3();
        addMethod("./ModelTest.class","./ModelTest.class");
    }
}
