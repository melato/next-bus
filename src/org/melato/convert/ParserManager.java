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
package org.melato.convert;

import java.util.HashMap;
import java.util.Map;


public class ParserManager implements TypeParserFactory {
  private Map<String,TypeParser> parsersForName = new HashMap<String,TypeParser>();
  private Map<Class<?>,TypeParser> parsersForType = new HashMap<Class<?>,TypeParser>();
  private TypeParserFactory factory = new DefaultParserFactory();
  
  /** Specify the parser to use for a field of a given type. */
  public void setParser(Class<?> type, TypeParser parser) {
    parsersForType.put(type,  parser);    
  }

  /** Specify the parser to use for a specific field. */
  public void setParser(String fieldName, TypeParser parser) {
    parsersForName.put(fieldName,  parser);    
  }
    
  @Override
  public TypeParser getParser(Class<?> type) {
    TypeParser parser = parsersForType.get(type);
    if ( parser != null || parsersForType.containsKey(type))
      return parser;
    parser = factory.getParser(type);
    return parser;    
  }

  public TypeParser getParser(String name, Class<?> type ) {
    TypeParser parser = parsersForName.get(name);
    if ( parser != null || parsersForType.containsKey(name))
      return parser;
    return getParser(type);
  }
}
