import spoon.reflect.code.CtInvocation;

public class InvocationWrapper {
    private CtInvocation inv;
    private String modifiedMethod;
    private boolean addBegin;

    public InvocationWrapper(CtInvocation inv, String modifiedMethod, boolean addBegin) {
        this.inv = inv;
        this.modifiedMethod = modifiedMethod;
        this.addBegin = addBegin;
    }

    public CtInvocation getInvocation() {
        return inv;
    }

    public String getModifiedMethod() {
        return modifiedMethod;
    }

    public boolean isAddBegin() {
        return addBegin;
    }
}
