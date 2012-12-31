package org.melato.log;

/** Initializes the console logger.
 *  This class is intended to be used in an ant script to
 *  initialize console logging for code that uses this logging system. */ 
public class LogInit {
  public LogInit() {
    Log.setLogger(new ConsoleLogger());
  }

}
