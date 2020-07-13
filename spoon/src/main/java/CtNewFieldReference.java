import org.apache.commons.beanutils.BeanUtils;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.reflect.reference.CtFieldReferenceImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CtNewFieldReference extends CtFieldReferenceImpl {
    CtFieldReferenceImpl field;

    public CtNewFieldReference(CtFieldReferenceImpl field){
        for (Method getMethod : field.getClass().getMethods()) {
            if (getMethod.getName().startsWith("get")) {
                try {
                    Method setMethod = this.getClass().getMethod(getMethod.getName().replace("get", "set"), getMethod.getReturnType());
                    setMethod.invoke(this, getMethod.invoke(field, (Object[]) null));

                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    //not found set
                }
            }
        }
    }

    public CtFieldReference getField() {
        return field;
    }

    public String toString(){
        return field.toString();
    }
}
