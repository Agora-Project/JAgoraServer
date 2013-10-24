package org.agora.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.net.Socket;

import org.agora.lib.JAgoraComms;
import org.agora.logging.Log;
import org.bson.BSONObject;

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
    Log.log(this + " starting.");
    while(running) {
      try {
        BlockingQueue<Socket> q = server.getRequestQueue();
        Socket clientSocket = q.poll(Options.REQUEST_WAIT, TimeUnit.MILLISECONDS);
        processSocketRequest(clientSocket);
      } catch (InterruptedException e) {
        Log.log(toString() + " was interrupted.");
      }
    }
  }
  
  /**
   * The function that handles Agora requests from sockets.
   * @param clientSocket The socket to serve-
   * @return Success or failure-
   */
  public boolean processSocketRequest(Socket clientSocket) {
    BSONObject request = JAgoraComms.readBSONObjectFromSocket(clientSocket);
    if (request == null)
      return false;
    
    BSONObject response = processBSONRequest(request);
    if (response == null)
      return false;
    
    return JAgoraComms.writeBSONObjectToSocket(clientSocket, response);
  }
  
  public BSONObject processBSONRequest(BSONObject request) {
    int requestType = (Integer)request.get("action");
    switch (requestType) {
    case JAgoraComms.LOGIN_ACTION:
      // TODO: implement
      break;
    case JAgoraComms.LOGOUT_ACTION:
      // TODO: implement
      break;
    }
    return null;
  }
  
  @Override
  public String toString() {
    return "JAgoraWorker["+threadID+"]";
  }

}
