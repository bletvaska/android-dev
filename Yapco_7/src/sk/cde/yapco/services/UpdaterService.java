package sk.cde.yapco.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import sk.cde.yapco.Repository;

/**
 * Created by mirek on 4.2.2014.
 */
public class UpdaterService extends Service {
    private static final String TAG = "UpdaterService";
    private static final int DELAY = 30;
    public static final String NEW_EPISODES = "sk.cde.yapco.NEW_EPISODES";
    private boolean running;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");

        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStarted");

        this.running = true;

        new Thread() {
            public void run() {
                while(running){
                    Repository repo = new Repository(getApplicationContext());
                    repo.refreshAllChannels();
                    sendBroadcast(new Intent(NEW_EPISODES));
                    Log.d(TAG, "channels are updated");

                    try {
                        int delay = Integer.valueOf(prefs.getString("pref_update_refresh_rate", "1"));
                        Thread.sleep(delay * 1000 * 60);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        this.running = false;
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
