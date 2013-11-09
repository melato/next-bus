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
package org.melato.bus.android.app;

import java.util.List;

import org.melato.android.AndroidLogger;
import org.melato.android.progress.ActivityProgressHandler;
import org.melato.android.progress.ProgressTitleHandler;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.HomeActivity;
import org.melato.log.Log;
import org.melato.progress.CanceledException;
import org.melato.progress.ProgressGenerator;
import org.melato.update.PortableUpdateManager;
import org.melato.update.UpdateFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/** The activity that performs database updates. */
public class UpdateActivity extends Activity {
  public static final String ACCEPTED_TERMS = "accepted_terms";
  private PortableUpdateManager updateManager;
  private ActivityProgressHandler progress;
  private enum MessageState {
    TERMS,
    ERROR,    
  };
  private MessageState state;

  private void showMessage(int messageId) {
    setContentView(R.layout.message);
    TextView noteView = (TextView) findViewById(R.id.note);
    noteView.setText(Html.fromHtml(getResources().getString(messageId)));
    noteView.setMovementMethod(new LinkMovementMethod());    
  }
  
  /** background task for retrieving the list of available updates. */
  class IndexTask extends AsyncTask<Void,Void,Boolean> {
    List<UpdateFile> updateFiles;
    @Override
    protected Boolean doInBackground(Void... params) {
      boolean required = updateManager.isRequired();
      updateFiles = updateManager.getAvailableUpdates();
      return required;
    }
    @Override
    protected void onPostExecute(Boolean result) {
      if ( result ) {
        setTitle(R.string.update_required);
      }
      StringBuilder buf = new StringBuilder();
      for(UpdateFile f: updateFiles) {
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
  }

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if ( ! isConnected(this) ) {
      this.state = MessageState.ERROR;
      showMessage(R.string.need_network);
      return;
    }
    if ( ! hasAcceptedTerms(this)) {
      this.state = MessageState.TERMS;
      setTitle(R.string.terms_of_use);
      showMessage(R.string.eula);
      return;
    }
    progress = new ProgressTitleHandler(this);
    updateManager = new UpdateManager(this);
    new IndexTask().execute();      
  }

  /** Called from the update button */
  public void update(View view) {
    Button button = (Button) findViewById(R.id.update);
    button.setEnabled(false);
    new UpdateTask().execute();
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
    Intent intent = new Intent(this, HomeActivity.class);
    startActivity(intent);        
  }
  
  void startUpdate() {   
  }
  
  class UpdateTask extends AsyncTask<Void,Integer,Boolean> {
    private Exception exception;

    @Override
    protected Boolean doInBackground(Void... params) {
      if ( progress != null ) {
        ProgressGenerator.setHandler(progress);
      }
      try {
        updateManager.update(updateManager.getAvailableUpdates());
        Info.reload();
        return true;
      } catch( CanceledException e ) {
      } catch( Exception e ) {
        exception = e;
      }
      return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
      if ( result ) {
        startMain();
      } else if ( exception != null) {
        Toast toast = Toast.makeText(UpdateActivity.this, exception.getMessage(), Toast.LENGTH_SHORT);
        toast.show();        
      }
      finish();
    }
    
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
  static class UpdatesChecker extends AsyncTask<Void,Integer,Boolean>{
    UpdateManager updateManager;
    Context context;
    
    public UpdatesChecker(Activity activity) {
      this.context = activity.getApplication();
      updateManager = new UpdateManager(activity);
    }

    /*
    * Return true if the update activity should start.
    */
    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        return ! updateManager.getAvailableUpdates().isEmpty();
      } catch( CanceledException e) {
        return false;
      }
      
    }

    @Override
    protected void onPostExecute(Boolean result) {
      super.onPostExecute(result);
      if ( result ) {
        startUpdateActivity();
      }
    }

    private void startUpdateActivity() {
      Intent intent = new Intent(context, UpdateActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      context.startActivity(intent);
    }
    /** Check for updates, if necessary.
     * Return true if the calling activity can continue.
     * Return false if the calling activity should exit and let the update activity take over.
     * @return
     */
    public boolean checkUpdates() {
      if ( updateManager.isRequired() ) {
        // assume that we have not data.  We have to update or die.
        startUpdateActivity();
        return false;
      } else if ( isConnected(context) ) {
        execute();
        return true;
      } else {
        return true;
      }
    }
  }
  
  /** Check for updates, and if there are any available give the option of downloading them.
   * return true if the application should proceed normally.
   * false if it should do nothing and let the UpdateActivity take over.
   **/
  public static boolean checkUpdates(Activity activity) {
    Log.setLogger(new AndroidLogger(activity));
    UpdatesChecker checker = new UpdatesChecker(activity);
    return checker.checkUpdates();
  }


}
