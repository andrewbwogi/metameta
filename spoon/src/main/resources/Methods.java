import java.util.ArrayList;

public class Methods {

    void invocations(){
        method5(new ArrayList());
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

    // test reference arguments
    void method5(ArrayList arrayList){
        arrayList.add(5);
    }

    // test most primitives
    long method6(long l, byte b, short s, double d, float f, boolean bo){
        if(l<10 && bo) {
            return l;
        }
        else {
            return 0;
        }
    }
}
