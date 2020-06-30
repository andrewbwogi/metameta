import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends MethodVisitor {
    String className;
    String desc;
    String newDesc;

    public MethodAdapter(int api, MethodVisitor mv, String className,String desc,String newDesc) {
        super(api, mv);
        this.className = className;
        this.desc = desc;
        this.newDesc = newDesc;
    }

    @java.lang.Override
    public void visitInsn(int opcode) {
        int last1 = desc.lastIndexOf(")");
        int last2 = newDesc.lastIndexOf(")");
        if ((((opcode >= (IRETURN)) && (opcode <= (RETURN))) || (opcode == (ATHROW)))
                && (desc.substring(last1).equals(newDesc.substring(last2)))) {
            call();
        }
        mv.visitInsn(opcode);
    }

    public void call() {
        // add call
    }


}
