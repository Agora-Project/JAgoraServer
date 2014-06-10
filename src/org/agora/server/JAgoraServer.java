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


public class JAgoraServer {

  /**
   * Random number generator.
   */
  protected Random rand;
  
  /**
   * Socket on which the server is listening.
   */
  protected ServerSocket socket;
  
  /**
   * When connections are opened, they are stored here. Worker threads grab'em
   * and process'em.
   */
  protected BlockingQueue<Socket> requestQueue;
  
  /**
   * Worker threads.
   */
  protected List<JAgoraWorker> workers;
  
  /**
   * Query responders for the different kinds of requests.
   */
  protected Map<Integer, QueryResponder> responders;
  // TODO: the above can probably become a plain old QueryResponder[]
  //       if we're careful. That's a future optimisation.
  
  /**
   * Session map
   */
  protected ConcurrentMap<Integer, UserSession> sessions;
  
  public JAgoraServer() {
    rand = new Random();
    requestQueue = new LinkedBlockingQueue<Socket>();
    workers = new LinkedList<JAgoraWorker>();
    sessions = new ConcurrentHashMap<Integer, UserSession>();
    initialiseResponders();
    
    readConfigurationFiles();
  }
  
  protected void readConfigurationFiles() {
    Options.readDBConfFromFile();
    Options.readAgoraConfFromFile();
  }
  
  /**
   * Fills the responders map and makes sure that there is
   * a query responder for all possible queries.
   */
  protected void initialiseResponders() {
    responders = new HashMap<Integer, QueryResponder>();
    responders.put(IJAgoraLib.LOGIN_ACTION, new LoginResponder());
    responders.put(IJAgoraLib.LOGOUT_ACTION, new LogoutResponder());
    responders.put(IJAgoraLib.QUERY_BY_THREAD_ID_ACTION, new ThreadByIDResponder());
    responders.put(IJAgoraLib.ADD_ARGUMENT_ACTION, new AddArgumentResponder());
    responders.put(IJAgoraLib.ADD_ATTACK_ACTION, new AddAttackResponder());
    responders.put(IJAgoraLib.ADD_ARGUMENT_VOTE_ACTION, new AddArgumentVoteResponder());
    responders.put(IJAgoraLib.ADD_ATTACK_VOTE_ACTION, new AddAttackVoteResponder());
    responders.put(IJAgoraLib.REGISTER_ACTION, new RegisterResponder());
    responders.put(IJAgoraLib.QUERY_THREAD_LIST_ACTION, new ThreadListResponder());
    responders.put(IJAgoraLib.EDIT_ARGUMENT_ACTION, new EditArgumentResponder());
  }
  
  public QueryResponder getResponder(int operation) {
    if (!responders.containsKey(operation))
      return null;
    return responders.get(operation);
  }
  
  /**
   * Starts the server and runs it.
   */
  public void run() {
    startServer();
    mainLoop();
  }
  
  /**
   * Creates and binds the server socket. Also spawns worker threads.
   */
  protected void startServer() {
    // Bind server socket.
    try {
      socket = new ServerSocket(org.agora.lib.Options.AGORA_PORT);
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
  
  
  protected void mainLoop() {
    while(true) {
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
  }
  
  public void stopServer() {
    Log.log("[JAgoraServer] Shutting down server.");
  }
  
  /**
   * Attempts to create a new database connection.
   * @return
   */
  public DatabaseConnection createDatabaseConnection() {
    DatabaseConnection dbc = new DatabaseConnection(Options.DB_URL,
                                                    Options.DB_USER,
                                                    Options.DB_PASS);
    boolean connected = dbc.open();
    if(!connected) {
      Log.error("[JAgoraServer] Could not initiate connection to database.");
      return null;
    }
    
    return dbc;
  }
  
  /**
   * Initiates login and session management for the given user.
   * @param user The username
   * @param userID The userID (from the DB)
   * @return The new UserSession 
   */
  public UserSession userLogin(String user, int userID) {
    byte[] sessBytes = new byte[Options.SESSION_BYTE_LENGTH];
    rand.nextBytes(sessBytes);
    String sessionID = Util.bytesToHex(sessBytes);
    UserSession session = new UserSession(user, userID, sessionID);
    sessions.put(userID, session);
    return session;
  }
  
  public UserSession getSession(int userID) {
    if (!sessions.containsKey(userID))
      return null;
    return sessions.get(userID);
  }
  
  public boolean verifySession(int userID, String sessionID) {
    UserSession us = getSession(userID);
    if (us == null)
      return false;
    
    return us.getSessionID().equals(sessionID);
  }
 
  public boolean verifySession(BasicBSONObject query) {
    int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
    String sessionID = query.getString(IJAgoraLib.SESSION_ID_FIELD);
    
    UserSession us = getSession(userID);
    if (us == null)
      return false;
    
    return us.getSessionID().equals(sessionID);
  }
  
  
  public boolean logoutUser(int userID) {
    return sessions.remove(userID) != null;
  }
  
  public BlockingQueue<Socket> getRequestQueue() { return requestQueue; }
  
  public static void InitLogging() {
    //Log.addLog(new FileLog(Options.LOG_FILE, Options.ERROR_FILE, Options.DEBUG_FILE));
    Log.addLog(new ConsoleLog());
  }
  
  public static void main(String[] args) {
    JAgoraServer.InitLogging();
    Log.log("[JAgoraServer] starting.");
    
    // Parse command-line options first.
    Options.parseOptions(args);
    
    // Regular options are while constructing the server.
    JAgoraServer jas = new JAgoraServer();
    
    jas.run();

  }

}
