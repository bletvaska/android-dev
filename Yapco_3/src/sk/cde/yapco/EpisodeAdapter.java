package sk.cde.yapco;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by mirek on 21.1.2014.
 */
public class EpisodeAdapter extends CursorAdapter {

    public EpisodeAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        System.out.println("newView()");
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.episode_row, parent, false);

        // set title
        TextView titleView = (TextView) row.findViewById(R.id.episodeTitle);
        titleView.setText(cursor.getString(cursor.getColumnIndex(DbHelper.I_TITLE)));

        // set meta
        TextView metaView = (TextView) row.findViewById(R.id.episodeMeta);
        Long duration = cursor.getLong(cursor.getColumnIndex(DbHelper.I_MEDIA_LENGTH));
        metaView.setText(getDurationString(duration));

        // set tag
        row.setTag(cursor.getString(cursor.getColumnIndex(DbHelper.I_MEDIA_URL)));

        return row;
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        System.out.println("bindView()");

        // set title
        TextView titleView = (TextView) row.findViewById(R.id.episodeTitle);
        titleView.setText(cursor.getString(cursor.getColumnIndex(DbHelper.I_TITLE)));

        // set meta
        TextView metaView = (TextView) row.findViewById(R.id.episodeMeta);
        Long duration = cursor.getLong(cursor.getColumnIndex(DbHelper.I_MEDIA_LENGTH));
        metaView.setText(getDurationString(duration));

        // set tag
        row.setTag(cursor.getString(cursor.getColumnIndex(DbHelper.I_MEDIA_URL)));
    }

    private String getDurationString(Long duration) {
        return String.format("Duration: %d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }
}
