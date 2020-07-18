import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends AdviceAdapter {
    String className;
    boolean isInner;

    public MethodAdapter(int api, org.objectweb.asm.MethodVisitor mv, int a, String name, String className, String desc, String newDesc, boolean isInner) {
        super(api, mv,a,name,desc);
        this.className = className;
        this.isInner = isInner;
    }

    @Override
    public void onMethodEnter() {
        if(!isInner) {
            mv.visitCode();
            call();
        }
    }

    public void call() {
        // add call
    }


}
