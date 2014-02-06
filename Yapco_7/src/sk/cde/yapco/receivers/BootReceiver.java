package sk.cde.yapco.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import sk.cde.yapco.services.UpdaterService;

/**
 * Created by mirek on 6.2.2014.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if( prefs.getBoolean("pref_allow_automatic_updates", false) ){
            context.startService(new Intent(context, UpdaterService.class));
        }

        Log.d(TAG, "onReceive");
    }
}
