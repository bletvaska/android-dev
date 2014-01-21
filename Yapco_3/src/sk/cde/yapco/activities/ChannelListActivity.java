package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import sk.cde.yapco.DbHelper;
import sk.cde.yapco.R;

/**
 * Created by mirek on 21.1.2014.
 */
public class ChannelListActivity extends Activity {
    private static final String TAG = "ChannelListActivity";
    private SimpleCursorAdapter adapter;
    private SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list_activity);
        Log.i(TAG, "onCreate()");

        this.db = (new DbHelper(this)).getReadableDatabase();
        Cursor cursor = db.query(
                DbHelper.CHANNEL_TABLE_NAME,
                null,
                null, null,
                null, null,
                DbHelper.C_TITLE + " ASC");

        String[] from = {DbHelper.C_TITLE, DbHelper.C_DESCRIPTION};
        int[] to = {R.id.channelTitle, R.id.channelDescription};
        this.adapter = new SimpleCursorAdapter(this, R.layout.channel_row, cursor, from, to, 0);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ItemListActivity.class);
                intent.putExtra("channel_id", id);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");

    }


    @Override
    public void onBackPressed() {
        // do nothing
        super.onBackPressed();
    }
}