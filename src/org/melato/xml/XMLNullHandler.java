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
package org.melato.xml;

import org.xml.sax.SAXException;

/** An element handler that does nothing.
 **/
public class XMLNullHandler implements XMLElementHandler {
	/** A static null handler. */
	public static final XMLElementHandler INSTANCE = new XMLNullHandler();

	/** Return a static null handler. */
	public static XMLElementHandler getInstance() {
		return INSTANCE;
	}
	
	public void end() throws SAXException {
	}

	public XMLElementHandler getHandler(XMLTag tag) {
		return INSTANCE;
	}

	public void start(XMLTag tag) throws SAXException {
	}

	public void characters(char[] ch, int start, int length) throws SAXException {
	}

}
