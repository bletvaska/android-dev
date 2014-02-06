package sk.cde.yapco.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import sk.cde.yapco.R;
import sk.cde.yapco.Repository;

/**
 * Created by mirek on 30.1.2014.
 */
public class ChannelInfoActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel_info);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Bundle extra = getIntent().getExtras();

        View view;
        // set title
        view = (TextView) findViewById(R.id.title);
        ((TextView)view).setText(extra.getString("title"));

        // set description
        view = (TextView) findViewById(R.id.description);
        ((TextView)view).setText(extra.getString("description"));
    }

    public void visitWebPage(View view){
        Bundle extra = getIntent().getExtras();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(extra.getString("link")));
        startActivity(intent);
    }

    public void showEpisodes(View view){
        Bundle extra = getIntent().getExtras();
        Intent intent = new Intent(this, ItemListActivity.class);
        intent.putExtra("channel_id", extra.getLong("channel_id"));
        startActivity(intent);
    }

    public void refreshEpisodes(View view){
        Bundle extra = getIntent().getExtras();
        Repository repository = new Repository(this);
        repository.refreshChannel(extra.getLong("channel_id"));
        Toast.makeText(this, "Channel has been refreshed", Toast.LENGTH_LONG).show();
    }

    public void unsubscribe( View view ){
        Bundle extra = getIntent().getExtras();
        Repository repository = new Repository(this);
        repository.deleteChannel(extra.getLong("channel_id"));
        Toast.makeText(this, "Channel has been deleted", Toast.LENGTH_LONG).show();
        finish();
    }
}