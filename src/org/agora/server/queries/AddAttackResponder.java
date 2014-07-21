package org.agora.server.queries;

import java.sql.SQLException;

import org.agora.graph.JAgoraArgumentID;
import org.agora.lib.BSONGraphDecoder;
import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBAddAttack;
import org.bson.BasicBSONObject;

public class AddAttackResponder implements QueryResponder {
  
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
      int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
      BSONGraphDecoder bdec = new BSONGraphDecoder();
      JAgoraArgumentID attacker = bdec.deBSONiseNodeID((BasicBSONObject)query.get(IJAgoraLib.ATTACKER_FIELD));
      JAgoraArgumentID defender = bdec.deBSONiseNodeID((BasicBSONObject)query.get(IJAgoraLib.DEFENDER_FIELD));
      
      boolean res = DBAddAttack.addAttackToDB(userID, attacker, defender, server.createDatabaseConnection());
      if (res) {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
      } else {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Attack already existed (with high probability!)");
      }
      
      return bsonResponse;
    } catch (SQLException e) {
      Log.error("[AddArgumentResponder] Could not execute add attack query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }

}
