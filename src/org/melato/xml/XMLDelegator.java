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
package org.melato.xml;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/** Parses XML files using XMLElementHandlers.
 * @author Alex Athanasopoulos
 * @date Dec 1, 2007
 */
public class XMLDelegator {
	/** Parse an XML document from an input stream.
	 * 
	 * @param rootHandler  The root handler.  This handler should
	 * expect a child element for the top element of the file.
	 * @param in  The input stream containing the XML content.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void parse( XMLElementHandler rootHandler, InputStream in )
	throws IOException, SAXException {
		InputSource source = new InputSource( in );
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			XMLReader reader = parser.getXMLReader();
			reader.setContentHandler( new DelegatingHandler( rootHandler ));
			reader.parse( source );
		} catch ( ParserConfigurationException e ) {
			throw new RuntimeException( e );
		}
	}

	/** Parse an XML document from a file.
	 *  @see parse( XMLELementHandler, File );
	 * @param rootHandler  The root handler.  This handler should
	 * expect a child element for the top element of the file.
	 * @param in  The input stream containing the XML content.
	 * @throws IOException
	 * @throws SAXException
	 */
	public static void parse( XMLElementHandler rootHandler, File file )
	throws IOException, SAXException {
		InputStream in = new FileInputStream( file );
		try {
			in = new BufferedInputStream( in );
			parse( rootHandler, in );
		} finally {
			in.close();
		}
	}
}
