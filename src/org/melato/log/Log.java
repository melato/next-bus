/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.log;

/**
 * A simple debugging log facility that can be used in different environments (e.g. Java SE, Android).
 * This is not intended to replace other logging facilities (there are too many already),
 * but to provide a very simple debugging tool.
 * Use for debugging.  When you're done, remove all Log calls.
 * @author Alex Athanasopoulos
 *
 */
public class Log {
  private static Logger logger;
  
  public static void setLogger(Logger logger) {
    Log.logger = logger;
  }
  public static void info( Object message ) {
    if ( logger != null ) {
      logger.log(String.valueOf(message));
    }
    
  }
}
