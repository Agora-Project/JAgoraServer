package org.agora.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.bson.BSONDecoder;
import org.bson.BSONEncoder;
import org.bson.BSONObject;
import org.bson.BasicBSONDecoder;
import org.bson.BasicBSONEncoder;
import org.bson.BasicBSONObject;

public class Test {
  
  public static void testServer() throws IOException {
    ServerSocket server = new ServerSocket(3000);
    System.out.println("Socket created.");
    Socket client = server.accept();
    System.out.println("Connection accepted.");
    
    BSONDecoder bdec = new BasicBSONDecoder();
    BSONObject bson = bdec.readObject(client.getInputStream());
    
    System.out.println("Read stuff: " + bson);
    client.close();
    
    server.close();
  }
  
  
  
  public static void testClient(String hostname, int port) throws IOException {
    BSONObject bson = new BasicBSONObject();
    System.out.println("Created empty BSON: " + bson);
    bson.put("request", "Give me argument #5555");
    System.out.println("Added BSON stuff: " + bson);
    
    BSONEncoder benc = new BasicBSONEncoder();
    byte[] b = benc.encode(bson);
    byte[] b2 = Arrays.copyOf(b, b.length + 1); // Uh oh, one extra byte! Can BSON take it?!
    
    System.out.println("encoded : " + b);
    
    Socket s = new Socket(hostname, port);
    System.out.println("Opened socket");
    s.getOutputStream().write(b2);
    System.out.println("Wrote bson");
    s.close();
    System.out.println("closed");
  }
  
  public static void main(String[] args) throws IOException {
    System.out.println(args[0]);
    if (args[0].equals("0"))
      testServer();
    else
      testClient(args[1], Integer.parseInt(args[2]));
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
  }
}
