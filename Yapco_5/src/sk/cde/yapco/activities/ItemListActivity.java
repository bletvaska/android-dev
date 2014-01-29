package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import sk.cde.yapco.FeedData;
import sk.cde.yapco.ItemAdapter;
import sk.cde.yapco.rss.Item;
import sk.cde.yapco.R;

/**
 * Created by mirek on 21.1.2014.
 */
public class ItemListActivity extends Activity {
    private static final String TAG = "EpisodesListAct";
    private FeedData feedData;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.episodes_list_activity);
        Log.i(TAG, "onCreate()");

        this.feedData = new FeedData(this);

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
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
        switch( item.getItemId() ){
            // refresh items in current podcast
            case R.id.refresh_feed:
                feedData.refreshChannel(getIntent().getExtras().getLong("channel_id") );
                refresh();
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
        Item item = feedData.getItem(menuInfo.id);

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
        Cursor cursor = feedData.queryItemsFromChannel(getIntent().getExtras().getLong("channel_id"));
        ItemAdapter adapter = new ItemAdapter(this, cursor, 0);
        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
    }
}
