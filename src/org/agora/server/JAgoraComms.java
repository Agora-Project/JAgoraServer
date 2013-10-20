package org.agora.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.agora.server.logging.Log;
import org.bson.BSONDecoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;

public class JAgoraComms {

  
  public static int toInt(InputStream is) throws IOException {
    byte ch1 = (byte)is.read();
    byte ch2 = (byte)is.read();
    byte ch3 = (byte)is.read();
    byte ch4 = (byte)is.read();
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new IOException();
    return toInt(new byte[]{ch1, ch2, ch3, ch4});
  }
  
  /**
   * Assumes something about endian-ness.
   * @param b
   * @return
   */
  public static int toInt(byte[] b) {
    // TODO: This assumes something about endian-ness. It should be in the
    //       official protocol at some point.
    return ((b[0] << 24) + (b[1] << 16) + (b[2] << 8) + (b[3] << 0));  
  }
  
  /**
   * Returns the required number of bytes to deserialise the BSON object.
   * @param is The input stream from the client socket
   * @param length length of the BSON serialisation
   * @return The BSON serialisation
   * @throws IOException
   */
  public static byte[] readArray(InputStream is, int length) throws IOException {
    byte[] result = new byte[length];
    int startByte = 0;
    int toGo = length;
    while (toGo > 0) {
      int read = is.read(result, startByte, toGo);
      toGo -= read;
      startByte += read;
    }
    return result;
  }
  
  
  public static BSONObject readBSONFromSocket(Socket s) {
    try {
      InputStream is = s.getInputStream();
      int size = toInt(is); // Read size of BSON object serialisation
      if (size > Options.MAX_INCOMING_BSON_SIZE)
        throw new IOException("Incoming file too large!");
      
      // Read object
      byte[] bsonSerialised = readArray(is, size);
      BSONDecoder bdec = new BasicBSONDecoder();
      //bdec.
      
      
    } catch (IOException e) {
      Log.error("Could not read BSON object from socket " + s);
      Log.error(e.getMessage());
    }
    
    return null;
  }
  
  
}
