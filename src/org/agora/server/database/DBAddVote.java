package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.agora.graph.JAgoraNodeID;
import org.agora.server.DatabaseConnection;

public class DBAddVote {

  protected static String ADD_ARGUMENT_VOTE_QUERY = "INSERT INTO votes (type, user_ID, source_ID, arg_ID) VALUES (?, ?, ?, ?);";
  
  public static boolean addArgumentVoteToDB(JAgoraNodeID nodeID, int voteType, int userID, DatabaseConnection dbc) throws SQLException {
    // TODO: return error somehow.
    if(!DBChecks.argumentExists(nodeID, dbc))
      return false;
    
    PreparedStatement ps = dbc.prepareStatement(ADD_ARGUMENT_VOTE_QUERY);
    ps.setInt(1, voteType);
    ps.setInt(2, userID);
    ps.setString(3, nodeID.getSource());
    ps.setInt(4, nodeID.getLocalID());
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();
    
    // TODO: can't it happen that res.length == 0?
    return res[0] != 0;
  }
  
  
  protected static String ADD_ATTACK_VOTE_QUERY = "INSERT INTO votes (type, user_ID, source_ID_attacker, arg_ID_attacker, source_ID_defender, arg_ID_defender) VALUES (?, ?, ?, ?);";
  
  public static boolean addAttackVoteToDB(JAgoraNodeID attackerID, JAgoraNodeID defenderID, int voteType, int userID, DatabaseConnection dbc) throws SQLException {
    // TODO: return error somehow.
    if(!DBChecks.attackExists(attackerID, defenderID, dbc)) return false;
    
    PreparedStatement ps = dbc.prepareStatement(ADD_ARGUMENT_VOTE_QUERY);
    ps.setInt(1, voteType);
    ps.setInt(2, userID);
    ps.setString(3, attackerID.getSource());
    ps.setInt(4, attackerID.getLocalID());
    ps.setString(5, defenderID.getSource());
    ps.setInt(6, defenderID.getLocalID());
    
    ps.addBatch();
    
    int[] res = ps.executeBatch();
    
    ps.close();
    dbc.close();
    
    // TODO: can't it happen that res.length == 0?
    return res[0] != 0;
  }
}
