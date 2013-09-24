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

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.PlanOptions;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTPClient;
import org.melato.bus.otp.OTPRequest;
import org.melato.bus.plan.NamedPoint;
import org.melato.gps.Point2D;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/** Computes and displays a list of plans for going to a destination.
 **/
public class PlanActivity extends Activity implements OnClickListener {
  public static final String POINT = "POINT";
  public static NamedPoint origin;
  public static NamedPoint destination;
  public static OTP.Plan plan;
  private Mode[] modes;
  
  static class Mode {
    String code;
    int     resource;
    CheckBox check;
    
    public String prefKey() {
      return "mode." + code;
    }
    public boolean getPreference(Context context) {
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
      return settings.getBoolean(prefKey(), true);
    }
    public void setPreference(SharedPreferences.Editor settings) {
      settings.putBoolean(prefKey(), check.isChecked());
    }
    public Mode(Context context, String code, int resource) {
      super();
      this.code = code;
      this.resource = resource;
      check = new CheckBox(context);
      check.setText(resource);
      check.setChecked(getPreference(context));
    }
    
  }
  class PlanTask extends AsyncTask<OTPRequest,Void,OTP.Plan> {    
    private Exception exception;
    @Override
    protected void onPreExecute() {
      setProgressBarIndeterminate(true);
      setProgressBarVisibility(true);
    }

    @Override
    protected OTP.Plan doInBackground(OTPRequest... params) {
      String url = Info.routeManager(PlanActivity.this).getPlannerUrl();
      OTP.Planner planner = new OTPClient(url);
      try {
        return planner.plan(params[0]);
      } catch(Exception e) {
        exception = e;
        return null;
      }
    }

    @Override
    protected void onPostExecute(OTP.Plan plan) {
      setProgressBarVisibility(false);
      if ( plan == null) {
        if ( exception != null) {
          Toast toast = Toast.makeText(PlanActivity.this, exceptionToString(exception), Toast.LENGTH_SHORT);
          toast.show();              
        }        
      } else {
        PlanActivity.plan = plan;
        setTitle(R.string.best_route);
        startActivity(new Intent(PlanActivity.this, OTPItinerariesActivity.class));
      }
    }
  }
  
  String exceptionToString(Exception e) {
    int resId = 0;
    if ( e instanceof UnknownHostException) {
      resId = R.string.error_connect;
    } else if ( e instanceof ConnectException) {
        resId = R.string.error_connect;
    } else {
      return e.toString();
    }
    return getString(resId);
  }
  
  void showEndpoints() {
    TextView view = (TextView) findViewById(R.id.from);
    view.setText(origin != null ? origin.toString() : "");
    view = (TextView) findViewById(R.id.to);
    view.setText(destination != null ? destination.toString() : "");
  }
  
  private PlanOptions getOptions() {
    return new PlanOptions(this);
  }
  
  
  @Override
  public void onClick(View v) {
    switch( v.getId() ) {
    case R.id.delete_from:
      origin = null;
      showEndpoints();
      break;
    case R.id.delete_to:
      destination = null;
      showEndpoints();
      break;
    }
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_PROGRESS);
    setContentView(R.layout.plan);
    LinearLayout modeView = (LinearLayout)findViewById(R.id.modeView);
    ((ImageButton)findViewById(R.id.delete_from)).setOnClickListener(this);
    ((ImageButton)findViewById(R.id.delete_to)).setOnClickListener(this);
    modes = new Mode[] {
        new Mode(this, OTPRequest.BUS, R.string.mode_bus),
        new Mode(this, OTPRequest.TRAM, R.string.mode_tram),
        new Mode(this, OTPRequest.SUBWAY, R.string.mode_subway),        
    };
    for( int i = 0; i < modes.length; i++ ) {
      modeView.addView(modes[i].check);
    }
  }
    
  @Override
  protected void onResume() {
    super.onResume();
    showEndpoints();
  }

  @Override
  protected void onDestroy() {
    savePreferences();
    super.onDestroy();
  }

  int parseTime(String s) {
    if ( s == null )
      return -1;
    s = s.trim();
    String[] fields = s.split(":");
    if ( fields.length == 2 ) {
      try {
        int time = Integer.parseInt(fields[0]) * 60 + Integer.parseInt(fields[1]);
        return time * 60;
      } catch( NumberFormatException e) {        
      }
    }
    return -1;
  }
  OTPRequest buildRequest(Point2D from) {
    OTPRequest request = new OTPRequest();
    Info.routeManager(this).setOtpDefaults(request);
    request.setFromPlace(from);
    request.setToPlace(destination);
    Date date = new Date();
    TextView timeView = (TextView) findViewById(R.id.time);
    int time = parseTime(timeView.getText().toString());
    if ( time >= 0) {
      date = OTPRequest.replaceTime(date, time);
    }
    request.setDate(date);
    CheckBox arriveView = (CheckBox) findViewById(R.id.arrive);
    if ( arriveView.isChecked()) {
      request.setArriveBy(true);
    }
    List<String> modeList = new ArrayList<String>();
    modeList.add(OTPRequest.WALK);
    for( Mode mode: modes ) {
      if ( mode.check.isChecked()) {
        modeList.add(mode.code);
      }
    }
    request.setMode(modeList);
    PlanOptions options = getOptions();
    request.setMaxWalkDistance(options.getMaxWalk());
    request.setWalkSpeed(options.getWalkSpeedMetric());
    request.setMin(options.isFewerTransfers() ? OTPRequest.OPT_TRANSFERS : OTPRequest.OPT_QUICK);
    return request;
  }

  void savePreferences() {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
    Editor editor = settings.edit();
    for( Mode mode: modes ) {
      mode.setPreference(editor);
    }
    editor.commit();
  }
  
  void plan() {
    Point2D from = PlanActivity.origin;
    if ( from == null) {
      from = Info.trackHistory(this).getLocation();
    }
    if ( destination == null) {
      setTitle("Missing Destination");
    } else if ( from == null) {
      setTitle("Missing Origin");
    } else {
      OTPRequest request = buildRequest(from);
      new PlanTask().execute(request);      
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.plan_menu, menu);
     HelpActivity.addItem(menu, this, Help.PLAN);
     return true;
  }

  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.swap:
        NamedPoint temp = origin;
        origin = destination;
        destination = temp;
        showEndpoints();
        handled = true;
        break;
      case R.id.plan:
        plan();
        handled = true;
        break;
      case R.id.last:
        if ( PlanActivity.plan != null ) {
          startActivity(new Intent(PlanActivity.this, OTPItinerariesActivity.class));
        }
        handled = true;
        break;
      case R.id.remove_first:
        origin = null;
        showEndpoints();
        handled = true;
        break;
      case R.id.remove_last:
        destination = null;
        showEndpoints();
        handled = true;
        break;
      case R.id.pref:
        startActivity(new Intent(this, PlanPreferencesActivity.class));      
        break;
    }
    return handled ? true : false;
  }
}