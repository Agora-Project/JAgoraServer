package org.agora.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.net.Socket;

import org.agora.server.logging.Log;

public class JAgoraWorker extends Thread {
  
  /**
   * ID counter for worker threads.
   */
  protected static int ID = 0;
  
  /**
   * Thread ID. Just for debug purposes.
   */
  protected int threadID;
  
  /**
   * The server from which this worker gets his requests.
   */
  protected JAgoraServer server;
  
  /**
   * Whether the worker should continue running.
   */
  protected boolean running;
  
  public JAgoraWorker(JAgoraServer server){
    threadID = ID++;
    this.server = server;
    running = true;
  }

  @Override
  public void run() {
    while(running) {
      try {
        BlockingQueue<Socket> q = server.getRequestQueue();
        Socket clientSocket = q.poll(Options.REQUEST_WAIT, TimeUnit.MILLISECONDS);
        processRequest(clientSocket);
      } catch (InterruptedException e) {
        Log.log(toString() + " was interrupted.");
      }
    }
  }
  
  
  public boolean processRequest(Socket clientSocket) {
    boolean success = true;
    
    return success;
  }
  
  @Override
  public String toString() {
    return "JAgoraWorker["+threadID+"]";
  }

}
