package org.agora.server.queries;

import org.agora.lib.IJAgoraLib;
import org.agora.server.*;
import org.bson.BasicBSONObject;

public class LogoutResponder implements QueryResponder {
  
  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
    BasicBSONObject bsonResponse = new BasicBSONObject();
    
    boolean verified = server.verifySession(query);
    
    if (!verified) {
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
      return bsonResponse;
    }
    
    server.logoutUser(query.getInt(IJAgoraLib.USER_ID_FIELD));
    bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
    
    return bsonResponse;
  }

}
