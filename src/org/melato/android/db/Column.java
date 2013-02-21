/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.android.db;

/** Helper class for defining an SQLite column */
public class Column {
  public final String name;
  public final String type;

  public Column(String name, String type) {
    super();
    this.name = name;
    this.type = type;
  }

  @Override
  public String toString() {
    return name;
  }
  
  public String createClause() {
    return name + " " + type;
  }
  
  /** Generate a create table statement for a table with the given columns. */
  public static String createStatement(String table, Column[] columns) {
    StringBuilder buf = new StringBuilder();
    buf.append( "CREATE TABLE " + table + "(");
    for( int i = 0; i < columns.length; i++ ) {
      if ( i > 0 ) {
        buf.append( ", " );
      }
      buf.append( columns[i].createClause() );
    }
    buf.append( ")" );
    return buf.toString();
  }
}
