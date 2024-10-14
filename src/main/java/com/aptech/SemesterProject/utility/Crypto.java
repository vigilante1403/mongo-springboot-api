package com.aptech.SemesterProject.utility;

import jakarta.mail.Message;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.concurrent.ExecutionException;

@Service
public class Crypto {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String HashPassword(String input,String salt){
        String result="";
        try{
            MessageDigest messagedigest = MessageDigest.getInstance("SHA-256");
            messagedigest.update(input.getBytes());
            messagedigest.update(salt.getBytes());
            byte[] outputString = messagedigest.digest();
            result = bytesToHex(outputString);
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }
}
