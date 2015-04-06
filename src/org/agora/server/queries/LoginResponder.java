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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.agora.lib.*;
import org.agora.logging.Log;
import org.agora.server.*;
import org.bson.BasicBSONObject;

public class LoginResponder implements QueryResponder {
  
  @Override
  public BasicBSONObject respond(BasicBSONObject query, JAgoraServer server) {
    BasicBSONObject bsonResponse = new BasicBSONObject();
    
    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[LoginResponder] Could not connect to database.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }
      
      // TODO: password is already hashed?
      String user = query.getString(IJAgoraLib.USER_FIELD);
      String pass = Util.hash(query.getString(IJAgoraLib.PASSWORD_FIELD));
      
      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[LoginResponder] Could not create statement.");
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
        return bsonResponse;
      }
      //TODO: input sanitisation
      String strQuery = "SELECT user_ID, type FROM users WHERE " + 
          "username = '" + user + "' AND " + 
          "password = '" + pass + "';";
      ResultSet rs = s.executeQuery(strQuery);

      
      boolean logged = rs.next();
      
      if (logged) {
        UserSession session = server.userLogin(user, rs.getInt("user_ID"), rs.getInt("type"));
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_OK);
        bsonResponse.put(IJAgoraLib.SESSION_ID_FIELD, session.getSessionID());
        bsonResponse.put(IJAgoraLib.USER_ID_FIELD, session.getUserID());
        bsonResponse.put(IJAgoraLib.USER_TYPE_FIELD, session.getUserType());
      } else {
        bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
        bsonResponse.put(IJAgoraLib.REASON_FIELD, "Wrong username/password.");
      }
      return bsonResponse;
    } catch (SQLException e) {
      Log.error("[LoginResponder] Could not execute query ("+e.getMessage()+")");
      bsonResponse.put(IJAgoraLib.RESPONSE_FIELD, IJAgoraLib.SERVER_FAIL);
      bsonResponse.put(IJAgoraLib.REASON_FIELD, "Server failure.");
      return bsonResponse;
    }
  }

}
