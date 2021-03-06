package sk.cde.yapco.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import sk.cde.yapco.Repository;

/**
 * Created by mirek on 4.2.2014.
 */
public class UpdaterService extends Service {
    private static final String TAG = "UpdaterService";
    private static final int DELAY = 30;
    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreated");
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
                    Log.d(TAG, "channels are updated");

                    try {
                        Thread.sleep(DELAY * 1000);
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
