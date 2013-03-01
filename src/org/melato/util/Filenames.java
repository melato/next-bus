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
package org.melato.util;

import java.io.File;

public class Filenames {
	public static String getExtension( File file ) {
		String name = file.getName();
		int dot = name.indexOf( '.' );
		return dot >= 0 ? name.substring( dot + 1 ) : null;
	}
  public static String getBasename( String name ) {
    int dot = name.indexOf( '.' );
    return dot >= 0 ? name.substring( 0, dot ) : name;
  }
	public static String getBasename( File file ) {
	  return getBasename(file.getName());
	}
	public static File replaceExtension( File file, String newExtension ) {
		String name = getBasename( file ) + "." + newExtension;
		return new File( file.getParentFile(), name );
	}

}
