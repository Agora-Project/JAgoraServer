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

import org.agora.lib.IJAgoraLib;
import org.agora.logging.Log;
import org.agora.server.*;
import org.agora.server.database.DBAddArgument;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

public class AddArgumentResponder implements QueryResponder {
  
  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
    BasicBSONObject bsonResponse = new BasicBSONObject();
    
    boolean verified = server.verifySession(query);
    
    if (!verified) {
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Invalid session ID.");
      return bsonResponse;
    }
    
    BasicBSONObject latestID = null;
    try {
      BSONObject content = (BSONObject)query.get(IJAgoraLib.CONTENT_FIELD);
      int threadID = query.getInt(IJAgoraLib.THREAD_ID_FIELD);
      int userID = query.getInt(IJAgoraLib.USER_ID_FIELD);
      
        latestID = DBAddArgument.addArgumentToDB(content, threadID, userID, server.createDatabaseConnection());
      if (!latestID.containsField("ID")) {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Database failure.");
        return bsonResponse;
      }
    } catch (SQLException e) {
      Log.error("[AddArgumentResponder] Could not execute add argument query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
    bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
    bsonResponse.put(IJAgoraLib.ARGUMENT_ID_FIELD, latestID);
    
    return bsonResponse;
  }

}
