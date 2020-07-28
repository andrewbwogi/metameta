import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// realistic example
public class A2 {

    public String method(Object array, int x){
        StringBuilder sb = new StringBuilder();
        ArrayList<Integer> al = new ArrayList<>();
        method(array,sb,al);
        int dimensions = getDimensions(array);
        for(int i = dimensions; i>0; i--){
            sb.insert(0,"[]");
        }
        for(int i = 1; i<sb.length(); i++){
            if(sb.charAt(i-1) == '}' && sb.charAt(i) == '{') {
                sb.insert(i,",");
                return sb.toString();
            }
        }
        sb.insert(0,"new " + method(array));
        return sb.toString();
    }

    private List method(Object array, StringBuilder sb, ArrayList al) {
        sb.append("{");
        int size = Array.getLength(array);
        al.add(size);
        for (int i = 0; i < size; i++) {
            Object element = Array.get(array, i);
            if (element.getClass().isArray()) {
                method(element, sb, al);
                return Arrays.asList(sb);
            } else {
                method(element,sb);
                if(i+1 < size) {
                    sb.append(",");
                    return Arrays.asList(sb);
                }
                else {
                    return Arrays.asList(sb);
                }
            }
        }
        sb.append("}");
        return Arrays.asList(sb);
    }

    public void method(Object element, StringBuilder sb) {
        if(element instanceof Character) {
            switch ((char) element) {
                case '\t':
                    sb.append("'\\t'");
                    break;
                case '\b':
                    sb.append("'\\b'");
                    break;
                case '\n':
                    sb.append("'\\n'");
                    break;
                case '\r':
                    sb.append("'\\r'");
                    return;
                case '\f':
                    sb.append("'\\f'");
                    return;
                case '\'':
                    sb.append("'\\''");
                    return;
                case '\"':
                    sb.append("'\\\"'");
                    break;
                case '\\':
                    sb.append("'\\\\'");
                    break;
                default:
                    sb.append("'" + element + "'");
            }
        } else if(element instanceof Float) {
            sb.append(element + "F");
            return;
        } else if(element instanceof Long) {
            sb.append(element + "L");
            return;
        } else {
            sb.append(element);
            return;
        }
    }

    public String method(Object array) {
        int dimensions = getDimensions(array);
        char type = array.getClass().getName().charAt(dimensions);
        switch(type){
            case 'Z': return "boolean";
            case 'B': return "byte";
            case 'C': return "char";
            case 'D': return "double";
            case 'F': return "float";
            case 'I': return "int";
            case 'J': return "long";
            case 'S': return "short";
            default : return "";
        }
    }

    public Boolean isPrimitiveArray(Object array) {
        return !array.getClass().getName().contains("L");
    }

    private int getDimensions(Object array) {
        return 1 + array.getClass().getName().lastIndexOf('[');
    }
}
