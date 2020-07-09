import java.util.ArrayList;

public class Methods {
    private int newField1_5;
    private int newField1_6;
    private int newField2_6;
    private int ooldField;


    void invocations(){
        method5(10,10);
        method6(10,10);
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
        ooldField = newField1_6 + newField2_6;
        return ooldField;
    }

    // test reference arguments
    void method7(ArrayList arrayList){
        arrayList.add(5);
    }

    // test most primitives
    long method8(long l, byte b, short s, double d, float f, boolean bo){
        if(l<10 && bo) {
            return l;
        }
        else {
            return 0;
        }
    }


}
