package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.agora.graph.JAgoraNodeID;
import org.agora.server.DatabaseConnection;

public class DBAddAttack {

  
  protected static String CHECK_QUERY = "SELECT 1 FROM attacks WHERE "
                                      + "arg_ID_attacker = ? AND "
                                      + "source_ID_attacker = ? AND "
                                      + "arg_ID_defender = ? AND "
                                      + "source_ID_defender = ?;";
  
  
  protected static String ADD_QUERY = "INSERT INTO attacks (arg_ID_attacker, source_ID_attacker,"
                                                         + "arg_ID_defender, source_ID_defender,"
                                                         + "user_ID)"
                                                         + "VALUES (?, ?, ?, ?, ?);";
  
  
  
  protected static boolean checkRecord(JAgoraNodeID attacker, JAgoraNodeID defender, DatabaseConnection dbc) throws SQLException {
    PreparedStatement ps = dbc.prepareStatement(CHECK_QUERY);
    ps.setInt(1, attacker.getLocalID());
    ps.setString(2, attacker.getSource());
    ps.setInt(3, defender.getLocalID());
    ps.setString(4, defender.getSource());
    
    ps.addBatch();
    
    ResultSet rs = ps.executeQuery();
    boolean res = rs.isAfterLast();
    
    ps.close();
    
    return res;
  }
  
  public static boolean addAttackToDB(int userID, JAgoraNodeID attacker, JAgoraNodeID defender, DatabaseConnection dbc) throws SQLException {
    // TODO: This check should somehow say that the record already exists.
    if (!checkRecord(attacker, defender, dbc))
      return false;
    
    PreparedStatement ps = dbc.prepareStatement(ADD_QUERY);
    ps.setInt(1, attacker.getLocalID());
    ps.setString(2, attacker.getSource());
    ps.setInt(3, defender.getLocalID());
    ps.setString(4, defender.getSource());
    ps.setInt(5, userID);
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();

    return res[0] != 0;
  }
}
