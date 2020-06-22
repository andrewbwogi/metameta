import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;

public class Main {
    final static int TRANSFORMERS = 1;

    public static void main(String[] args) throws Exception {
        String in = args[0];
        String out = args[1];
        Iterator it = FileUtils.iterateFiles(new File(args[0]), null, false);
        while (it.hasNext()) {
            String fileName = ((File) it.next()).getName();
            String inputPath = in + fileName;
            String className = fileName.substring(0,(fileName.length()-6));
            String outputPath;
            for (int i = 1; i <= TRANSFORMERS; i++) {
                Object object = Class.forName("ASMTransformer" + i).newInstance();
                Method method = object.getClass().getMethod("execute",String.class,String.class);
                outputPath = out + className + "-" + i + "/" + fileName;
                method.invoke(object,inputPath,outputPath);
            }
        }
    }
}
