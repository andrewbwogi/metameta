import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter2 extends MethodVisitor {
    String methodName;
    String desc;
    String className;

    public MethodAdapter2(int api, MethodVisitor mv, String methodName, String desc, String className) {
        super(api, mv);
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            call();
        }
        mv.visitInsn(opcode);
    }

    public void call() {
        mv.visitVarInsn(ALOAD, 0);
        mv.visitIntInsn(BIPUSH, 99);
        mv.visitMethodInsn(INVOKEVIRTUAL, className, methodName, desc, false);
    }
}
