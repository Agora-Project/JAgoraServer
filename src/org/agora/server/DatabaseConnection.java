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

package org.agora.server;

import java.sql.*;

import org.agora.logging.*;



public class DatabaseConnection implements java.lang.AutoCloseable {
  protected String url;
  protected String user;
  protected String pass;
  
  protected Connection c;
  
  protected boolean successState; 
  
  public DatabaseConnection(String url, String user, String pass){
    c = null;
    this.url = url;
    this.user = user;
    this.pass = pass;
    successState = true;
  }
  
  /**
   * Connect to database.
   * @return Whether connection was successful.
   */
  public boolean open() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      c = DriverManager.getConnection(url, user, pass);
      return true;
    } catch (ClassNotFoundException e) {
      Log.error("[DatabaseConnection] Unable to load com.mysql.jdbc.Driver.");
      Log.error(e.getMessage());
    } catch (SQLException e) {
      Log.error("[DatabaseConnection] Problem connecting to '" + url + "'");
      Log.error(e.getMessage());
    }
    return fail();
  }
  
  /**
   * Terminates current connection (if there is one).
   * @return
   */
  public void close() {
    if (!isConnected()) {
      fail();
      return;
    }
    
    try {
      c.close();
      return;
    } catch (SQLException e) {
      Log.error("[DatabaseConnection] Problems disconnecting from database.");
      Log.error(e.getMessage());
    }
    fail();
    return;
  }
  
  /**
   * Produces a statement. Don't forget to close it!
   * @return
   */
  public Statement produceStatement() {
    if (!isConnected()) {
      Log.error("[DatabaseConnection] Tried to perform query while not connected.");
      return null;
    }
    
    try {
      return c.createStatement();
    } catch (SQLException e) {
      Log.error("[DatabaseConnection] Could not create statement.");
    }
    return null;
  }
  
  /**
   * Produces a statement. Don't forget to close it!
   * @return
   */
  public PreparedStatement prepareStatement(String statement) {
    if (!isConnected()) {
      Log.error("[DatabaseConnection] Tried to perform query while not connected.");
      return null;
    }
    
    try {
      return c.prepareStatement(statement);
    } catch (SQLException e) {
      Log.error("[DatabaseConnection] Could not create statement.");
    }
    return null;
  }
  
  public boolean commit() {
    try {
      c.commit();
      return true;
    } catch (SQLException e) {
      Log.error("[DatabaseConnection] Could not commit.");
      return false;
    }
  }
  
  // TODO: this is gross, right?
  protected boolean fail() { successState = false; return false; } 
  public boolean isConnected() { return c != null; }
}
