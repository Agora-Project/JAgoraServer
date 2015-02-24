package org.agora.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import org.agora.server.queries.*;
import org.agora.lib.*;
import org.agora.logging.ConsoleLog;
import org.agora.logging.Log;
import org.bson.BasicBSONObject;

public interface JAgoraServer {

  public QueryResponder getResponder(int operation);

  public DatabaseConnection createDatabaseConnection();

  /**
   * Initiates login and session management for the given user.
   *
   * @param user The username
   * @param userID The userID (from the DB)
   * @return The new UserSession
   */
  public UserSession userLogin(String user, int userID, int userType);

  public UserSession getSession(int userID);

  public boolean verifySession(int userID, String sessionID);

  public boolean verifySession(BasicBSONObject query);

  public boolean logoutUser(int userID);

  public BlockingQueue<Socket> getRequestQueue();
}
