package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import sk.cde.yapco.DbHelper;
import sk.cde.yapco.R;
import sk.cde.yapco.rss.RssFeedParser;

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

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), ItemListActivity.class);
                intent.putExtra("channel_id", id);
                startActivity(intent);
            }
        });

        registerForContextMenu(lv);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        refresh();
    }

    @Override
    public void onBackPressed() {
        // do nothing
//        super.onBackPressed();
    }

    // ============================================= options menu and options

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_list_activity_options_menu, menu);
        return true;
    }

    public void addNewFeed(MenuItem item){
        Intent intent = new Intent(this, AddFeedActivity.class);
        startActivity(intent);
    }

    public void exitApplication(MenuItem item){
        finish();
    }

    public void updateAllChannels(MenuItem item){
        DbHelper.refreshAllChannels(this);
        refresh();
    }

    // ============================================= context menu and options

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((TextView)info.targetView.findViewById(R.id.channelTitle)).getText().toString();
        menu.setHeaderTitle(title);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.channel_context_menu, menu);
    }


    public void viewChannelDescription(MenuItem item ){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Long channelId = menuInfo.id;
        System.out.println(">>> channel description for " + channelId);
    }

    public void visitChannelWebPage(MenuItem item){
        // get channel id
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Long channelId = menuInfo.id;
        String[] params = {channelId.toString()};

        // select channel from db
        Cursor cursor = db.query(
                DbHelper.CHANNEL_TABLE_NAME,
                null,
                "_id=?", params,
                null, null, null);

        // get result (no check, if there is some)
        cursor.moveToFirst();
        String link = cursor.getString( cursor.getColumnIndex(DbHelper.C_LINK));

        // create intent
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(link) );
        startActivity(intent);
    }

    public void updateChannel(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DbHelper.refreshChannel(this, menuInfo.id);
        refresh();
    }

    public void unsubscribeFromChannel(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        DbHelper.unsubscribeChannel(this, menuInfo.id);
        refresh();
    }

    // ================================= helper methods

    private void refresh(){
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
    }

}