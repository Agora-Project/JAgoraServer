package org.agora.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.agora.server.logging.ConsoleLog;
import org.agora.server.logging.Log;
import org.agora.server.logging.FileLog;


public class JAgoraServer {

  
  /**
   * Socket on which the server is listening.
   */
  protected ServerSocket socket;
  
  /**
   * When connections are opened, they are stored here. Worker threads grab'em
   * and process'em.
   */
  protected BlockingQueue<Socket> requestQueue;
  
  protected List<JAgoraWorker> workers;
  
  
  
  public JAgoraServer() {
    requestQueue = new LinkedBlockingQueue<Socket>();
    workers = new LinkedList<JAgoraWorker>();
  }
  
  /**
   * Creates and binds the server socket. Also spawns worker threads.
   */
  public void startServer() {
    // Bind server socket.
    try {
      socket = new ServerSocket(Options.LISTEN_PORT);
    } catch (IOException e) {
      Log.error("Failure while creating server socket.");
      Log.error(e.toString());
    }
    
    // Start threads.
    for (int i = 0; i < Options.NUM_WORKERS; i++) {
      JAgoraWorker jaw = new JAgoraWorker(this);
      workers.add(jaw);
      jaw.start();
    }
    
  }
  
  public void listen() {
    try {
      Socket clientSocket = socket.accept();
      Log.debug("Received connection from " + clientSocket.getInetAddress());
      if (!requestQueue.offer(clientSocket))
        System.err.println("Could not add client socket to request queue.");
    } catch (IOException e) {
      Log.error("Failure while listening to sockets.");
      Log.error(e.toString());
    }
  }
  
  public void stopServer() {
    
  }
  
  public BlockingQueue<Socket> getRequestQueue() { return requestQueue; }
  
  public static void InitLogging() {
    //Log.addLog(new FileLog(Options.LOG_FILE, Options.ERROR_FILE, Options.DEBUG_FILE));
    Log.addLog(new ConsoleLog());
  }
  
  
  
  public static void main(String[] args) {
    JAgoraServer.InitLogging();
    Log.log("[JAgoraServer] starting.");
    JAgoraServer jas = new JAgoraServer();
    jas.startServer();
    while (true)
      jas.listen();
  }
}
