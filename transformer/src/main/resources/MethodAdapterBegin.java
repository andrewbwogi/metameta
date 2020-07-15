import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends AdviceAdapter {
    String className;

    public MethodAdapter(int api, org.objectweb.asm.MethodVisitor mv, int a, String name, String className, String desc, String newDesc) {
        super(api, mv,a,name,desc);
        this.className = className;
    }

    @Override
    public void onMethodEnter() {
        mv.visitCode();
        call();
    }

    public void call() {
        // add call
    }


}
