package org.agora.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.agora.server.logging.Log;
import org.agora.server.logging.FileLog;


public class JAgoraServer {
  
  public static int SERVER_PORT = 1597;

  public static String LOG_FILE = "/var/log/agorad.log";
  public static String ERROR_FILE = "/var/log/agorad.err";
  public static String DEBUG_FILE = "/var/log/agorad.dbg";
  
  
  
  protected int serverPort;
  
  protected ServerSocket socket;
  
  public JAgoraServer(int serverPort) {
    this.serverPort = serverPort;
  }
  
  public void startListening() {
    try {
      socket = new ServerSocket(serverPort);
      Socket clientSocket = socket.accept();
      InputStream is = clientSocket.getInputStream();
      while(true) {
        int inData = is.read();
        if (inData < 0)
          break;
        clientSocket.getOutputStream().write(inData);
        clientSocket.getOutputStream().flush();
      }
      clientSocket.close();
    } catch (IOException e) {
      Log.error("[JAgoraServer] Failure while listening to sockets.");
      Log.error(e.toString());
    }
  }
  
  public static void InitLogging() {
    Log.addLog(new FileLog(LOG_FILE, ERROR_FILE, DEBUG_FILE));
  }
  
  public static void main(String[] args) {
    JAgoraServer.InitLogging();
    Log.log("[JAgoraServer] starting.");
    JAgoraServer jas = new JAgoraServer(JAgoraServer.SERVER_PORT);
    while (true)
      jas.startListening();
  }
}
