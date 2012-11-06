/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.android.gpx.map;

import java.util.Collections;
import java.util.List;

import org.melato.gpx.Waypoint;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

/**
 * A map overlay that contains waypoints.
 * @author Alex Athanasopoulos
 */
public class WaypointsOverlay extends ItemizedOverlay<WaypointOverlayItem> {
	private List<Waypoint> waypoints = Collections.emptyList();
	
	public WaypointsOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
	}

	public void setWaypoints(List<Waypoint> waypoints) {
		this.waypoints = waypoints;
		populate();
	}
	
	@Override
	protected WaypointOverlayItem createItem(int i) {
		return new WaypointOverlayItem(waypoints.get(i));
	}

	@Override
	public int size() {
		return waypoints.size();
	}
}
