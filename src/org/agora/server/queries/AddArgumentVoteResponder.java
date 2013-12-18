package org.agora.server.queries;

import java.sql.SQLException;

import org.agora.graph.JAgoraNodeID;
import org.agora.lib.BSONGraphDecoder;
import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBAddVote;
import org.bson.BasicBSONObject;

public class AddArgumentVoteResponder implements QueryResponder {
  
  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
    BasicBSONObject bsonResponse = new BasicBSONObject();
    
    boolean verified = server.verifySession(query);
    
    if (!verified) {
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
      return bsonResponse;
    }
    
    try {
      BasicBSONObject bsonId = (BasicBSONObject) query.get(IJAgoraLib.ARGUMENT_ID_FIELD);
      BSONGraphDecoder dec = new BSONGraphDecoder();
      JAgoraNodeID nodeID = dec.deBSONiseNodeID(bsonId);
      int voteType = (int) query.get(IJAgoraLib.VOTE_TYPE_FIELD);
      int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
      
      boolean res = DBAddVote.addArgumentVoteToDB(nodeID, voteType, userID, server.createDatabaseConnection());
      if (!res) {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Database failure.");
        return bsonResponse;
      }
    } catch (SQLException e) {
      Log.error("[AddArgumentVoteResponder] Could not execute add argument vote query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
    
    
    bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
    
    return bsonResponse;
  }

}
