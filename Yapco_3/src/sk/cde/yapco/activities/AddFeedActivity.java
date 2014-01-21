package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import org.xmlpull.v1.XmlPullParserException;
import sk.cde.yapco.*;

import java.io.IOException;
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
            ParseFeed pf = new ParseFeed();
            pf.execute(feed);
            insertChannel(pf.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }


    public void insertChannel(Channel channel) {
        SQLiteDatabase db = (new DbHelper(this)).getWritableDatabase();

        // insert channel first
        ContentValues values = new ContentValues();

//        values.put(DbHelper.C_ID, 0);
        values.put(DbHelper.C_TITLE, channel.title);
        values.put(DbHelper.C_LINK, channel.link);
        values.put(DbHelper.C_DESCRIPTION, channel.description);

        long rowId = db.insert(DbHelper.CHANNEL_TABLE_NAME, null, values);
        Log.i(TAG, String.format("INSERT INTO %s VALUES (0, '%s', '%s', '%s')",
                DbHelper.CHANNEL_TABLE_NAME, channel.title, channel.link, channel.description));

        // insert all of the items
        for (Item item : channel) {
            values = new ContentValues();

//            values.put(DbHelper.I_ID, 0);
            values.put(DbHelper.I_CHID, rowId);
            values.put(DbHelper.I_DESCRIPTION, item.description);
            values.put(DbHelper.I_LINK, item.link);
            values.put(DbHelper.I_TITLE, item.title);
            values.put(DbHelper.I_PUBLISHED, item.published.toString());
            values.put(DbHelper.I_MEDIA_URL, item.mediaUrl);
            values.put(DbHelper.I_MEDIA_LENGTH, item.mediaLength);
            values.put(DbHelper.I_MEDIA_TYPE, item.mediaType);

            db.insert(DbHelper.ITEM_TABLE_NAME, null, values);
            Log.i(TAG, String.format("INSERT INTO %s VALUES (0, %d, '%s', '%s', '%s', '%s', '%s', '%s', %d)",
                    DbHelper.ITEM_TABLE_NAME, rowId, item.title, item.link, item.description, item.published,
                    item.mediaUrl, item.mediaType, item.mediaLength));
        }

    }

    public class ParseFeed extends AsyncTask<String, Void, Channel> {

        @Override
        protected Channel doInBackground(String... params) {
            try {
                return RssFeedParser.parse(params[0]);
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
