package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import sk.cde.yapco.Repository;
import sk.cde.yapco.ItemAdapter;
import sk.cde.yapco.rss.Channel;
import sk.cde.yapco.rss.Item;
import sk.cde.yapco.R;
import sk.cde.yapco.services.UpdaterService;

/**
 * Created by mirek on 21.1.2014.
 */
public class ItemListActivity extends Activity {
    private static final String TAG = "EpisodesListAct";
    private Repository repository;
    private ItemListReceiver receiver;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.episodes_list_activity);
        Log.i(TAG, "onCreate()");

        this.repository = new Repository(this);

        ListView lv = (ListView) findViewById(R.id.listView);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Uri uri = Uri.parse(view.getTag().toString());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "audio/*");
                startActivity(intent);
            }
        });

        registerForContextMenu(lv);

        this.receiver = new ItemListReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(this.receiver, new IntentFilter(UpdaterService.NEW_EPISODES));
        refresh();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(this.receiver);
    }

    // =================================== options menu and it's selection handling

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.episode_list_activity_options_menu, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long channelId = getIntent().getExtras().getLong("channel_id");

        switch( item.getItemId() ){
            // refresh items in current podcast
            case R.id.refresh_feed:
                repository.refreshChannel(channelId);
                refresh();
                break;

            case R.id.channel_description:
                Channel channel = repository.getChannel(channelId);
                Intent intent = new Intent(this, ChannelInfoActivity.class);
                intent.putExtra("title", channel.title);
                intent.putExtra("description", channel.description);
                intent.putExtra("link", channel.link);
                intent.putExtra("image", channel.imageLocation);
                intent.putExtra("channel_id", channelId);
                startActivity(intent);
                break;

            default:
                return false;
        }
        return true;
    }


    // =================================== context menu and it's selection handling

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String title = ((TextView)info.targetView.findViewById(R.id.episodeTitle)).getText().toString();
        menu.setHeaderTitle(title);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.episode_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        Item item = repository.getItem(menuInfo.id);

        switch( menuItem.getItemId() ){
            case R.id.visit_web_page:{
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(item.link) );
                startActivity(intent);
                break;
            }

            case R.id.play_item:{
                Intent intent = new Intent( Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(item.mediaUrl), item.mediaType);
                startActivity(intent);
                break;
            }

            default:
                return false;
        }

        return true;
    }


    // =================================== helper methods

    private void refresh() {
        Cursor cursor = repository.queryItemsFromChannel(getIntent().getExtras().getLong("channel_id"));
        ItemAdapter adapter = new ItemAdapter(this, cursor, 0);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }

    private class ItemListReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
            Log.d(TAG, "onReceive");
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }
}
