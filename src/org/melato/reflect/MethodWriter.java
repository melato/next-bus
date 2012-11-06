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
package org.melato.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MethodWriter implements PropertyWriter {
  Method  method;
  
  public MethodWriter(Method method) {
    this.method = method;
  }

  
  @Override
  public Class<?> getPropertyType() {
    return method.getReturnType();
  }


  @Override
  public void apply(Object obj, Object value) {
    try {
      method.invoke(obj, value);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException( e );
    } catch (IllegalAccessException e) {
      throw new RuntimeException( e );
    } catch (InvocationTargetException e) {
      throw new RuntimeException( e );
    }
  }  
  
}
