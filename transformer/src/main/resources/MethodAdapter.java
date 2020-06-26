import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodAdapter extends MethodVisitor {
    String className;

    public MethodAdapter(int api, MethodVisitor mv, String className) {
        super(api, mv);
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
    }

    // add position of call

    public void call() {
        // add call
    }


}
