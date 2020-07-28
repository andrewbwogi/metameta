import java.util.Arrays;
import java.util.List;

public class A3 {

    // field and reference tests
    private int oldField = 3;
    private int oldField2;
    private String oldField3;

    public String method(String s) {
        return s;
    }

    public List method(int x, int y) {
        return Arrays.asList(1,x,y);
    }

    public String[][] method(int x,String y) {
        oldField3 += "World" + x;
        return new String[][]{{oldField3},{y}};
    }

    public int method(int x) {
        int i = oldField;
        return x + i;
    }

    public void method() {
        int i = oldField;
        int x = 1;
    }
}