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
package org.melato.progress;


/** A ProgressHandler that reports progress to stderr.
 * @author Alex Athanasopoulos
 * @date Dec 1, 2007
 */
public class ConsoleProgressHandler implements ProgressHandler {
	private int limit;
	private String text;
	private long lastTime;
	/** The number of milliseconds to wait between printing progress indication changes.
	 */
	public static final int UPDATE_INTERVAL = 1000;

	@Override
	public boolean isCanceled() {
		return false;
	}

	@Override
	public void setPosition(int pos) {
		long time = System.currentTimeMillis();
		if ( time - lastTime < UPDATE_INTERVAL ) {
			return;
		}
		lastTime = time;
		System.err.println( text + " " + pos + "/" + limit );
	}

	@Override
	public void setLimit(int limit) {
		this.limit = limit;
		//System.err.println( "Progress limit: " + limit );
	}

	@Override
	public void setText(String text) {
		this.text = text;
		//System.err.println( "Progress text: " + text );
	}
}
