public class Empty {
    public Empty() {
        newMethod(100L);
    }

    public long newMethod(long l) {
        if (l < 10) {
            return l;
        }else {
            return 0;
        }
    }
}