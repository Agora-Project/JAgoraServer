package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.agora.graph.JAgoraNodeID;
import org.agora.server.DatabaseConnection;

public class DBAddAttack {

  static String x = "a.arg_ID_attacker AS arg_ID_attacker, a.source_ID_attacker AS source_ID_attacker, "
      + "a.arg_ID_defender AS arg_ID_defender, a.source_ID_defender AS source_ID_defender, "
      + "arg_att.thread_ID AS att_thread_ID, arg_def.thread_ID AS def_thread_ID, ";
  
  protected static String ADD_QUERY = "INSERT INTO attacks (arg_ID_attacker, source_ID_attacker,"
                                                         + "arg_ID_defender, source_ID_defender,"
                                                         + "user_ID)"
                                                         + "VALUES (?, ?, ?, ?, ?);";
  
  public static boolean addAttackToDB(int userID, JAgoraNodeID attacker, JAgoraNodeID defender, DatabaseConnection dbc) throws SQLException {
    
    PreparedStatement ps = dbc.prepareStatement(ADD_QUERY);
    ps.setInt(1, attacker.getLocalID());
    ps.setString(2, attacker.getSource());
    ps.setInt(3, defender.getLocalID());
    ps.setString(4, defender.getSource());
    ps.setInt(5, userID);
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    // TODO: needed?
    // dbc.commit(); // This gives an error.
    dbc.close();

    return res[0] != 0;
  }
}
