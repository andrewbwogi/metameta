import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IRETURN;

public class ClassAdapter2 extends ClassVisitor {
    String className;
    String modMethodName = "method";
    int acc = ACC_PUBLIC;
    String newMethodName = "newMethod";
    String desc = "(C)V";
    String sig = null;
    String[] exc = null;

    public ClassAdapter2(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int a, String name, String d, String s, String[] e) {
        MethodVisitor mv = super.visitMethod(a, name, d, s, e);
        MethodAdapter2 ma = new MethodAdapter2(Opcodes.ASM5, mv, newMethodName, desc, className);
        if (name.equals(modMethodName)) {
            return ma;
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(acc, newMethodName, desc, sig, exc);
        if (mv != null) {
            definition(mv);
            mv.visitEnd();
        }
        cv.visitEnd();
    }

    private void definition(MethodVisitor mv) {
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
}