/*
 * FileSaver.java
 *
 * Created on October 6, 2006, 4:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Writer;


public class FileSaver{
    
    File f1, f2;

    public FileSaver(){
    }

    public static void Save(File fileinput,File fileoutput){
        try{
            FileInputStream input = new FileInputStream(fileinput);
            FileOutputStream output = new FileOutputStream(fileoutput);

            int readSize;
            byte[] buff = new byte[1];
            while (true){
                readSize = input.read(buff);
                if (readSize <= 0){
                        break;
                }
                output.write(buff);
            }

            input.close();
            output.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
