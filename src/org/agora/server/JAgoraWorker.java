/*

Copyright (C) 2015 Agora Communication Corporation

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package org.agora.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.net.Socket;

import org.agora.lib.*;
import org.agora.logging.Log;
import org.agora.server.queries.*;
import org.bson.BasicBSONObject;

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
  protected JAgoraSocketServer server;
  
  /**
   * Whether the worker should continue running.
   */
  protected boolean running;
  
  public JAgoraWorker(JAgoraSocketServer server){
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
        if (clientSocket != null)
          processSocketRequest(clientSocket);
      } catch (InterruptedException e) {
        Log.log(toString() + " was interrupted.");
      }
    }
  }
  
  /**
   * The function that handles Agora requests from sockets.
   * @param clientSocket The socket to serve
   * @return Success or failure-
   */
  public boolean processSocketRequest(Socket clientSocket) {
    BasicBSONObject request = JAgoraComms.readBSONObjectFromSocket(clientSocket);
    if (request == null)
      return false;
    
    BasicBSONObject response = processBSONRequest(request);
    if (response == null)
      return false;
    
    return JAgoraComms.writeBSONObjectToSocket(clientSocket, response);
  }
  
  public BasicBSONObject processBSONRequest(BasicBSONObject request) {
    int requestType = (Integer)request.get(IJAgoraLib.ACTION_FIELD);
    QueryResponder r = server.getResponder(requestType);
    if (r == null) {
      BasicBSONObject response = new BasicBSONObject();
      response.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      response.put(IJAgoraLib.REASON_FIELD, "Cannot handle this request.");
      return response;
    }
    return r.respond(request, server);
  }
  
  @Override
  public String toString() {
    return "JAgoraWorker["+threadID+"]";
  }

}
