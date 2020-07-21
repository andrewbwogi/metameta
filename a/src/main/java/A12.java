public class A12 extends A11 {

    A12(){
        super(40);
    }

    @Override
    public long method(long x) {
        return x;
    }

    @Override
    public int method(int x, int y, int z) {
        return 0;
    }

    @Override
    public int newMethod(int x, int y) {
        return 0;
    }

    @Override
    public int method(String s) {
        return 5;
    }
}
