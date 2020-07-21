public abstract class A11
{
    protected int oldField = 3;
    protected int oldField2 = 3;

    A11(int x){
        oldField = x;
    }

    abstract public int newMethod(int x, int y);

    public int method(int x) {
        int i = 0;
        return x + i;
    }

    public void newMethod(char x) {
        System.out.println(x);
    }

    public static boolean newMethod(boolean b) {
        return b;
    }

    public int method(String s) {
        return 100;
    }

    abstract public long method(long x);

    abstract public int method(int x, int y, int z);
}