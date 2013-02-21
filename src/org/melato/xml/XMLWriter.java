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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import org.xml.sax.Attributes;

/*
 * This file contains source code adapted from javax/swing/text/html/HMTLWriter.java
 * which is licensed under GNU GPL v2 from Oracle.  
 */

/**
 * This is a writer for XML Documents.
 */
public class XMLWriter {
	public static final String UTF8 = "utf-8";
    private	PrintWriter	writer;
	private	boolean		newlines = false;
	private	int			tagCount;
    
	public void setNewlines(boolean useNewlines) {
		this.newlines = useNewlines;
	}
	
    /**
     * Temporary buffer.
     */
    private char[] tempChars;

    public XMLWriter( File file ) throws IOException {
    	this( new OutputStreamWriter( new FileOutputStream( file ), UTF8 ));
    }
    
    public XMLWriter(Writer writer ) {
    	this.writer = new PrintWriter( writer );
    }
    
    public XMLWriter(OutputStream out) throws UnsupportedEncodingException {
    	this.writer = new PrintWriter( new OutputStreamWriter( out, UTF8 ));
    }
    
    public void printHeader() {
    	writer.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    }
    
    public void printHeader(Attributes attributes) {
    	tagOpen( "?xml", false);
    	int count = attributes.getLength();
    	for( int i = 0; i < count; i++) {
    		tagAttribute( attributes.getQName(i), attributes.getValue(i));
    	}
    	tagClose(true);
    }
    
    public void printHeader(Map<String,String> attributes) {
    	tagOpen( "?xml", false);
    	for( Map.Entry<String,String> e: attributes.entrySet() ) {
    		tagAttribute(e.getKey(), e.getValue());
    	}
    	writer.print("?>");
    }
    
    
    public void println() {
    	writer.println();
    }
    
    public void write( XMLTag tag ) {
    	tagOpen( tag.getName(), false );
    	Attributes attributes = tag.getAttributes();
    	int n = attributes.getLength();
    	for( int i = 0; i < n; i++ ) {
    		tagAttribute( attributes.getQName(i), attributes.getValue(i));
    	}
    	tagClose();
    }
    
  /**
   * Output a tag attribute.
   * To use, open the tag with tagOpen(tag, false);
   * @param name
   * @param value
   */
  public void tagAttribute( String name, String value ) {
    writer.write( " " );
		writer.write( name );
		writer.write( "=" );
		writer.write( '"' );
		int length = value.length();
		char[] cc = getTempChars(length);
		value.getChars( 0, length, cc, 0 );
		int start = 0;
		for (int i = 0; i < length; i++ ) {
			char c = cc[i];
			switch( c ) {
			case '"':
				writer.write( cc, start, i - start );
				writer.write( "&quote;" );
				start = i + 1;
				break;
			default:
				break;
			}
		}
		writer.write( cc, start, length - start );
		writer.write( '"' );    				
    }
    
    public void tagAttributes( Map<String,String> attributes) {
    	for( Map.Entry<String,String> a : attributes.entrySet()) {
    		tagAttribute( a.getKey(), a.getValue());
    	}
    }
    
    public void tagEnd( String tag ) {
    	writer.write( "</" );
    	writer.write( tag );
    	writer.write( ">" );
    	if ( newlines ) {
    		writer.println();
    	}
    }

    /**
     * output &gt;
     * Use for tags opened with tagOpen(tag, false);
     */
    public void tagClose() {    	
    	writer.write( ">" );
    }
    
    public void tagClose( boolean endToo ) {
    	writer.write( endToo ? "/>" : ">" );
    	if ( endToo && newlines ) {
    		writer.println();
    	}
    }
    
    /**
     * output a beginning tag, e.g. &lt;tag&gt;
     * call tagEnd() at the end of the element.
     * @param tag
     */
    public void tagOpen( String tag ) {
    	tagOpen( tag, true );
    }
    
    /**
     * output a beginning tag, but leave it open for attributes, e.g. &lt;tag;
     * @param tag
     */
    public void tagOpen( String tag, boolean close ) {
    	if ( newlines && tagCount++ > 0 ) {
    		writer.println();
    	}
    	writer.write( "<" );
    	writer.write( tag );
    	if ( close ) {
    		tagClose();
    	}
    }
    
    /* Adapted from javax/swing/text/html/HMTLWriter.java */

    /**
     * This method is overriden to map any character entities, such as
     * &lt; to &amp;lt;. <code>super.output</code> will be invoked to
     * write the content.
     */
    public void text(char[] chars, int start, int length)
	           {
	int last = start;
	length += start;
	for (int counter = start; counter < length; counter++) {
	    // This will change, we need better support character level
	    // entities.
	    switch(chars[counter]) {
		// Character level entities.
	    case '<':
		if (counter > last) {
		    writer.write(chars, last, counter - last);
		}
		last = counter + 1;
		writer.write("&lt;");
		break;
	    case '>':
		if (counter > last) {
		    writer.write(chars, last, counter - last);
		}
		last = counter + 1;
		writer.write("&gt;");
		break;
	    case '&':
		if (counter > last) {
		    writer.write(chars, last, counter - last);
		}
		last = counter + 1;
		writer.write("&amp;");
		break;
	    case '"':
		if (counter > last) {
		    writer.write(chars, last, counter - last);
		}
		last = counter + 1;
		writer.write("&quot;");
		break;
		// Special characters
	    case '\n':
	    case '\t':
	    case '\r':
		break;
	    default:
		if ( 0 <= chars[counter] && chars[counter] < ' ' ) {
		    if (counter > last) {
			writer.write(chars, last, counter - last);
		    }
		    last = counter + 1;
		    // If the character is outside of ascii, write the
		    // numeric value.
		    writer.write("&#");
		    writer.write(String.valueOf((int)chars[counter]));
		    writer.write(";");
		}
		break;
	    }
	}
	if (last < length) {
	    writer.write(chars, last, length - last);
	}
    }

    private char[] getTempChars( int length ) {
    	if (tempChars == null || tempChars.length < length) {
    	    tempChars = new char[length];
    	}
    	return tempChars;
    }
    
    /**
     * This directly invokes super's <code>output</code> after converting
     * <code>string</code> to a char[].
     */
    public void text(String string) {
    	if ( string == null )
    		return;
		int length = string.length();
		char[] cc = getTempChars(length);
		string.getChars(0, length, cc, 0);
		text(cc, 0, length);
    }

    public PrintWriter getWriter() {
		return writer;
	}
    
    /**
     * Set the output writer.
     * This may be called anytime to redirect the output somewhere else.
     * @param writer
     */
    public void setWriter( PrintWriter writer ) {
    	flush();
    	this.writer = writer;
    }    
    
    public void flush() {
    	writer.flush();
    }
    
    public void close() {
    	writer.close();
    }
}
