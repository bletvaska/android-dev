package sk.cde.yapco;

import android.app.Application;
import android.util.Log;

/**
 * Created by mirek on 6.2.2014.
 */
public class YapcoApplication extends Application {
    private static final String TAG = "YapcoApplication";
    private boolean firstRun;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        this.firstRun = true;
    }

    public boolean isFirstRun(){
        if( firstRun ){
            firstRun = false;
            return true;
        }

        return false;
    }
}
