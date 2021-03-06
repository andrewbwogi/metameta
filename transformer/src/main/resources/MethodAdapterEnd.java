import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends MethodVisitor {
    String className;
    String desc;
    String newDesc;
    boolean isInner;

    public MethodAdapter(int api, org.objectweb.asm.MethodVisitor mv, int a, String name, String className, String desc, String newDesc, boolean isInner) {
        super(api, mv);
        this.className = className;
        this.desc = desc;
        this.newDesc = newDesc;
        this.isInner = isInner;
    }

    @java.lang.Override
    public void visitInsn(int opcode) {
        int last1 = desc.lastIndexOf(")");
        int last2 = newDesc.lastIndexOf(")");
        if ((((opcode >= (IRETURN)) && (opcode <= (RETURN))))
                && (desc.substring(last1).equals(newDesc.substring(last2))) && !isInner) {
            call();
        }
        mv.visitInsn(opcode);
    }

    public void call() {
        // add call
    }


}
