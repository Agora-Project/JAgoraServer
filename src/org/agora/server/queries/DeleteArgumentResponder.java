package org.agora.server.queries;

import java.sql.SQLException;
import java.sql.Statement;
import org.agora.graph.JAgoraArgumentID;

import org.agora.graph.JAgoraGraph;
import org.agora.lib.*;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBDeleteArgument;
import org.agora.server.database.DBGraphDecoder;
import org.bson.BasicBSONObject;

public class DeleteArgumentResponder implements QueryResponder {

  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {

    BasicBSONObject bsonResponse = new BasicBSONObject();
    
    boolean verified = server.verifySession(query);
    
    if (!verified) {
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
      return bsonResponse;
    }

    verified = server.getSession(query.getInt(IJAgoraLib.USER_ID_FIELD)).hasModeratingPrivilege();
        
    if (!verified) {
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Not a Moderator.");
      return bsonResponse;
    }

    JAgoraArgumentID id = new BSONGraphDecoder().deBSONiseNodeID((BasicBSONObject) query.get(IJAgoraLib.ARGUMENT_ID_FIELD));

    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[DeleteArgumentResponder] Could not connect to database.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }

      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[DeleteArgumentResponder] Could not create statement.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }

      int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
      
      if (DBDeleteArgument.deleteArgumentFromDB(id, userID, dbc))
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
      else bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      
      return bsonResponse;

    } catch (SQLException e) {
      Log.error("[ThreadByIDResponder] Could not execute query (" + e.getMessage() + ")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }
}
