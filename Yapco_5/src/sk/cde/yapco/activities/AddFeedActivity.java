package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import sk.cde.yapco.DbHelper;
import sk.cde.yapco.R;
import sk.cde.yapco.rss.Channel;
import sk.cde.yapco.rss.Item;
import sk.cde.yapco.rss.RssFeedParser;

import java.util.concurrent.ExecutionException;

public class AddFeedActivity extends Activity {
    private static final String TAG = "AddFeedActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        SQLiteDatabase db = new DbHelper(this).getWritableDatabase();
        db.close();
    }

    public void onClick(View view) {
        EditText et = (EditText) findViewById(R.id.editText);
        String feed = et.getText().toString();

        try {
            RssFeedParser.ParseFeed pf = new RssFeedParser.ParseFeed();
            pf.execute(feed);
            insertChannel(pf.get());
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    private void insertChannel(Channel channel) {
        SQLiteDatabase db = (new DbHelper(this)).getWritableDatabase();

        // insert channel first
        ContentValues values = new ContentValues();

//        values.put(DbHelper.C_ID, 0);
        values.put(DbHelper.C_TITLE, channel.title);
        values.put(DbHelper.C_LINK, channel.link);
        values.put(DbHelper.C_DESCRIPTION, channel.description);
        values.put(DbHelper.C_PODCAST_URL, channel.podcastUrl);

        long rowId = db.insert(DbHelper.CHANNEL_TABLE_NAME, null, values);
        Log.i(TAG, String.format("INSERT INTO %s VALUES (0, '%s', '%s', '%s', '%s')",
                DbHelper.CHANNEL_TABLE_NAME, channel.title, channel.link, channel.description, channel.podcastUrl));

        // insert all of the items
        for (Item item : channel) {
            DbHelper.insertItem(db, rowId, item);
        }

        db.close();

    }
}
