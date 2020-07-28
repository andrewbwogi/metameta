public class A4<T> {

    // inner class tests

    public T method(){
        T t = null;
        return t;
    }

    public int method(int x) {
        Inner i = new Inner() {
            @Override
            public int method() {
                return 3;
            }
        };
        return x + i.method();
    }

    public int method(int x,T y) {
        class Local {

            int method() {
                return 20;
            }
        }
        Local local = new Local();
        return local.method();
    }

    public class Inner {
        int method(){
            int x = A4.StaticNested.method();
            StaticNested n = new StaticNested();
            x += n.method(5);
            return x;
        }
    }

    public static class StaticNested {

        private static int method() {
            return 30;
        }

        private int method(int x) {
            return x;
        }
    }
}
