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

import org.melato.convert.types.BooleanParser;
import org.melato.convert.types.ByteParser;
import org.melato.convert.types.CharParser;
import org.melato.convert.types.DoubleParser;
import org.melato.convert.types.FloatParser;
import org.melato.convert.types.IntParser;
import org.melato.convert.types.LongParser;
import org.melato.convert.types.ShortParser;
import org.melato.convert.types.StringParser;

/**
 * A ParserFactory with our built-in parsers.
 * @author Alex Athanasopoulos
 */
public class DefaultParserFactory implements TypeParserFactory {
  @Override
  public TypeParser getParser(Class<?> type) {
    if ( String.class.equals(type)) {
      return new StringParser();
    }
    if ( int.class.equals(type) || Integer.class.equals(type)) {
      return new IntParser();
    }
    if ( float.class.equals(type) || Float.class.equals(type)) {
      return new FloatParser();
    }
    if ( boolean.class.equals(type) || Boolean.class.equals(type)) {
      return new BooleanParser();
    }
    if ( long.class.equals(type) || Long.class.equals(type)) {
      return new LongParser();
    }
    if ( double.class.equals(type) || Double.class.equals(type)) {
      return new DoubleParser();
    }
    if ( char.class.equals(type) || Character.class.equals(type)) {
      return new CharParser();
    }
    if ( short.class.equals(type) || Short.class.equals(type)) {
      return new ShortParser();
    }
    if ( byte.class.equals(type) || Byte.class.equals(type)) {
      return new ByteParser();
    }
    throw new IllegalArgumentException( "Unsupported data type: " + type.getName());
  }
}
