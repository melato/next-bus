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

import java.util.Collection;

/**
 * An XML parser that uses reflection to construct beans.
 * Use with XMLDelegator.
 * It maps xml element tags to bean property names.
 * It uses customizable type converters to convert text to property types.
 * @author Alex Athanasopoulos
 *
 * @param <T>
 */
public class FieldHandler<T> extends ReflectionHandler<T> {

  /**
   * @param beanClass  The class to use in order to construct beans.
   * @param collector  Where to put the resulting beans.
   */
  public FieldHandler(Class<T> beanClass, Collection<T> collector) {
    super(beanClass, new FieldPropertyReflector(), collector);
  }

}
