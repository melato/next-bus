/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.otp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.melato.gps.Point2D;

/** A plan request.  It has fields used by OTP, but could be used by other planners as well. */
public class OTPRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  public static final String TRANSIT = "TRANSIT";
  public static final String BUS = "BUS";
  public static final String TRAM = "TRAM";
  public static final String WALK = "WALK";
  
  public static final String OPT_TRANSFERS = "TRANSFERS";
  public static final String OPT_QUICK = "QUICK";
  private Point2D fromPlace;
  private Point2D toPlace;
  private int maxWalkDistance = 1000;
  private float walkSpeed = 5 / 3.6f;
  private Date date = new Date();
  private boolean arriveBy;
  private List<String> mode = new ArrayList<String>();
  private String min = OPT_QUICK;
  
  public OTPRequest() {
    super();
    mode.add(TRANSIT);
    mode.add(WALK);
  }
  public List<String> getMode() {
    return mode;
  }
  public void setMode(List<String> mode) {
    this.mode = mode;
  }
  
  public String getMin() {
    return min;
  }
  public void setMin(String min) {
    this.min = min;
  }
  public Point2D getFromPlace() {
    return fromPlace;
  }
  public void setFromPlace(Point2D fromPlace) {
    this.fromPlace = fromPlace;
  }
  public Point2D getToPlace() {
    return toPlace;
  }
  public void setToPlace(Point2D toPlace) {
    this.toPlace = toPlace;
  }
  public int getMaxWalkDistance() {
    return maxWalkDistance;
  }
  public void setMaxWalkDistance(int maxWalkDistance) {
    this.maxWalkDistance = maxWalkDistance;
  }
  public float getWalkSpeed() {
    return walkSpeed;
  }
  public void setWalkSpeed(float walkSpeed) {
    this.walkSpeed = walkSpeed;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }  
  public boolean isArriveBy() {
    return arriveBy;
  }
  public void setArriveBy(boolean arriveBy) {
    this.arriveBy = arriveBy;
  }
  
  public static Date replaceTime(Date date, int seconds) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    cal.set(Calendar.HOUR_OF_DAY, seconds / 3600);
    cal.set(Calendar.MINUTE, (seconds % 3600) / 60);
    cal.set(Calendar.SECOND, seconds % 60);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTime();
  }
}
