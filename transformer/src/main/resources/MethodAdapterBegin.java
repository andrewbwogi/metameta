import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends MethodVisitor {
    String className;

    public MethodAdapter(int api, MethodVisitor mv, String className,String desc,String newDesc) {
        super(api, mv);
        this.className = className;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
        call();
    }

    public void call() {
        // add call
    }


}
