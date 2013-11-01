package org.agora.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import org.agora.server.queries.*;
import org.agora.lib.*;
import org.agora.logging.ConsoleLog;
import org.agora.logging.Log;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;


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
  }
  
  /**
   * Fills the responders map and makes sure that there is
   * a query responder for all possible queries.
   */
  protected void initialiseResponders() {
    responders = new HashMap<Integer, QueryResponder>();
    responders.put(IJAgoraLib.LOGIN_ACTION, new LoginResponder());
  }
  
  public QueryResponder getResponder(int operation) {
    if (!responders.containsKey(operation))
      return null;
    return responders.get(operation);
  }
  
  /**
   * Creates and binds the server socket. Also spawns worker threads.
   */
  public void startServer() {
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
  
  public BlockingQueue<Socket> getRequestQueue() { return requestQueue; }
  
  public static void InitLogging() {
    //Log.addLog(new FileLog(Options.LOG_FILE, Options.ERROR_FILE, Options.DEBUG_FILE));
    Log.addLog(new ConsoleLog());
  }
  
  public void readDBConfFromFile(String file) {
    String json;
    try {
      Scanner s = new Scanner(new File(file));
      json = s.useDelimiter("\\A").next();
      s.close();
    } catch (FileNotFoundException e) {
      Log.error("[JAgoraServer] Could not find configuration file " + file);
      return;
    }
    
    DBObject bson = (DBObject) JSON.parse(json);
    Options.DB_URL = (String) bson.get("url");
    Options.DB_USER = (String) bson.get("user");
    Options.DB_PASS = (String) bson.get("pass");
  }
  
  
  
  public static void main(String[] args) {
    JAgoraServer.InitLogging();
    Log.log("[JAgoraServer] starting.");
    JAgoraServer jas = new JAgoraServer();
    jas.readDBConfFromFile(Options.DB_FILE);
    jas.startServer();
    while (true)
      jas.listen();
  }

}
