package org.agora.server.queries;

import java.sql.SQLException;
import java.sql.Statement;

import org.agora.graph.JAgoraGraph;
import org.agora.lib.*;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBGraphDecoder;
import org.bson.BasicBSONObject;

public class ThreadByIDResponder implements QueryResponder {
  
  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
    
    // Verify session. Is this really necessary?
    //boolean verified = server.verifySession(query);
    
    BasicBSONObject bsonResponse = new BasicBSONObject();
//    if (!verified) {
//      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
//      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
//      return bsonResponse;
//    }
    
    int threadID = query.getInt(IJAgoraLib.THREAD_ID_FIELD);
    
    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[ThreadByIDResponder] Could not connect to database.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }
      
      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[ThreadByIDResponder] Could not create statement.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }
      
      // Grab graph from DB.
      DBGraphDecoder dgd = new DBGraphDecoder();
      dgd.loadGraphByThreadID(s, threadID);
      
      JAgoraGraph graph = dgd.getGraph();
      
      // Encode graph into BSON
      BSONGraphEncoder enc = new BSONGraphEncoder();
      BasicBSONObject bsonGraph = enc.BSONiseGraph(graph);
      
      // Add it to the response
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
      bsonResponse.put(IJAgoraLib.GRAPH_FIELD, bsonGraph);
      return bsonResponse;
      
    } catch (SQLException e) {
      Log.error("[ThreadByIDResponder] Could not execute query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }
}
