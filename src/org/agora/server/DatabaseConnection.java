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
  
  protected boolean fail() { successState = false; return false; } 
  public boolean isConnected() { return c != null; }
}
