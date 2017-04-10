/*
 * ByteOperation.java
 *
 * Created on October 8, 2006, 4:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Encryption;

public class ByteOperation {
    
    /** Creates a new instance of ByteOperation */
    public ByteOperation() {
    }

    public static void IVector(byte[] ivec, int n,String key){
        //generate from key        
        int i;
        for (i = 0; i < n; i++){
            ivec[i] = (byte) (((key.charAt(i))*(i+1))%256);
        }
    }
    
    public static void CopyByte1toByte2(byte[] b1, byte[] b2, int n){
        int i;
        for (i = 0; i < n; i++){
            b2[i] = b1[i];
        }
    }
    
    public static void Xor(byte[] b1, byte[] b2, int n){
        int i,a,b;
        for (i = 0; i < n; i++){                        
            b1[i] = (byte) (b1[i] ^ b2[i]);
        }
    }
    
    public static void wrapLeft(byte[] buff, int n){
        if (0 < n && n < 8){
            int len = buff.length;
            byte tmp2;
            byte tmp = buff[0];

            for (int i = 0; i < len; i++){
                buff[i] = (byte) (buff[i] << n);
                if (i+1 < len){
                    tmp2 = (byte) (buff[i+1] >> (8-n));
                } else {
                    tmp2 = (byte) (tmp >> (8-n));
                }
                if (tmp2 < 0){
                    byte y = (byte)(-128 >> (7-n));
                    tmp2 = (byte) (tmp2 ^ y);
                }
                buff[i] = (byte) (buff[i] + tmp2);
            }
        }
    }

    public static void wrapRight(byte[] buff, int n){
        if (0 < n && n < 8){
            int len = buff.length;
            byte tmp2;
            byte tmp = buff[len-1];

            for (int i = len-1; i >= 0; i--){
                buff[i] = (byte) (buff[i] >> n);
                if (buff[i] < 0){
                    byte y = (byte)(-128 >> (n-1));
                    buff[i] = (byte) (buff[i] ^ y);
                }

                if (i > 0){
                    tmp2 = (byte) (buff[i-1] << (8-n));
                } else {
                    tmp2 = (byte) (tmp << (8-n));
                }
                buff[i] = (byte) (buff[i] + tmp2);
            }
        }
    }
}
