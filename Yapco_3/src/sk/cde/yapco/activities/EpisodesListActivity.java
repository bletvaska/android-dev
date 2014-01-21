package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import sk.cde.yapco.DbHelper;
import sk.cde.yapco.EpisodeAdapter;
import sk.cde.yapco.R;

/**
 * Created by mirek on 21.1.2014.
 */
public class EpisodesListActivity extends Activity {
    private static final String TAG = "EpisodesListAct";
    private SQLiteDatabase db;
    private EpisodeAdapter adapter;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.episodes_list_activity);
        Log.i(TAG, "onCreate()");

        System.out.println("Showing " + getIntent().getExtras().get("channel_id"));

        this.db = (new DbHelper(this)).getReadableDatabase();

        String[] params = {getIntent().getExtras().getString("channel_id")};
        Cursor cursor = db.query(DbHelper.ITEM_TABLE_NAME, null, "chid=?", params, null, null, null);
        this.adapter = new EpisodeAdapter(this, cursor);

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
    }

}
