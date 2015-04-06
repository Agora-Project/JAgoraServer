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
