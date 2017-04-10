/*
 * CFB.java
 *
 * Created on October 6, 2006, 9:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CFB extends ByteOperation {
    
    /** Creates a new instance of CFB */
    public CFB() {
        super();
    }
    
    public static void EncryptAlgorithm(byte[] vector, byte[] Key){       
        int i = 0;        
        /* Algorithm - 5 times of:
         * - Xor
         * - Wrap Left 5 bit
         * - Xor
         * - Wrap Right 4 bit
         */        
        while (i < 5){
            Xor(vector, Key, Key.length);
            wrapLeft(vector, 5);            
            Xor(vector, Key, Key.length);
            wrapRight(vector, 4);    
            i++;
        }        
    }
    
    public static File Encrypt(File fileInput, String key, int nBit){
        int readSize, i;        
        int len = key.length();        
        byte[] Key = new byte[len];
        File fileOutput = new File("cache");
            
        for (i=0; i<len; i++){
            Key[i] = (byte) key.charAt(i);
        }

        /* blockSize - is an nBit that is supported by CFB
         * n cannot be longer than the key length
         */
        int blockSize = nBit;

        /* vectorSize is defined as the length of the key
         */
        int vectorSize = key.length();
                
        if (blockSize > vectorSize){
            
        } else {
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
                 * contains mode of CFB = 300
                 */
                int mode = 300;
                ciphertext.writeInt(mode);

                /* Header 3
                 * type : long integer
                 * contains initial length of the plain file
                 * - is used to count the padding bit during decryption
                 */
                long fileSize = fileInput.length();
                ciphertext.writeLong(fileSize);

                /* Header 4
                 * type : long integer
                 * contains the length of a bit that has been managed
                 */
                ciphertext.writeInt(nBit);

                /* ------------------------------------
                 * Algorithm
                 **/

                byte[] buff = new byte[blockSize];
                byte[] vector1 = new byte[vectorSize];
                byte[] vector2 = new byte[vectorSize];

                // Initialize the first vector
                IVector(vector1, vectorSize, key);

                /* Header 5
                 * type : IV
                 * contains IV that is initiated by its length
                 */
                ciphertext.writeInt(vectorSize);
                ciphertext.write(vector1,0,vectorSize);

                while (true)
                {
                    readSize = plaintext.read(buff, 0, blockSize);
                    if (readSize <= 0){
                        break;
                    }

                    CopyByte1toByte2(vector1,vector2,vectorSize);

                    EncryptAlgorithm(vector1, Key);                  

                    Xor(buff, vector2, blockSize);

                    ciphertext.write(buff, 0, blockSize);

                    // Shift the vector value by blockSize
                    for (i = 0; i < vectorSize - blockSize; i++){
                        vector1[i] = vector1[i+blockSize];
                    }

                    while (i < vectorSize){
                        vector1[i] = buff[i - vectorSize + blockSize];
                        i++;
                    }
                }
                plaintext.close();
                ciphertext.close();               
                
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return fileOutput;
    }
    
    public static File Decrypt(File fileInput, String key){
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
             * contains the mode of CFB = 300
             */            
            tmpInt = ciphertext.readInt();
            if (tmpInt != 300){
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
            int blockSize = ciphertext.readInt();
            
            /* Header 5
             * type : IV
             * contains IV that is initiated by its length        
             */
            int lengthIv = ciphertext.readInt();            

            /* vectorSize is defined as the length of the key
             **/
            int vectorSize = key.length();
            
            byte[] buff = new byte[blockSize];
            byte[] vector1 = new byte[vectorSize];
            byte[] vector2 = new byte[vectorSize];
            
            for (i = 0; i < lengthIv; i++){
                vector1[i] = ciphertext.readByte();
            }
            
            while (true){
                readSize = ciphertext.read(buff, 0, blockSize);
                if (readSize <= 0){
                    break;
                }
                
                CopyByte1toByte2(vector1,vector2,vectorSize);

                EncryptAlgorithm(vector1, Key);                  

                // Shift vector value by blockSize 
                for (i = 0; i < vectorSize - blockSize; i++){
                    vector1[i] = vector1[i+blockSize];
                }

                while (i < vectorSize){
                    vector1[i] = buff[i - vectorSize + blockSize];
                    i++;
                }
                
                Xor(buff, vector2, blockSize);
                
                if (filesize > blockSize){
                    plaintext.write(buff, 0, blockSize);
                    filesize = filesize - blockSize;
		} else {
                    plaintext.write(buff, 0, filesize.intValue());
                    break;
		}                
            }            
            
            ciphertext.close();
            plaintext.close();
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return filePlain;
    }    
}
