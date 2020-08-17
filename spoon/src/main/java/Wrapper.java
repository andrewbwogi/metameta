import spoon.reflect.code.CtInvocation;

public class Wrapper {
    private CtInvocation inv;
    private String modifiedMethod;
    private boolean addBegin;

    public Wrapper(CtInvocation inv, String modifiedMethod, boolean addBegin) {
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
