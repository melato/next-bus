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
package org.melato.bus.android.app;

import org.melato.android.progress.ActivityProgressHandler;
import org.melato.android.progress.ProgressTitleHandler;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.activity.RoutesActivity;
import org.melato.progress.CanceledException;
import org.melato.progress.ProgressGenerator;
import org.melato.update.UpdateFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UpdateActivity extends Activity implements Runnable {
  public static final String ACCEPTED_TERMS = "accepted_terms";
  private UpdateManager updateManager;
  private ActivityProgressHandler progress;
  private enum MessageState {
    TERMS,
    ERROR,    
  };
  private MessageState state;

  private void showMessage(MessageState state, int messageId) {
    setContentView(R.layout.message);
    TextView noteView = (TextView) findViewById(R.id.note);
    this.state = state;
    noteView.setText(messageId);
    noteView.setMovementMethod(new ScrollingMovementMethod());    
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if ( ! isConnected(this) ) {
      showMessage(MessageState.ERROR, R.string.need_network);
      return;
    }
    if ( ! hasAcceptedTerms(this)) {
      setTitle(R.string.terms_of_use);
      showMessage(MessageState.TERMS, R.string.eula);
      return;
    }
    progress = new ProgressTitleHandler(this);
    updateManager = new UpdateManager(this);
    if ( updateManager.isRequired() ) {
      setTitle(R.string.update_required);
    }
    StringBuilder buf = new StringBuilder();
    for(UpdateFile f: updateManager.getAvailableUpdates()) {
      if ( f.getNote() != null ) {
        if ( buf.length() > 0 ) {
          buf.append( "\n" );
        }
        buf.append(f.getNote());
      }
    }
    setContentView(R.layout.update);
    TextView noteView = (TextView) findViewById(R.id.note);
    noteView.setText(buf.toString());
  }

  /** Called from the update button */
  public void update(View view) {
    Button button = (Button) findViewById(R.id.update);
    button.setEnabled(false);
    new Thread(this).start();
  }
  
  /** Called from the cancel button */
  public void cancel(View view) {
    Button button = (Button) findViewById(R.id.cancel);
    button.setEnabled(false);
    progress.cancel();    
    finish();
  }

  /** Called from the ok button */
  public void ok(View view) {
    if ( state == MessageState.ERROR )
      finish();
    else if ( state == MessageState.TERMS ) {
      SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
      Editor editor = prefs.edit();
      editor.putBoolean(ACCEPTED_TERMS, true);
      editor.commit();
      finish();
      startActivity(new Intent(this, UpdateActivity.class));
    }
  }

  void startMain() {
    BusActivities activities = new BusActivities(this);
    if ( activities.hasRecentRoutes() ) {
      RoutesActivity.showRecent(this);
    } else {
      RoutesActivity.showAll(this);
    }
  }
  
  void startUpdate() {
    
  }
  @Override
  public void run() {
    if ( progress != null ) {
      ProgressGenerator.setHandler(progress);
    }
    try {
      updateManager.update(updateManager.getAvailableUpdates());
      runOnUiThread(new Runnable() {

        @Override
        public void run() {
          startMain();
        }
        
      });
      
    } catch( CanceledException e ) {      
    }
    finish();
  }
  
  
  @Override
  protected void onDestroy() {
    if ( progress != null )
      progress.cancel();
    super.onDestroy();
  }
  
  public static boolean hasAcceptedTerms(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    return prefs.getBoolean(ACCEPTED_TERMS, false);
  }
  
  public static boolean isConnected(Context context) {
    ConnectivityManager network = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = network.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.getState() == State.CONNECTED;      
  }
  
  /**
   * Checks for available updates.  It does so in a background thread, if there is need to download anything.
   * Otherwise it checks in the ui thread.
   * @author Alex Athanasopoulos
   */
  static class UpdatesChecker implements Runnable {
    UpdateManager updateManager;
    Activity activity;
    
    public UpdatesChecker(Activity activity) {
      this.activity = activity;
      updateManager = new UpdateManager(activity);
    }
    
    /** Check for updates, if necessary.
     * Return true if the calling activity can continue.
     * Return false if the calling activity should exit and let the update activity take over.
     * @return
     */
    public boolean checkUpdates() {
      if ( updateManager.isRequired() ) {
        // assume that we have not data.  We have to update or die.
        activity.startActivity(new Intent(activity, UpdateActivity.class));
        return false;
      } else {
        if ( isConnected(activity) ) {
          if ( updateManager.needsRefresh() ) {
            // refresh in the background
            new Thread(this).start();
          }
          else {
            // check here
            run();
          }
        }
      }
      return true;
    }
    
    public void run() {
      if ( ! updateManager.getAvailableUpdates().isEmpty() ) {
        activity.startActivity(new Intent(activity, UpdateActivity.class));
      }
    }
  }
  
  /** Check for updates, and if there are any available give the option of downloading them.
   * return true if the application should proceed normally.
   * false if it should do nothing and let the UpdateActivity take over.
   **/
  public static boolean checkUpdates(Activity activity) {
    UpdatesChecker checker = new UpdatesChecker(activity);
    return checker.checkUpdates();
  }


}
