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
package org.melato.update;

import org.melato.reflect.External;


/** Specifies information about a downloadable file. */
public class UpdateFile {
  /** The code name of the file.  It does not need to be the actual filename */
  @External public String name;

  /** The version of the update.
   *  An update is considered current if the installed version is equal to the available version.
   *  Otherwise it is considered out-of-date.
   *  There is no need for version identifiers to be increasing.  They can be random guids.
   */
  @External public String version;

  /** The url to get a new file from. */
  @External public String url;
  
  /** The size of the file that the url points to, in bytes. */
  @External public int    size;
  
  /** How often to check for new updates, in hours.
   * The application should not check more often than that.
   * */
  @External public int    frequencyHours;
  
  /** Short description of the update. */
  @External public String note;
  
  
  @Override
  public String toString() {
    return getName();
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  public String getVersion() {
    return version;
  }

  public int getSize() {
    return size;
  }

  public String getNote() {
    return note;
  }

  public int getFrequencyHours() {
    return frequencyHours;
  }
}
