public class A6 extends A5 {

    // inheritance tests
    A6(){
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
