import spoon.reflect.code.CtInvocation;

import java.lang.reflect.Method;

public class Main {
    static int INVOCATIONS;
    static int FROM;

    public static void main(String[] args) throws Exception {
        FROM = Integer.parseInt(args[2]);
        INVOCATIONS = Integer.parseInt(args[3]);
        Spoon spoon;
        Transformer transformer = new Transformer();
        String modMethod = "method";
        for (int i = FROM; i <= INVOCATIONS; i++) {
            spoon = new Spoon();
            Method method = spoon.getClass().getMethod("getInvocation", Integer.class, String.class, Boolean.class);
            InvocationWrapper inv = (InvocationWrapper) method.invoke(spoon, i, modMethod, true);
            transformer.add(inv,i);

            spoon = new Spoon();
            inv = (InvocationWrapper) method.invoke(spoon, i, modMethod, false);
            transformer.add(inv,i);
        }
    }
}
