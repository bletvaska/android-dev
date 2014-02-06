package sk.cde.yapco.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import sk.cde.yapco.services.UpdaterService;

/**
 * Created by mirek on 6.2.2014.
 */
public class NetworkReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkReceiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");

        boolean isNetworkDown = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

        if( isNetworkDown ){
            Log.d(TAG, "onReceive: NOT connected, stopping UpdaterService");
            context.stopService(new Intent(context, UpdaterService.class));
        }else{
            Log.d(TAG, "onReceive: connected, starting UpdaterService");
            context.startService(new Intent(context, UpdaterService.class));
        }
    }
}
