
package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.agora.graph.JAgoraArgumentID;
import org.agora.server.DatabaseConnection;
import static org.agora.server.database.DBAddAttack.ADD_QUERY;

/**
 *
 * @author angle
 */


public class DBDeleteArgument {
  
  protected static String DELETE_ARGUMENT_QUERY = "DELETE FROM arguments " +
                                         "WHERE arg_id = ?;";
  
  protected static String DELETE_ATTACKS_QUERY = "DELETE FROM arguments WHERE (arg_ID_attacker = ? " + 
                                                "AND source_ID_attacker = ?) OR (arg_ID_defender = ? AND source_ID_defender = ?)";
  
  public static boolean deleteArgumentFromDB(JAgoraArgumentID id, int userID, DatabaseConnection dbc) throws SQLException {
    
    PreparedStatement ps = dbc.prepareStatement(DELETE_ARGUMENT_QUERY);
    ps.setInt(1, id.getLocalID());
    ps.setString(2, id.getSource());
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    if(res[0] == 0) return false;
    
    ps = dbc.prepareStatement(DELETE_ATTACKS_QUERY);
    ps.setInt(1, id.getLocalID());
    ps.setString(2, id.getSource());
    ps.setInt(3, id.getLocalID());
    ps.setString(4, id.getSource());
    
    ps.addBatch();
    
    res = ps.executeBatch();
    
    ps.close();
    dbc.close();

    return res[0] != 0;
  }
  
}
