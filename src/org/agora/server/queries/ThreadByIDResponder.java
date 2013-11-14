package org.agora.server.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.agora.graph.JAgoraGraph;
import org.agora.lib.*;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.serialisation.DBGraphDecoder;
import org.bson.BasicBSONObject;

public class ThreadByIDResponder implements QueryResponder {
  
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
    
    int threadID = query.getInt(IJAgoraLib.QUERY_ID_FIELD);
    
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
      //TODO: input sanitisation
      String strQuery = "SELECT user_ID FROM users WHERE " + 
          "username = '" + user + "' AND " + 
          "password = '" + pass + "';";
      ResultSet rs = s.executeQuery(strQuery);

      
      boolean logged = rs.next();
      
      if (logged) {
        UserSession session = server.userLogin(user, rs.getInt("user_ID"));
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
        bsonResponse.put(IJAgoraLib.SESSION_ID_FIELD, session.getSessionID());
        bsonResponse.put(IJAgoraLib.USER_ID_FIELD, session.getUserID());
      } else {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Wrong username/password.");
      }
      return bsonResponse;
    } catch (SQLException e) {
      Log.error("[ThreadByIDResponder] Could not execute query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }

  
  protected JAgoraGraph loadGraphFromDatabase(DatabaseConnection dbc, int threadID) throws SQLException {
    DBGraphDecoder dbgd = new DBGraphDecoder();
    
    Statement s = dbc.produceStatement();
    
    ResultSet rs = s.executeQuery("SELECT source_ID, arg_ID FROM arguments WHERE thread_ID='"+threadID+"';");
    dbgd.loadNodesFromResultSet(rs);
    
    // TODO: use a PreparedStatement of the form "SELECT * FROM attacks WHERE 
    //       origin = ?" to iteratively grab all of the the relevant attacks.
    //       CAREFUL with duplicates!
    //       An alternative would be to join the argument & attack tables first
    //       with the defender nodes, and then with the attacker nodes where the
    //       thread ID of the attacker/defender is the given one
    
    return dbgd.getGraph();
  }
  
}
