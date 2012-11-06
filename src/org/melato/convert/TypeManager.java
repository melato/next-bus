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

/** Maps names and/or classes to T */ 
public abstract class TypeManager<T> {
  private Map<String,T> handlersForName = new HashMap<String,T>();
  private Map<Class<?>,T> handlersForType = new HashMap<Class<?>,T>();
  
  /** Specify the handler to use for a field of a given type. */
  public void set(Class<?> type, T handler) {
    handlersForType.put(type,  handler);    
  }

  /** Specify the handler to use for a specific field. */
  public void set(String fieldName, T handler) {
    handlersForName.put(fieldName,  handler);    
  }

  protected abstract T getDefault(Class<?> type);
    
  public T get(Class<?> type) {
    T handler = handlersForType.get(type);
    if ( handler != null || handlersForType.containsKey(type))
      return handler;
    return getDefault(type);
  }

  public T get(String name, Class<?> type ) {
    T handler = handlersForName.get(name);
    if ( handler != null || handlersForType.containsKey(name))
      return handler;
    return getDefault(type);
  }
}
