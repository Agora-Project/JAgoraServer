/*

Copyright (C) 2015 Agora Communication Corporation

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/

package org.agora.server.queries;

import java.sql.SQLException;

import org.agora.graph.JAgoraArgumentID;
import org.agora.lib.BSONGraphDecoder;
import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBAddVote;
import org.bson.BasicBSONObject;

public class AddAttackVoteResponder implements QueryResponder {
  
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
      BSONGraphDecoder dec = new BSONGraphDecoder();
      JAgoraArgumentID attackerID = dec.deBSONiseNodeID((BasicBSONObject)query.get(IJAgoraLib.ATTACKER_FIELD));
      JAgoraArgumentID defenderID = dec.deBSONiseNodeID((BasicBSONObject)query.get(IJAgoraLib.DEFENDER_FIELD));
      int voteType = (int) query.get(IJAgoraLib.VOTE_TYPE_FIELD);
      int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
      
      boolean res = DBAddVote.addAttackVoteToDB(attackerID, defenderID, voteType, userID, server.createDatabaseConnection());
      if (!res) {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Database failure.");
        return bsonResponse;
      }
    } catch (SQLException e) {
      Log.error("[AddAttackVoteResponder] Could not execute add argument vote query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
    
    
    bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
    
    return bsonResponse;
  }

}
