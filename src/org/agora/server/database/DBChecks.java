package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.agora.graph.JAgoraNodeID;
import org.agora.server.DatabaseConnection;

public class DBChecks {

  protected static String CHECK_ARGUMENT_QUERY = "SELECT 1 FROM arguments WHERE "
                                               + "arg_ID = ? AND source_ID = ?;";

  protected static String CHECK_ATTACK_QUERY = "SELECT 1 FROM attacks WHERE "
                                             + "arg_ID_attacker = ? AND "
                                             + "source_ID_attacker = ? AND "
                                             + "arg_ID_defender = ? AND "
                                             + "source_ID_defender = ?;";
  
  
  /**
   * Checks whether a given argument exists.
   * @param node
   * @param dbc
   * @return
   * @throws SQLException
   */
  public static boolean argumentExists(JAgoraNodeID node, DatabaseConnection dbc) throws SQLException {
    PreparedStatement ps = dbc.prepareStatement(CHECK_ARGUMENT_QUERY);
    ps.setInt(1, node.getLocalID());
    ps.setString(2, node.getSource());
    
    ps.addBatch();
    
    ResultSet rs = ps.executeQuery();
    boolean empty = rs.isAfterLast();
    
    ps.close();
    
    return !empty;
  }
    
  /**
   * Checks whether a given attack exists.
   * @param attacker
   * @param defender
   * @param dbc
   * @return
   * @throws SQLException
   */
  public static boolean attackExists(JAgoraNodeID attacker, JAgoraNodeID defender, DatabaseConnection dbc) throws SQLException {
    PreparedStatement ps = dbc.prepareStatement(CHECK_ATTACK_QUERY);
    ps.setInt(1, attacker.getLocalID());
    ps.setString(2, attacker.getSource());
    ps.setInt(3, defender.getLocalID());
    ps.setString(4, defender.getSource());
    
    ps.addBatch();
    
    ResultSet rs = ps.executeQuery();
    boolean empty = !rs.next();
    
    ps.close();
    
    return !empty;
  }
}
