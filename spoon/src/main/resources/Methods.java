import java.util.ArrayList;

public class Methods {
    private int newField1_5;
    private int newField1_6;
    private int newField2_6;
    private int oldField;
    private ArrayList<Integer> newField1_7;
    private ArrayList<Integer> newField1_8;

    void invocations(){
        method5(10,10);
        method6(10,10);
        method7();
        method8();
        method9("Hello");
        method10();
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
