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
package org.melato.convert.xml;

import org.melato.convert.FormatManager;
import org.melato.convert.TypeFormatter;
import org.melato.xml.XMLWriter;

/**
 * Writes beans to XML
 */
public class ReflectionWriter<T> {
  private FormatManager formatManager = new FormatManager();
  private XMLWriter xml;
  private String tag;

  private PropertyReflector reflector;
  private TypeFormatter[] formatters;

  /**
   * @param beanClass  The class to use in order to construct beans.
   * @param collector  Where to put the resulting beans.
   */
  public ReflectionWriter(Class<T> beanClass, PropertyReflector reflector, XMLWriter xml, String tag) {
    this.reflector = reflector;
    this.xml = xml;
    this.tag = tag;
    reflector.setBeanClass(beanClass);
    formatters = new TypeFormatter[reflector.getPropertyCount()];
    for( int i = 0; i < formatters.length; i++ ) {
      formatters[i] = formatManager.get(reflector.getPropertyName(i), reflector.getPropertyType(i));
    }
  }
  
  public void write(T bean) {
    xml.println();
    xml.tagOpen(tag);
    for( int i = 0; i < formatters.length; i++ ) {
      String name = reflector.getPropertyName(i);
      if ( formatters[i] != null ) {
        try {
          Object value = reflector.getProperty(bean,  i );
          if ( value != null ) {
            xml.println();
            xml.tagOpen(name);
            xml.text(formatters[i].format(value));
            xml.tagEnd(name);
          }
        } catch (Exception e) {
          throw new RuntimeException( e );
        }
      }
    }
    xml.tagEnd(tag);    
  }

  public FormatManager getFormatManager() {
    return formatManager;
  }

  public void setFormatManager(FormatManager formatManager) {
    this.formatManager = formatManager;
  }
}
