package org.agora.server.queries;

import java.sql.Statement;
import java.util.ArrayList;
import org.agora.graph.JAgoraThread;
import org.agora.lib.BSONThreadListEncoder;
import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.DatabaseConnection;
import org.agora.server.JAgoraServer;
import org.agora.server.database.DBGraphDecoder;
import org.bson.BasicBSONObject;

/**
 *
 * @author greg
 */
public class ThreadListResponder implements QueryResponder{

    @Override
    public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
        
        // Verify session
    boolean verified = server.verifySession(query);
    
    BasicBSONObject bsonResponse = new BasicBSONObject();
    if (!verified) {
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
      return bsonResponse;
    }
    
    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[ThreadListResponder] Could not connect to database.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }
      
      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[ThreadListResponder] Could not create statement.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }
      
      ArrayList<JAgoraThread> threads;
      DBGraphDecoder dgd = new DBGraphDecoder();
      threads = dgd.getThreads(s);
      
      BSONThreadListEncoder enc = new BSONThreadListEncoder();
      BasicBSONObject bsonThreads = enc.BSONiseThreadList(threads);
      
      // Add it to the response
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
      bsonResponse.put(IJAgoraLib.THREAD_FIELD, bsonThreads);
      return bsonResponse;
      
    } catch (Exception e) {
      Log.error("[ThreadListResponder] Could not execute query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }

}
