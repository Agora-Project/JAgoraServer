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

import org.agora.lib.*;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBDeleteArgument;
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
