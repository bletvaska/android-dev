package sk.cde.yapco.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import sk.cde.yapco.R;

/**
 * Created by mirek on 5.2.2014.
 */
public class PrefsActivity extends PreferenceActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.prefs);

    }
}