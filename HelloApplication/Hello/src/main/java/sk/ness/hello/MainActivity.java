package sk.ness.hello;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onButtonClicked(View view){

        EditText et = (EditText) findViewById(R.id.editText);
//        TextView tv = (TextView) findViewById(R.id.textView2);

        String message = et.getText().toString();
//        tv.setText(message);

        Intent intent = new Intent( this, HelloActivity.class);
        intent.putExtra("name", message );
        startActivity(intent);
    }
}
