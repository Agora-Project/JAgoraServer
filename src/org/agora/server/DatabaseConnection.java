package org.agora.server;

import java.sql.*;

import org.agora.logging.*;



public class DatabaseConnection {
  protected String url;
  protected String user;
  protected String pass;
  
  protected Connection c;
  
  public DatabaseConnection(String url, String user, String pass){
    c = null;
    this.url = url;
    this.user = user;
    this.pass = pass;
  }
  
  /**
   * Connect to database.
   * @return Whether connection was successful.
   */
  public boolean initiateConnection() {
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
    return false;
  }
  
  /**
   * Terminates current connection (if there is one).
   * @return
   */
  public boolean terminateConnection() {
    if (!isConnected())
      return false;
    
    try {
      c.close();
      return true;
    } catch (SQLException e) {
      Log.error("[DatabaseConnection] Problems disconnecting from database.");
      Log.error(e.getMessage());
    }
    return false;
  }
  
  /**
   * Produces a statement. Don't forget to close it!
   * @return
   */
  public Statement getStatement() {
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
  
  
  public boolean isConnected() { return c != null; }
}
