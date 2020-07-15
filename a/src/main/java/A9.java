import java.util.Arrays;
import java.util.List;

public class A9
{
    public String method() {
        return "World";
    }

    public List method(int x) {
        return Arrays.asList(1,x);
    }

    public String[][] method(int x,int y) {
        return new String[][]{{"World"},{"Hello"}};
    }


}