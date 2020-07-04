import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import static org.objectweb.asm.Opcodes.*;

public class ClassAdapter extends ClassVisitor {
    String className;

    // replace literals with correct values
    String modifiedMethod = "modifiedMethod";
    String newMethod = "newMethod";
    String desc = "desc";

    public ClassAdapter(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int a, String name, String d, String s, String[] e) {

        // remove abstract method if added method has same signature
        if ((name.equals(newMethod)) && desc.equals(d) && ((a & (ACC_ABSTRACT)) == (ACC_ABSTRACT)))
            return null;
        MethodVisitor mv = super.visitMethod(a, name, d, s, e);
        MethodAdapter ma = new MethodAdapter(Opcodes.ASM5, mv, className,d,desc);

        // don't add calls to static methods
        if (name.equals(modifiedMethod) && ((a & ACC_STATIC) != ACC_STATIC)) {
            return ma;
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, newMethod, desc, null, null);
        if (mv != null) {
            definition(mv);
            mv.visitEnd();
        }
        cv.visitEnd();
    }

    private void definition(MethodVisitor methodVisitor) {
        // add definition
    }
}