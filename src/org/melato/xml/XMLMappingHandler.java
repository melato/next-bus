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

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.SAXException;

/**
 * An XML Element handler that delegates tags to handlers through a map of handlers.
 * It also delegates its element body to another handler. 
 * @author Alex Athanasopoulos
 * @date Dec 1, 2007
 */
public class XMLMappingHandler implements XMLElementHandler {
	private Map<String,XMLElementHandler> handlerMap = new HashMap<String,XMLElementHandler>();
	private	XMLElementHandler	bodyHandler = XMLNullHandler.getInstance();
	private	boolean			recursive;
	private Logger logger = Logger.getLogger(XMLMappingHandler.class.getName());
	
	private static class EmptyPathException extends RuntimeException {
		private static final long serialVersionUID = 1L;		
	}

	/** Associate an XML sub tag with a handler. */
	public void setHandler( String tag, XMLElementHandler handler ) {
		handlerMap.put( tag, handler );
	}
	
	/**
	 * Recursive mapping.  The mapping is valid for any level.
	 * For example if a handler is defined for element "x",
	 * but element "x" has path "a/b/c/x".
	 * The handler for x is still called.
	 * @param recursive
	 */
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	/** Define a handler for the element body.
	 *  It handles the start, characters, and end calls. */
	public void setBodyHandler( XMLElementHandler handler ) {
		this.bodyHandler = handler;
	}

	public void setPathHandler( String[] path, XMLElementHandler handler ) {
		XMLMappingHandler leafHandler = this;
		for( int i = 0; i < path.length - 1; i++ ) {
			String tag = path[i];
			if ( tag.length() == 0 ) {
				throw new EmptyPathException();
			}
			XMLElementHandler h = leafHandler.handlerMap.get( tag );
			if ( h == null ) {
				h = new XMLMappingHandler();
				leafHandler.setHandler( tag, h );
			} else if ( ! (h instanceof XMLElementHandler) ) {
				throw new RuntimeException( "not a mapping handler" );
			}
			leafHandler = (XMLMappingHandler) h;
		}
		leafHandler.setHandler( path[path.length-1], handler );
	}
		
	public void setPathHandler( String path, XMLElementHandler handler ) {
		String[] pp = path.split( "/" );
		try {
			setPathHandler( pp, handler );
		} catch( EmptyPathException e ) {
			throw new IllegalArgumentException( "Empty path component in: " + path );
		}
	}
	
  public void setPathHandler( XMLElementHandler handler, String ... paths ) {
    setPathHandler( paths, handler);
  }

  public XMLElementHandler getHandler(XMLTag tag) {
		XMLElementHandler handler = handlerMap.get( tag.getName() );
		if ( handler == null ) {
			if ( ! recursive) {
				handler = XMLNullHandler.getInstance();
			} else {
				XMLMappingHandler innerHandler = new XMLMappingHandler();
				innerHandler.handlerMap = this.handlerMap;
				innerHandler.recursive = true;
				handler = innerHandler;
			}
		}
		if ( logger.isLoggable(Level.FINE )) {
			logger.fine( tag.getName() + ": " + handler.getClass().getName() );
		}
		return handler;
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		bodyHandler.characters(ch, start, length);
	}

	public void end() throws SAXException {
		bodyHandler.end();
	}

	public void start(XMLTag tag) throws SAXException {
		bodyHandler.start(tag);
	}
}
