package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.agora.graph.JAgoraArgumentID;
import org.agora.server.DatabaseConnection;

public class DBAddAttack {
  
  
  protected static String ADD_QUERY = "INSERT INTO attacks (arg_ID_attacker, source_ID_attacker,"
                                                         + "arg_ID_defender, source_ID_defender,"
                                                         + "user_ID)"
                                                         + "VALUES (?, ?, ?, ?, ?);";
  
  
  
  public static boolean addAttackToDB(int userID, JAgoraArgumentID attacker, JAgoraArgumentID defender, DatabaseConnection dbc) throws SQLException {
    // TODO: These checks should somehow say that the record already exists.
    if (!DBChecks.argumentExists(attacker, dbc))        return false;
    if (!DBChecks.argumentExists(defender, dbc))        return false;
    if (DBChecks.attackExists(attacker, defender, dbc)) return false;
    
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
