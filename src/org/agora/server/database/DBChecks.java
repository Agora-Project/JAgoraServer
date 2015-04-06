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

package org.agora.server.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.agora.graph.JAgoraArgumentID;
import org.agora.server.DatabaseConnection;
import org.agora.server.Options;
import org.bson.BasicBSONObject;

public class DBChecks {

  protected static String CHECK_ARGUMENT_QUERY = "SELECT 1 FROM arguments WHERE "
                                               + "arg_ID = ? AND source_ID = ?;";

  protected static String CHECK_ATTACK_QUERY = "SELECT 1 FROM attacks WHERE "
                                             + "arg_ID_attacker = ? AND "
                                             + "source_ID_attacker = ? AND "
                                             + "arg_ID_defender = ? AND "
                                             + "source_ID_defender = ?;";
  
  protected static String CHECK_LATEST_ARGUMENT_QUERY = "SELECT LAST_INSERT_ID();";
  
  
  /**
   * Checks whether a given argument exists.
   * @param node
   * @param dbc
   * @return
   * @throws SQLException
   */
  public static boolean argumentExists(JAgoraArgumentID node, DatabaseConnection dbc) throws SQLException {
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
  public static boolean attackExists(JAgoraArgumentID attacker, JAgoraArgumentID defender, DatabaseConnection dbc) throws SQLException {
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
  
  public static BasicBSONObject latestArgument(DatabaseConnection dbc) throws SQLException {
      PreparedStatement ps = dbc.prepareStatement(CHECK_LATEST_ARGUMENT_QUERY);
      
      ps.addBatch();
    
      ResultSet rs = ps.executeQuery();
      
      BasicBSONObject result = new BasicBSONObject();
      result.put("Source", Options.SERVER_URL);
      rs.next();
      System.out.println(rs.getInt(1));
      result.put("ID", rs.getInt(1));
      ps.close();
      
      return result;
  }
}
