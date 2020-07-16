import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.objectweb.asm.Opcodes.*;

public class ClassAdapter extends ClassVisitor {
    String className;

    // replace literals with correct values
    String modifiedMethod = "modifiedMethod";
    String newMethod = "newMethod";
    String desc = "desc";
    ArrayList<String> fieldNames = new ArrayList();

    public ClassAdapter(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int a, String name, String d, String s, String[] e) {

        // remove abstract method if added method has same signature
        if ((name.equals(newMethod)) && desc.equals(d) && ((a & (ACC_ABSTRACT)) == (ACC_ABSTRACT)))
            return null;
        MethodVisitor mv = super.visitMethod(a, name, d, s, e);
        MethodAdapter ma = new MethodAdapter(Opcodes.ASM7, mv, a, name, className,d,desc);

        // don't add calls to static methods
        if (name.equals(modifiedMethod) && ((a & ACC_STATIC) != ACC_STATIC)) {
            return ma;
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, newMethod, desc, null, null);
        addFields();
        addMissingFields()
        definition(mv);
        mv.visitEnd();
        cv.visitEnd();
    }

    public String getFreshName(String fieldName){
        int i = 0;
        while(fieldNames.contains(fieldName)){
            fieldName = fieldName + i;
            i++;
        }
        if(i>0)
            fieldNames.add(fieldName);
        return fieldName;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        fieldNames.add(name);
        return cv.visitField(access, name, desc, signature, value);
    }


    public void addFields() {
        FieldVisitor fv;
        Field field;
        int NEWFIELDS = 0; // set number of new fields
        for(int i = 0; i < NEWFIELDS; i++){
            try {
                field = ClassAdapter.class.getDeclaredField("fieldName"+i); // set classname
                field.set(this,getFreshName((String)field.get(this)));
                String name = (String) field.get(this);
                field = ClassAdapter.class.getDeclaredField("fieldDesc"+i); // set classname
                String desc = (String) field.get(this);
                fv = cv.visitField(ACC_PRIVATE, name, desc, null, null);
                fv.visitEnd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addMissingFields() {
        FieldVisitor fv;
        Field field;
        int MISSINGFIELDS = 0; // set number of new fields
        for(int i = 0; i < MISSINGFIELDS; i++){
            try {
                field = ClassAdapter.class.getDeclaredField("missingFieldName"+i); // set classname
                field.set(this,getFreshName((String)field.get(this)));
                String name = (String) field.get(this);
                if(!fieldNames.contains(name)) {
                    field = ClassAdapter.class.getDeclaredField("missingFieldDesc" + i); // set classname
                    String desc = (String) field.get(this);
                    fv = cv.visitField(ACC_PRIVATE, name, desc, null, null);
                    fv.visitEnd();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void definition(MethodVisitor methodVisitor) {
        // add definition
    }
}