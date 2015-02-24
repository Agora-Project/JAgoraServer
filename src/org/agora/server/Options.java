package org.agora.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.agora.logging.Log;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.InputStream;

public class Options {

  // Server configuration
  public static String CONF_FILE = "agora.conf";

  public static int NUM_WORKERS = 4;
  public static int MAX_INCOMING_BSON_SIZE = 10 * 1024; // 10kb?

  public static int SESSION_BYTE_LENGTH = 8;

  /**
   * This is used to set the ID of this server. It must correspond to the url at
   * which the Agora server can be found.
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
  public static String DEBUG_FILE = "/var/log/agorad.dbg";
  public static String ERROR_FILE = "/var/log/agorad.err";

  /**
   * Parses all command line options.
   *
   * @param args
   */
  public static void parseOptions(String[] args) {
    for (String opt : args) {
      parseOption(opt);
    }
  }

  /**
   * Parses a single command-line option.
   *
   * @param option
   */
  public static void parseOption(String option) {
    if (option.indexOf('=') >= 0) { // Assignment options
      String[] tokens = option.split("=");
      if (tokens.length != 2) {
        Log.error("Could not parse option '" + option + "'");
        return;
      }
      if (tokens[0].equals("-db")) {
        DB_FILE = tokens[1];
        Log.log("Database file set to '" + tokens[1] + "'");
      } else if (tokens[0].equals("-cfg")) {
        CONF_FILE = tokens[1];
        Log.log("Configuration file set to '" + tokens[1] + "'");
      }
    } else { // No assignment option
    }

  }

  /**
   * Reads Agora Server options from default configuration file.
   */
  public static void readAgoraConfFromFile() {
    readAgoraConfFromFile(CONF_FILE);
  }

  /**
   * Reads Agora Server options from given configuration file.
   *
   * @param Configuration file.
   */
  public static void readAgoraConfFromFile(String file) {
    String json;
    try {
      Scanner s = new Scanner(new File(file));
      json = s.useDelimiter("\\A").next();
      s.close();
    } catch (FileNotFoundException e) {
      Log.error("[JAgoraServer] Could not find configuration file " + file);
      return;
    }

    // Not even checking whether they exist. They must - boom!
    DBObject bson = (DBObject) JSON.parse(json);
    SERVER_URL = (String) bson.get("url");
    NUM_WORKERS = (Integer) bson.get("number of workers");
    SESSION_BYTE_LENGTH = (Integer) bson.get("session token size");
    REQUEST_WAIT = (Integer) bson.get("request timeout");
    LOG_MESSAGES = (Boolean) bson.get("log");
    DEBUG_MESSAGES = (Boolean) bson.get("debug log");
    ERROR_MESSAGES = (Boolean) bson.get("error log");
    LOG_FILE = (String) bson.get("log file");
    DEBUG_FILE = (String) bson.get("debug log file");
    ERROR_FILE = (String) bson.get("error log file");
  }

  public static void readAgoraConfFromStream(InputStream is) {
    String json = null;
    try (Scanner s = new Scanner(is)) {
      json = s.useDelimiter("\\A").next();
    } catch (Exception e) {
      Log.error("[Options] Could not read Agora Conf from stream: " + e.getMessage());
      throw e;
    }

    // Not even checking whether they exist. They must - boom!
    DBObject bson = (DBObject) JSON.parse(json);
    SERVER_URL = (String) bson.get("url");
    NUM_WORKERS = (Integer) bson.get("number of workers");
    SESSION_BYTE_LENGTH = (Integer) bson.get("session token size");
    REQUEST_WAIT = (Integer) bson.get("request timeout");
    LOG_MESSAGES = (Boolean) bson.get("log");
    DEBUG_MESSAGES = (Boolean) bson.get("debug log");
    ERROR_MESSAGES = (Boolean) bson.get("error log");
    LOG_FILE = (String) bson.get("log file");
    DEBUG_FILE = (String) bson.get("debug log file");
    ERROR_FILE = (String) bson.get("error log file");
  }

  /**
   * Reads Agora Server database options from default configuration file.
   */
  public static void readDBConfFromFile() {
    readDBConfFromFile(DB_FILE);
  }

  /**
   * Reads Agora Server database options from given configuration file.
   *
   * @param Configuration file.
   */
  public static void readDBConfFromFile(String file) {
    String json;
    try {
      Scanner s = new Scanner(new File(file));
      json = s.useDelimiter("\\A").next();
      s.close();
    } catch (FileNotFoundException e) {
      Log.error("[JAgoraServer] Could not find database configuration file " + file);
      return;
    }

    DBObject bson = (DBObject) JSON.parse(json);
    DB_URL = (String) bson.get("url");
    DB_USER = (String) bson.get("user");
    DB_PASS = (String) bson.get("pass");
  }

  public static void readDBConfFromStream(InputStream is) {
    String json = null;
    try (Scanner s = new Scanner(is)) {
      json = s.useDelimiter("\\A").next();
    } catch (Exception e) {
      Log.error("[Options] Could not read Database Conf from stream: " + e.getMessage());
      throw e;
    }

    DBObject bson = (DBObject) JSON.parse(json);
    DB_URL = (String) bson.get("url");
    DB_USER = (String) bson.get("user");
    DB_PASS = (String) bson.get("pass");
  }
}
