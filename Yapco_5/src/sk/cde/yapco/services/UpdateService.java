package sk.cde.yapco.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import sk.cde.yapco.Repository;

/**
 * Created by mirek on 4.2.2014.
 */
public class UpdateService extends Service {
    static final String TAG = "UpdateService";
    private boolean running;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");

        this.running = true;

        new Thread(){
            public void run(){
                Repository repository = new Repository(getApplicationContext());

                while( running ){
                    Log.d(TAG, "refreshing channels");
                    repository.refreshAllChannels();

                    try {
                        Thread.sleep(30 * 1000);
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
