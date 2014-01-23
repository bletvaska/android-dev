package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import sk.cde.yapco.DbHelper;
import sk.cde.yapco.ItemAdapter;
import sk.cde.yapco.R;

/**
 * Created by mirek on 21.1.2014.
 */
public class ItemListActivity extends Activity {
    private static final String TAG = "EpisodesListAct";
    private SQLiteDatabase db;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.episodes_list_activity);
        Log.i(TAG, "onCreate()");

        this.db = (new DbHelper(this)).getReadableDatabase();

        Long channelId = getIntent().getExtras().getLong("channel_id");
        String[] params = {channelId.toString()};
        Cursor cursor = db.query(
                DbHelper.ITEM_TABLE_NAME,
                null,
                "chid=?", params,
                null, null, null);

        ItemAdapter adapter = new ItemAdapter(this, cursor, 0);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.episode_list_activity_options_menu, menu );
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

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
    public boolean onContextItemSelected(MenuItem item) {
        // extract item id
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Long channelId = menuInfo.id;
        String[] params = {channelId.toString()};

        // select item from db
        Cursor cursor = db.query(
                DbHelper.ITEM_TABLE_NAME,
                null,
                "_id=?", params,
                null, null, null);

        // get result (no check, if there is some)
        cursor.moveToFirst();

        switch( item.getItemId() ){
            case R.id.visit_web_page:{
                String link = cursor.getString( cursor.getColumnIndex(DbHelper.I_LINK));
                Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse(link) );
                startActivity(intent);

                return true;
            }

            case R.id.play_item:{
                String mediaUrl = cursor.getString( cursor.getColumnIndex(DbHelper.I_MEDIA_URL));
                String mediaType = cursor.getString( cursor.getColumnIndex(DbHelper.I_MEDIA_TYPE));
                Intent intent = new Intent( Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(mediaUrl), mediaType);
                startActivity(intent);

                return true;
            }
        }
        return super.onContextItemSelected(item);
    }
}
