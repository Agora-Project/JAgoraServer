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
    BasicBSONObject bson = new BasicBSONObject();
    
    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[LoginResponder] Could not connect to database.");
        bson.put("response", IJAgoraLib.SERVER_FAIL);
        bson.put("reason", "Server failure.");
        return bson;
      }
      
      // TODO: password is already hashed?
      String user = (String) query.get("user");
      String pass = (String) query.get("pass");
      
      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[LoginResponder] Could not create statement.");
        bson.put("response", IJAgoraLib.SERVER_FAIL);
        bson.put("reason", "Server failure.");
        return bson;
      }
      //TODO: input sanitisation
      String strQuery = "SELECT user_ID FROM users WHERE " + 
          "username = '" + user + "' AND " + 
          "password = '" + pass + "';";
      ResultSet rs = s.executeQuery(strQuery);
      
      boolean logged = rs.next();
      
      if (logged) {
        UserSession session = server.userLogin(user, rs.getInt("user_ID"));
        bson.put("response", IJAgoraLib.SERVER_OK);
        bson.put("token", session.getToken());
        bson.put("id", session.getUserID());
      } else {
        bson.put("response", IJAgoraLib.SERVER_FAIL);
        bson.put("reason", "Wrong username/password.");
      }
      return bson;
    } catch (SQLException e) {
      Log.error("[LoginResponder] Could not execute query.");
      Log.error(e.getMessage());
      bson.put("response", IJAgoraLib.SERVER_FAIL);
      bson.put("reason", "Server failure.");
      return bson;
    }
  }

}
