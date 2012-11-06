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

/** An interface used to monitor progress of computations.
 * This is generally implemented by a UI dialog or status bar
 * that shows progress indication to the user and lets the user cancel the computation.
 * @author Alex Athanasopoulos
 * @date Dec 1, 2007
 */
public interface ProgressHandler {
	/** Set the progress, as a number.
	 * @param pos  A value >= 0 and < limit.
	 */
	void setPosition( int pos );

	/** Set the maximum value of the progress.
	 */
	void setLimit( int limit );

	/** Set a textual message representing what the operation is currently doing.
	 * @param text
	 */
	void setText( String text );

	/** Find out if the computation should be canceled.
	 *  Generally this should return the state of a flag that is set
	 *  by a cancel button.
	 * @return true if the computation should stop, otherwise false.
	 */
	boolean isCanceled();
}
