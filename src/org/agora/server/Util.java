package org.agora.server;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.agora.logging.Log;
import org.apache.commons.codec.binary.Base64;

public class Util {
  
  public static String hash(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash = md.digest(password.getBytes(Charset.forName("UTF-8")));
      
      return Base64.encodeBase64String(hash);
    } catch (NoSuchAlgorithmException e) {
      Log.error("[Util.hashPassword] Error hashing password ("+e.getMessage()+")");
      return null;
    }
  }
  
  
  
  // Credits: http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
  final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
  public static String bytesToHex(byte[] bytes) {
      char[] hexChars = new char[bytes.length * 2];
      int v;
      for ( int j = 0; j < bytes.length; j++ ) {
          v = bytes[j] & 0xFF;
          hexChars[j * 2] = hexArray[v >>> 4];
          hexChars[j * 2 + 1] = hexArray[v & 0x0F];
      }
      return new String(hexChars);
  }
}
