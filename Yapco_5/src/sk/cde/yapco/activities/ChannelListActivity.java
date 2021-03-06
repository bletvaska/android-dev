package sk.cde.yapco.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import sk.cde.yapco.Repository;
import sk.cde.yapco.R;
import sk.cde.yapco.rss.Channel;
import sk.cde.yapco.services.RefreshService;
import sk.cde.yapco.services.UpdateService;

/**
 * Created by mirek on 21.1.2014.
 */
public class ChannelListActivity extends Activity {
    private static final String TAG = "ChannelListActivity";

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        onItemStopServiceClicked(null);
    }

    private Repository repository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_list_activity);
        Log.i(TAG, "onCreate()");

        this.repository = new Repository(this);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to quit app?");

        builder.setPositiveButton(R.string.exit, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    public void updateAllChannels(MenuItem item){
        repository.refreshAllChannels();
        refresh();
    }

    public void onItemStartServiceClicked(MenuItem item){
        Intent intent = new Intent(this, UpdateService.class);
        startService(intent);
    }

    public void onItemStopServiceClicked(MenuItem item){
        Intent intent = new Intent(this, UpdateService.class);
        stopService(intent);
    }

    public void onItemStartRefreshServiceClicked(MenuItem item){
        Intent intent = new Intent(this, RefreshService.class);
        startService(intent);
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
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // select channel from db
        Channel channel = repository.getChannel(menuInfo.id);

        // create intent
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(channel.link) );
        startActivity(intent);
    }

    public void updateChannel(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        repository.refreshChannel(menuInfo.id);
        refresh();
    }

    public void unsubscribeFromChannel(MenuItem item){
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        repository.deleteChannel(menuInfo.id);
        refresh();
    }

    // ================================= helper methods

    private void refresh(){
        Cursor cursor = repository.queryChannels();

        String[] from = {Repository.C_TITLE, Repository.C_DESCRIPTION};
        int[] to = {R.id.channelTitle, R.id.channelDescription};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.channel_row, cursor, from, to, 0);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

}