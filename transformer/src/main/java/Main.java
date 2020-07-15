import spoon.reflect.code.CtInvocation;

import java.lang.reflect.Method;

public class Main {
    static int INVOCATIONS;
    static int FROM;

    public static void main(String[] args) throws Exception {
        FROM = Integer.parseInt(args[2]);
        INVOCATIONS = Integer.parseInt(args[3]);
        Constructor constructor;
        Transformer transformer = new Transformer();
        String modMethod = "method";
        for (int i = FROM; i <= INVOCATIONS; i++) {
            constructor = new Constructor();
            Method method = constructor.getClass().getMethod("constructCall" + i, String.class);
            CtInvocation inv = (CtInvocation) method.invoke(constructor, "newMethod");
            if(i == 13)
                modMethod = "A3";
            transformer.addBegin(inv, "Begin" + i, modMethod);
            transformer.addEnd(inv, "End" + i, modMethod);
        }
    }
}
