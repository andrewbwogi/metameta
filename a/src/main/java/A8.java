public class A8 {
    private int oldField = 3;

    public int method(int x) {
        int i = oldField;
        return x + i;
    }

    public void method() {
        int i = oldField;
        int x = 1;
    }
}