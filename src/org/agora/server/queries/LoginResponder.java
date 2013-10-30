package org.agora.server.queries;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.agora.logging.Log;
import org.agora.server.DatabaseConnection;
import org.agora.server.JAgoraServer;
import org.bson.BSONObject;

public class LoginResponder implements QueryResponder {
  
  @Override
  public BSONObject respond(BSONObject query, JAgoraServer server) {
    try (DatabaseConnection dbc = server.createDatabaseConnection()) {
      if (dbc == null) {
        Log.error("[LoginResponder] Could not connect to database.");
        return null;
      }
      
      // TODO: password is already hashed?
      String user = (String) query.get("user");
      String pass = (String) query.get("pass");
      
      Statement s = dbc.produceStatement();
      if (s == null) {
        Log.error("[LoginResponder] Could not create statement.");
        return null;
      }
      //TODO: input sanitisation
      ResultSet rs = s.executeQuery("SELECT user_ID FROM users WHERE " + 
                                    "user = '" + user + "' AND " + 
                                    "pass = '" + pass + "';");
      
      boolean logged = rs.next();
      // TODO: do things
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

}
