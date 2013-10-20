package org.agora.server;

public class Options {
  
  // Server configuration
  public static int LISTEN_PORT = 1597;
  public static int NUM_WORKERS = 4;
  
  
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
