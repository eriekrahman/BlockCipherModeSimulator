/*
 * ECB.java
 *
 * Created on October 6, 2006, 9:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Encryption;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ECB extends ByteOperation{
    
    /** Creates a new instance of EBC */
    public ECB() {
    }

    public static void blockEncrypt(byte[] buff, String key){
        int blockSize = key.length();
        int i;
        byte[] Key = new byte[blockSize];
        for (i=0; i<blockSize; i++){
            Key[i] = (byte) key.charAt(i);
        }
        while(i<16){
            wrapLeft(buff, 8);
            Xor(buff, Key, blockSize);
            wrapRight(buff, 8);
            i++;
        }
    }

    public static File Encrypt(File fileInput, String key) {
        File fileOutput = new File("cache");
                
        try {
            DataInputStream plainTeks = new DataInputStream(new FileInputStream(fileInput));
            DataOutputStream cipherTeks = new DataOutputStream(new FileOutputStream(fileOutput));
            long fileSize = fileInput.length();

            cipherTeks.writeInt(1000);//start of a file
            cipherTeks.writeInt(100);//100 Ecb
            cipherTeks.writeLong(fileSize);


            int blockSize = key.length();
            byte[] buff = new byte[blockSize];
            int readSize = 1;
                            
            while (true){
		readSize = plainTeks.read(buff, 0, blockSize);
		if (readSize <= 0){
			break;
		}
                blockEncrypt(buff,key);
                cipherTeks.write(buff, 0, blockSize);
            }
            
            plainTeks.close();
            cipherTeks.close();
                
        } catch (IOException e){
            e.printStackTrace();
        }
        
        return fileOutput;
    }
    
    public static File Decrypt(File fileCipher,String key){
        File filePlain = new File("cache");
    
        try {
            DataInputStream cipherTeks = new DataInputStream(new FileInputStream(fileCipher));
            DataOutputStream plainTeks = new DataOutputStream(new FileOutputStream(filePlain));
            int head = cipherTeks.readInt();
            int mode = cipherTeks.readInt();
            long fileSize = cipherTeks.readLong();

            if ((head != 1000) && (mode != 100)){
                throw new Exception("Wrong file!");
            }
            
            int blockSize = key.length();
            byte[] buff = new byte[blockSize];
            int readSize;
            
            while (true){
                readSize = cipherTeks.read(buff, 0, blockSize);
                if (readSize <= 0){
                    break;
                }
                blockEncrypt(buff,key);
                if (fileSize >= blockSize){
                    plainTeks.write(buff, 0, blockSize);
                    fileSize = fileSize - blockSize;
                } else {
                    plainTeks.write(buff, 0, (int) fileSize);
                    fileSize = 0;
                }
            }
     
            plainTeks.close();
            cipherTeks.close();
                
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return filePlain;
    }
    
}