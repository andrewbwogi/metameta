import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.declaration.CtClassImpl;
import java.util.List;

public class SpoonToASMTransformer extends AbstractTransformer {

    SpoonToASMTransformer(String className) { super(className); }

    void addBegin(CtMethod method, List arguments) {}

    void addEnd(CtMethod method) {}

    void addField(CtField field) {}

    CtType getASMProgram() {
        return new CtClassImpl();
    }
}