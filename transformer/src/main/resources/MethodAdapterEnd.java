import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends MethodVisitor {
    String className;

    public MethodAdapter(int api, MethodVisitor mv, String className) {
        super(api, mv);
        this.className = className;
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            call();
        }
        mv.visitInsn(opcode);
    }

    public void call() {
        // add call
    }


}
