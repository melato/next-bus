/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.client.Formatting;
import org.melato.bus.model.Schedule;
import org.melato.bus.plan.LegAdapter;

import android.content.Context;

public class LegFormatter {
  public static String label(LegAdapter leg, Context context) {
    StringBuilder buf = new StringBuilder();
    if ( ! leg.isWalk()) {
      buf.append(leg.getLabel());
      buf.append(" ");
      buf.append(Schedule.formatTime(leg.getStartTime() / 60));
      buf.append(" ");
      buf.append(leg.getFromName());
      buf.append(" -> ");
      buf.append(Schedule.formatTime(leg.getEndTime() / 60));
      buf.append(" ");
      buf.append(leg.getToName());
      int diffTime = leg.getDiffTime();
      if ( diffTime >= 0 ) {
        buf.append(" (");
        buf.append( context.getString(R.string.wait));
        buf.append(" ");
        buf.append(Schedule.formatDuration(diffTime));
        buf.append(")");
      }
    } else {
      buf.append(context.getString(R.string.walk));
      buf.append( " ");
      buf.append(Formatting.straightDistance(leg.getDistance()));
      buf.append(" ");
      buf.append(Schedule.formatDuration(leg.getDuration()));
    }
    return buf.toString();
  }
  
  
}
