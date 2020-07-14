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

    // test most primitives
    long method9(long l, byte b, short s, double d, float f, boolean bo){
        if(l<10 && bo) {
            return l;
        }
        else {
            return 0;
        }
    }


}
