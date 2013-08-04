/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.client;

/** A help item.  Used to display a page of help information in an android activity. */
public class HelpItem {
  private String name;
  private String title;
  private String text;
  private String node;
    
  /** The title of the item.  Should be short enough for an activity title.  */ 
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  /** Internal optional mnemonic id for the item. */
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  /** The text content (body) of the item.  */
  public String getText() {
    return text;
  }
  public void setText(String text) {
    this.text = text;
  }
  /** Internal persistent id for the item. */
  public String getNode() {
    return node;
  }
  public void setNode(String node) {
    this.node = node;
  }
}
