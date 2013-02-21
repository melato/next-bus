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
package org.melato.convert.xml;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class FieldPropertyReflector implements PropertyReflector {
  private Field[] fields;
  
  @Override
  public void setBeanClass(Class<?> beanClass) {
    List<Field> properties = new ArrayList<Field>();
    for( Field field: beanClass.getFields() ) {
      int modifiers = field.getModifiers();
      if ( Modifier.isPublic(modifiers) && ! Modifier.isStatic(modifiers) ) { 
          properties.add(field);        
      }
    }
    fields = properties.toArray(new Field[0]);
  }

  @Override
  public int getPropertyCount() {
    return fields.length;
  }

  @Override
  public Class<?> getPropertyType(int index) {
    return fields[index].getType();
  }
  
  @Override
  public String getPropertyName(int index) {
    return fields[index].getName();
  }

  @Override
  public void setProperty(Object bean, int index, Object value) throws Exception {
    fields[index].set(bean,  value);
  }

  @Override
  public Object getProperty(Object bean, int index) throws Exception {
    return fields[index].get(bean);
  }

}
