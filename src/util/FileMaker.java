package util;

import java.io.File;
import java.io.IOException;

public class FileMaker {
 
    public static void makeFileIfNotExists(String path) {
        try {
            File file = new File(path);

            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()){
                parentDir.mkdir();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
    }    
    
}