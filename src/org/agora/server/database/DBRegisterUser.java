package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.agora.server.DatabaseConnection;

public class DBRegisterUser {

  protected static String REGISTER_QUERY = "INSERT INTO users (username, password, email) VALUES (?, ?, ?);";
  
  public static boolean registerUserInDB(String username, String password, String email, DatabaseConnection dbc) throws SQLException {
    
    PreparedStatement ps = dbc.prepareStatement(REGISTER_QUERY);
    ps.setString(1, username);
    ps.setString(2, password);
    ps.setString(3, email);
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();

    return res[0] != 0;
  }
}
