package sk.cde.yapco.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import sk.cde.yapco.FeedData;
import sk.cde.yapco.R;
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
    }

    public void onClick(View view) {
        EditText et = (EditText) findViewById(R.id.editText);
        String feed = et.getText().toString();

        try {
            RssFeedParser.ParseFeed pf = new RssFeedParser.ParseFeed();
            pf.execute(feed);

            FeedData fd = new FeedData(this);
            fd.insertChannel(pf.get());
             finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
