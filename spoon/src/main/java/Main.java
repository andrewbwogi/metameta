import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Iterator;

public class Main {
    final static int METHODS = 3;

    public static void main(final String args[]) throws Exception {
        String in = args[0];
        Iterator it = FileUtils.iterateFiles(new File(args[0]), null, false);
        while(it.hasNext()){
            String fileName = ((File) it.next()).getName();
            String inputPath = in + fileName;
            String className = fileName.substring(0,(fileName.length()-5));
            String outputPath;
            Spoon transformer = new Spoon(inputPath,className);
            //transformer.addBegin(args[1] + className + "-Begin" + 1 + "/" + fileName, 1);
            for(int i = 1; i <= METHODS; i++) {

                // add method call in the beginning of chosen method
                Method method = transformer.getClass().getMethod("addBegin",String.class,Integer.class);
                outputPath = args[1] + className + "-Begin" + i + "/" + fileName;
                method.invoke(transformer,outputPath,i);

                // add method call in the end of chosen method
                method = transformer.getClass().getMethod("addEnd",String.class,Integer.class);
                outputPath = args[1] + className + "-End" + i + "/" + fileName;
                method.invoke(transformer,outputPath,i);
            }
        }
    }
}