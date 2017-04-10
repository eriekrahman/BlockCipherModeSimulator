/*
 * CBC.java
 *
 * Created on October 6, 2006, 9:02 PM
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

public class CBC extends ByteOperation{
    
    /** Creates a new instance of CBC */
    public CBC() {
        super();
    }
    
    public static void EncryptAlgorithm(byte[] buff, byte[] Key){       
        int i = 0;    
        while (i < 5){            
            Xor(buff, Key, Key.length);
            wrapLeft(buff, 5);
            Xor(buff, Key, Key.length);
            wrapRight(buff, 4);
            i++;
        }
    }
    
    public static void DecryptAlgorithm(byte[] buff, byte[] Key){       
        int i = 0;
        while (i < 5){
            wrapLeft(buff, 4);
            Xor(buff, Key, Key.length);
            wrapRight(buff, 5);
            Xor(buff, Key, Key.length);            
            i++;
        }      
    }
    
    public static File Encrypt(File fileInput, String key){
        File fileOutput = new File("cache");
        
        int readSize, i;        
        int len = key.length();
        
        byte[] Key = new byte[len];
        
        for (i=0; i<len; i++){
            Key[i] = (byte) key.charAt(i);
        }

        /* blockSize - is an nBit that is supported by CBC
         * n = key length
         */
        int blockSize = key.length();
        
        try {            
            DataInputStream plaintext = new DataInputStream(new FileInputStream(fileInput));
            DataOutputStream ciphertext = new DataOutputStream(new FileOutputStream(fileOutput));

            // Header File

            /* Header 1
             * type : integer
             * contains initial int of crypted file = "3456"
             */
            ciphertext.writeInt(3456);

            /* Header 2
             * type : integer
             * contains mode of CBC = 200
             */
            int mode = 200;
            ciphertext.writeInt(mode);

            /* Header 3
             * type : long integer
             * contains initial length of the plain file
             * - is used to count the padding bit during decryption
             */
            long fileSize = fileInput.length();
            ciphertext.writeLong(fileSize);

            /* ------------------------------------
             * Algorithm
             **/
            
            // Save plain text
            byte[] buff = new byte[blockSize];
            // Save temporarily the cipher result
            byte[] vector = new byte[blockSize];

            // Initialize the first vector
            IVector(vector, blockSize, key);

            /* Header 4
             * type : IV
             * contains IV without being initiated by its length
             * the key length during decryption will be obtained from the submitted key
             */
            ciphertext.write(vector,0,blockSize);

            while (true)
            {
                readSize = plaintext.read(buff, 0, blockSize);
                if (readSize <= 0){
                    break;
                }
                
                Xor(buff, vector, blockSize);
                
                EncryptAlgorithm(buff, Key);
                
                ciphertext.write(buff, 0, blockSize);
                
                CopyByte1toByte2(buff,vector,blockSize);
            }
            plaintext.close();
            ciphertext.close();

        } catch (IOException e){
            e.printStackTrace();
        }
        return fileOutput;
    }    
    
    public static File Decrypt(File fileInput,String key){        
        int readSize, i;
        int tmpInt;
        File filePlain = new File("cache");
        
        // Change string to byte
        int len = key.length();        
        byte[] Key = new byte[len];        
        for (i=0; i<len; i++){
            Key[i] = (byte) key.charAt(i);
        }
        
        try {            
            DataInputStream ciphertext = new DataInputStream(new FileInputStream(fileInput));
            DataOutputStream plaintext = new DataOutputStream(new FileOutputStream(filePlain));
                        
            int blockSize = key.length();
        
            byte[] buff = new byte[blockSize];
            byte[] buff2 = new byte[blockSize];
            byte[] vector = new byte[blockSize];
            
            // Read file header

            /* Header 1
             * type : integer
             * contains the initial int of crypted file = "3456"
             */
            tmpInt = ciphertext.readInt();
            if (tmpInt != 3456){
                throw new Exception("The file is not encrypted!");
            }
            
            /* Header 2
             * type : integer
             * contains the mode of CBC = 200
             */              
            tmpInt = ciphertext.readInt();
            if (tmpInt != 200){
                throw new Exception("File is invalid - wrong mode!");
            } 
            
            /* Header 3
             * type : long integer
             * contains initial length of the plain file
                 * - is used to count the padding bit during decryption
             */
            Long filesize = ciphertext.readLong();
            
            /* Header 4
             * type : long integer
             * contains n-bit of length
             */
            for (i = 0; i < len; i++){
                vector[i] = ciphertext.readByte();
            }
            
            while (true){
                readSize = ciphertext.read(buff, 0, blockSize);
                if (readSize <= 0){
                    break;
                }
                
                CopyByte1toByte2(buff, buff2, blockSize);
                
                DecryptAlgorithm(buff, Key);
                
                Xor(buff, vector, blockSize);
                
                if (filesize > blockSize){
                    plaintext.write(buff, 0, blockSize);
                    filesize = filesize - blockSize;
		} else {
                    plaintext.write(buff, 0, filesize.intValue());
                    break;
		}    
                
                CopyByte1toByte2(buff2, vector, blockSize);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return filePlain;
    }
    
}
