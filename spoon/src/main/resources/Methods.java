import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Methods {
    private int newField1_5;
    private int newField1_6;
    private int newField2_6;
    private int oldField;
    private ArrayList<Integer> newField1_7;
    private ArrayList<Integer> newField1_8;
    private String[][] newField1_12;
    private int[] newField2_12;
    private A10.Inner newField1_14;
    private A10.StaticNested newField2_14;
    private A10.Inner oldfieldInner;
    private A10.StaticNested oldfieldStatic;

    void invocations(){
        method5(10,10);
        method6(10,10);
        method7();
        method8();
        method9("Hello");
        method10();
        method11(Arrays.asList(1,2));
        method12(new Integer[]{1, 1, 1, 1, 1});
        method13(1,2);
        //method14(new A10().new Inner(), new A10.StaticNested());
        method14(new A10().new Inner());
    }

    // new way to construct spoon metaprogram
    long method4(long l){
        if(l<10) {
            return l;
        }
        else {
            return 0;
        }
    }

    // test new field
    int method5(int x, int y){
        newField1_5 = x;
        return 0;
    }

    // test new field
    int method6(int x, int y){
        newField1_6 = x;
        newField2_6 = y;
        oldField = newField1_6 + newField2_6;
        return oldField;
    }

    // test reference arguments
    void method7(){
        newField1_7 = new ArrayList<>();
        newField1_7.add(5);
    }

    int method8(){
        newField1_8 = new ArrayList<>();
        newField1_8.add(5);
        return newField1_8.get(0);
    }

    int method9(String s){
        return 10;
    }

    String method10(){
        return "Hello";
    }

    List method11(List<Integer> a){
        return a;
    }

    String[][] method12(Integer[] i){
        newField1_12 = new String[][]{{"Hello" + i[0]},{"World"}};
        newField2_12 = new int[2];
        newField2_12[0] = i[0];
        return newField1_12;
    }

    int method13(int x, int y){
        return x+y;
    }

    /*
    A10.Inner method14(A10.Inner x, A10.StaticNested y){
        newField1_14 = x;
        newField2_14 = y;
        oldfieldInner = newField1_14;
        oldfieldStatic = newField2_14;
        return oldfieldInner;
    }
*/

    A10.Inner method14(A10.Inner x){
        newField1_14 = null;
        newField2_14 = null;
        oldfieldInner = null;
        oldfieldStatic = null;
        return oldfieldInner;
    }

    // test most primitives
    long method20(long l, byte b, short s, double d, float f, boolean bo){
        if(l<10 && bo) {
            return l;
        }
        else {
            return 0;
        }
    }
}
