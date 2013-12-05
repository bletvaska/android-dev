package sk.ness.hello;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

public class HelloActivity extends ActionBarActivity {

    private static final String TAG = "HelloActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);


        Log.i(TAG, "hello there");
        TextView tv = (TextView) findViewById(R.id.textView);

        Bundle bundle = getIntent().getExtras();

        String message = getIntent().getStringExtra("name");
        System.out.println("this is message: " + message);
        tv.setText(message);
    }

}
