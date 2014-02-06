package sk.cde.yapco;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by mirek on 21.1.2014.
 */
public class ItemAdapter extends CursorAdapter {

    public ItemAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(R.layout.episode_row, parent, false);

        // set title
        TextView titleView = (TextView) row.findViewById(R.id.episodeTitle);
        titleView.setText(cursor.getString(cursor.getColumnIndex(Repository.C_TITLE)));

        // set meta
        TextView metaView = (TextView) row.findViewById(R.id.episodeMeta);
        Long duration = cursor.getLong(cursor.getColumnIndex(Repository.C_MEDIA_LENGTH));
        metaView.setText(getHumanReadableByteCount(duration));

        // set tag
        row.setTag(cursor.getString(cursor.getColumnIndex(Repository.C_MEDIA_URL)));

        return row;
    }

    @Override
    public void bindView(View row, Context context, Cursor cursor) {
        // set title
        TextView titleView = (TextView) row.findViewById(R.id.episodeTitle);
        titleView.setText(cursor.getString(cursor.getColumnIndex(Repository.C_TITLE)));

        // set meta
        TextView metaView = (TextView) row.findViewById(R.id.episodeMeta);
        Long duration = cursor.getLong(cursor.getColumnIndex(Repository.C_MEDIA_LENGTH));
        metaView.setText(getHumanReadableByteCount(duration));

        // set tag
        row.setTag(cursor.getString(cursor.getColumnIndex(Repository.C_MEDIA_URL)));
    }

    private String getHumanReadableByteCount(Long bytes) {
        // http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "kMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
