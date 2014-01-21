package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.xmlpull.v1.XmlPullParserException;
import sk.cde.yapco.Channel;
import sk.cde.yapco.R;
import sk.cde.yapco.RssFeedParser;

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
    }

    public void onClick(View view) {
        EditText et = (EditText) findViewById(R.id.editText);
        String feed = et.getText().toString();

        try {
            ParseFeed pf = new ParseFeed();
            pf.execute(feed);
            Channel channel = pf.get();
            System.out.println(channel);

            Uri uri = Uri.parse(channel.get(0).mediaUrl);

            // prehratie prehravacom
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "audio/*");

            // prehratie prehliadacom
            //Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            startActivity(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
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
