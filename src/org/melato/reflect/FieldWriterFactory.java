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
package org.melato.reflect;


public class FieldWriterFactory implements PropertyWriterFactory {
  Class<?> cls;
  
  public FieldWriterFactory(Class<?> cls) {
    this.cls = cls;
  }

  @Override
  public PropertyWriter getWriter(String name) {
    try {
      return new FieldWriter(cls, name);
    } catch (NoSuchFieldException e) {
      System.err.println( "Ignoring unknown field: " + cls.getName() + "." + name);
      return new NullPropertyWriter();
    }
  }
  
}
