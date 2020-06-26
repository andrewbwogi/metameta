import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter1 extends MethodVisitor {
    String methodName;
    String desc;
    String className;

    public MethodAdapter1(int api, MethodVisitor mv, String methodName, String desc, String className) {
        super(api, mv);
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        call();
    }

    public void call() {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitIntInsn(BIPUSH, 10);
        mv.visitIntInsn(BIPUSH, 10);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, methodName, desc, false);
        mv.visitInsn(POP);
    }


}
