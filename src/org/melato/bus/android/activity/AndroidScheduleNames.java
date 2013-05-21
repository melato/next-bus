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
import org.melato.bus.client.ScheduleNames;

import android.content.Context;

public class AndroidScheduleNames extends ScheduleNames {
  private Context context;
    
  public AndroidScheduleNames(Context context) {
    super();
    this.context = context;
  }
  private static final int[] DAY_RESOURCES = {
    R.string.days_Su,
    R.string.days_Mo,
    R.string.days_Tu,
    R.string.days_We,
    R.string.days_Th,
    R.string.days_Fr,
    R.string.days_Sa,
  };
  
  @Override
  public String getDayName(int day) {
    return context.getResources().getString(DAY_RESOURCES[day]);      
  }
  @Override
  public String getAllDaysName() {
    return context.getResources().getString(R.string.days_all);
  }
  @Override
  public String getTodayName() {
    return context.getString(R.string.today);      
  }
}
