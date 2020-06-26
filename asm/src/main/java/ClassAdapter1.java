import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.IRETURN;

public class ClassAdapter1 extends ClassVisitor {
    String className;
    String modMethodName = "method";
    int acc = ACC_PUBLIC;
    String newMethodName = "newMethod";
    String desc = "(II)I";
    String sig = null;
    String[] exc = null;

    public ClassAdapter1(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int a, String name, String d, String s, String[] e) {
        MethodVisitor mv = super.visitMethod(a, name, d, s, e);
        MethodAdapter1 ma = new MethodAdapter1(Opcodes.ASM5, mv, newMethodName, desc, className);
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
        mv.visitVarInsn(ILOAD, 1);
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IADD);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(1, 3);
        mv.visitEnd();
    }
}