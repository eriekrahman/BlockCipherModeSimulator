/*
 * FileLoader.java
 *
 * Created on October 6, 2006, 4:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class FileLoader extends Thread {

    Document doc;
    File f;
    
    public FileLoader(File f, Document doc) {
        setPriority(this.MAX_PRIORITY);
        this.f = f;
        this.doc = doc;
    }

    public void run() {
        try {
            // try to start reading
            Reader in = new FileReader(f);
            char[] buff = new char[4096];
            int nch;
            while ((nch = in.read(buff, 0, buff.length)) != -1) {
                doc.insertString(doc.getLength(), new String(buff, 0, nch), null);
            }
        }
        catch (IOException e) {
           System.err.println(e.getMessage());
        }
        catch (BadLocationException e) {
            System.err.println(e.getMessage());
        }
    }

}
