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
package org.melato.util;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * implement variable substitution on strings, such as ${name} or @name@
 * The pattern is configurable.
 * @author Alexandros Athanasopoulos
 */
public class VariableSubstitution {
  /** ${name} */
  public static final String ANT_PATTERN = "\\$\\{([^\\{\\}\\$]*)\\}";
  /** @name@ */
  public static final String AT_PATTERN = "@([a-zA-Z0-9.]+)@";
	Pattern pattern;
	
	
	public VariableSubstitution(String variablePattern) {
    super();
    this.pattern = Pattern.compile( variablePattern );
  }


  /**
	 * Substitute occurences of ${name} with variables.get("name");
	 * @param text
	 * @param variables
	 * @return
	 */
	public String substitute( String text, Map<String,String> variables) {
		Matcher matcher = pattern.matcher(text);
		StringBuilder buf = new StringBuilder();
		int start = 0;
		while( matcher.find(start)) {
			buf.append(text, start, matcher.start() );
			start = matcher.end();
			String key = matcher.group(1);
			String value = variables.get(key);
			if (value == null) {
				value = "";
			}
			buf.append(value);
		}
		buf.append(text, start, text.length());
		return buf.toString();
	}

	static class PropertiesMap extends AbstractMap<String,String> implements Map<String,String> {
	  private Properties properties;

	  
    public PropertiesMap(Properties properties) {
      this.properties = properties;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String get(Object key) {
      return (String) properties.get(key);
    }
	}
  /**
   * Substitute using Properties
   */
  public String substitute( String text, Properties properties) {
    return substitute(text, new PropertiesMap(properties));
  }

	
}
