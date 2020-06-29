import spoon.reflect.code.CtInvocation;

import java.lang.reflect.Method;

public class Main {
    final static int INVOCATIONS = 3;

    public static void main(String[] args) throws Exception {
        Constructor constructor = new Constructor();
        Transformer transformer = new Transformer();
        for (int i = 1; i <= INVOCATIONS; i++) {
            Method method = constructor.getClass().getMethod("constructCall" + i, String.class);
            CtInvocation inv = (CtInvocation) method.invoke(constructor, "newMethod");
            transformer.addBegin(inv, "Begin" + i, "method");
            transformer.addEnd(inv, "End" + i, "method");
        }
    }
}
