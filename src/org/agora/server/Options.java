package org.agora.server;

import org.agora.logging.Log;

public class Options {
  
  // Server configuration
  // TODO: read all configurations from file, not code.
  public static String CONF_FILE = "agora.conf";
  
  public static int NUM_WORKERS = 4;
  public static int MAX_INCOMING_BSON_SIZE = 10*1024; // 10kb?
  
  public static int SESSION_BYTE_LENGTH = 8;
  
  /**
   * This is used to set the ID of this server. It must correspond to the url
   * at which the Agora server can be found.
   */
  public static String SERVER_URL = null;
  
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
  
  public static void parseOptions(String[] args) {
    for (String opt: args)
      parseOption(opt);
  }
  
  public static void parseOption(String option) {
    if (option.indexOf('=') >= 0) { // Assignment options
       String[] tokens = option.split("=");
       if (tokens.length != 2) {
         Log.error("Could not parse option '"+option+"'");
         return;
       }
       if (tokens[0].equals("-db")) {
         DB_FILE = tokens[1];
         Log.log("Database file set to '"+tokens[1]+"'");
       } else if (tokens[0].equals("-cfg")) {
         CONF_FILE = tokens[1];
         Log.log("Configuration file set to '"+tokens[1]+"'");
       }
    } else { // No assignment option
    }
    
  }
}




