package org.agora.server;

public class Options {
  
  // Server configuration
  public static int NUM_WORKERS = 4;
  public static int MAX_INCOMING_BSON_SIZE = 10*1024; // 10kb?
  
  public static int SESSION_BYTE_LENGTH = 8;
  
  // Database information
  public static String DB_FILE = "database.conf";
  public static String DB_URL = null;
  public static String DB_USER = null;
  public static String DB_PASS = null;

  // Worker configuration
  public static long REQUEST_WAIT = 1000;
  
  
  // Logging options
  public static boolean LOG_MESSAGES = true;
  public static boolean DEBUG_MESSAGES = true;
  public static boolean ERROR_MESSAGES = true;
  
  public static String LOG_FILE = "/var/log/agorad.log";
  public static String ERROR_FILE = "/var/log/agorad.err";
  public static String DEBUG_FILE = "/var/log/agorad.dbg";
  
  
}
