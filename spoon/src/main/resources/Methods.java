import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Methods {
    private int newField1_5;
    private int newField1_6;
    private int newField2_6;
    private int oldField;
    public int oldField2;
    private ArrayList<Integer> newField1_7;
    private ArrayList<Integer> newField1_8;
    private String[][] newField1_12;
    private int[] newField2_12;
    public String oldField3;

    void invocations(){
        method5(10,10);
        method6(10,10);
        method7();
        method8();
        method9("Hello");
        method10();
        method11(Arrays.asList(1,2));
        method12(new Integer[]{1, 1, 1, 1, 1});
        method13(10);
    }

    // new way to construct spoon metaprogram
    public long method4(long l){
        if(l<10) {
            return l;
        }
        else {
            return 0;
        }
    }

    // test new field
    public int method5(int x, int y){
        newField1_5 = x;
        return 0;
    }

    // test new field
    public int method6(int x, int y){
        newField1_6 = x;
        newField2_6 = y;
        oldField = newField1_6 + newField2_6;
        oldField2 = oldField;
        return oldField;
    }

    // test reference arguments
    public void method7(){
        newField1_7 = new ArrayList<>();
        newField1_7.add(5);
    }

    public int method8(){
        newField1_8 = new ArrayList<>();
        newField1_8.add(5);
        return newField1_8.get(0);
    }

    public int method9(String s){
        return 10;
    }

    public String method10(){
        return "Hello";
    }

    public List method11(List<Integer> a){
        return a;
    }

    public String[][] method12(Integer[] i){
        newField1_12 = new String[][]{{"Hello" + i[0]},{"World"}};
        newField2_12 = new int[2];
        newField2_12[0] = i[0];
        oldField3 = "Hello";
        return newField1_12;
    }

    public int method13(int k){
        if (k > 0) {
            return k + newMethod(k - 1);
        } else {
            return 0;
        }
    }

    // test most primitives
    public long method20(long l, byte b, short s, double d, float f, boolean bo){
        if(l<10 && bo) {
            return l;
        }
        else {
            return 0;
        }
    }
}