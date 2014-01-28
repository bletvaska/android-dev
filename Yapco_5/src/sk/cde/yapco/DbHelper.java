package sk.cde.yapco;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import sk.cde.yapco.activities.AddFeedActivity;
import sk.cde.yapco.activities.ChannelListActivity;
import sk.cde.yapco.rss.Channel;
import sk.cde.yapco.rss.Item;
import sk.cde.yapco.rss.RssFeedParser;

import java.util.Random;
import java.util.concurrent.ExecutionException;

import static sk.cde.yapco.activities.AddFeedActivity.*;

/**
 * Created by mirek on 14.1.2014.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static String TAG = "DbHelper";

    private static final String DB_NAME = "database.db";
    private static final int DB_VERSION = 16;

    // channel table
    public static final String CHANNEL_TABLE_NAME = "channel";
    public static final String C_ID = "_id";
    public static final String C_TITLE = "title";
    public static final String C_LINK = "link";
    public static final String C_DESCRIPTION = "description";
    public static final String C_PODCAST_URL = "podcastUrl";

    public static final String ITEM_TABLE_NAME = "item";
    public static final String I_ID = "_id";
    public static final String I_TITLE = "title";
    public static final String I_LINK = "link";
    public static final String I_DESCRIPTION = "description";
    public static final String I_PUBLISHED = "published";
    public static final String I_MEDIA_URL = "mediaUrl";
    public static final String I_MEDIA_TYPE = "mediaType";
    public static final String I_MEDIA_LENGTH = "mediaLength";
    public static final String I_CHID = "chid";

    private static final String SQL_CREATE_TABLE_CHANNEL =
            "CREATE TABLE " + CHANNEL_TABLE_NAME + " ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "title TEXT, " +
                    "link TEXT, " +
                    "description TEXT, " +
                    "podcastUrl TEXT, " +
                    "UNIQUE(podcastUrl)" +
                    " )";

    private static final String SQL_CREATE_TABLE_ITEM =
            "CREATE TABLE " + ITEM_TABLE_NAME + " ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "chid INTEGER, " +    // foreign key
                    "title TEXT, " +
                    "link TEXT, " +
                    "description TEXT, " +
                    "published TEXT, " +
                    "mediaUrl TEXT, " +
                    "mediaType TEXT, " +
                    "mediaLength LONG," +
                    "UNIQUE(link) " +
                    " )";


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");

        db.execSQL(SQL_CREATE_TABLE_CHANNEL);
        db.execSQL(SQL_CREATE_TABLE_ITEM);

        // Java portál
        db.execSQL("INSERT INTO channel (title, link, description, podcastUrl) VALUES(" +
                "'Java portál', " +
                "'http://www.java.cz', " +
                "'Portál o programovacím jazyku Java a souvisejích technologiích (JAVA, JSP, XML, XSLT, HTML, EJB, SQL)', " +
                "'http://java.cz/rss-2.0/articles.do?articleTypeId=2682&displayDownloads=true')");

        // Android Central Podcast
        db.execSQL("INSERT INTO channel (title, link, description, podcastUrl) VALUES(" +
                "'Android Central Podcast', " +
                "'http://www.androidcentral.com', " +
                "'Android podcast - Get all the latest news on the Android Platform with Phil and Mickey: Android Apps, the Droid, Nexus One, and more.', " +
                "'http://feeds.feedburner.com/AndroidCentralPodcast?format=xml')");

        // English as a Second Language (ESL) Podcast - Learn English Online
        db.execSQL("INSERT INTO channel (title, link, description, podcastUrl) VALUES(" +
                "'English as a Second Language (ESL) Podcast - Learn English Online', " +
                "'http://www.eslpod.com/index.html', " +
                "'A podcast for those wanting to learn or improve their English - great for any ESL or EFL learner.  Visit us at http://www.eslpod.com.', " +
                "'http://feeds.feedburner.com/EnglishAsASecondLanguagePodcast?format=xml')");

        // The Linux Action Show! MP3
        db.execSQL("INSERT INTO channel (title, link, description, podcastUrl) VALUES(" +
                "'The Linux Action Show! MP3', " +
                "'http://www.jupiterbroadcasting.com', " +
                "'Audio versions of The Linux Action Show! A show that covers everything geeks care about in the open source and Linux world. Get a solid dose of Linux, gadgets, news events and much more!', " +
                "'http://feeds2.feedburner.com/TheLinuxActionShow')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, String.format("Upgrading DB from version %d to version %d", oldVersion, newVersion));
        // drop first
        db.execSQL("DROP TABLE IF EXISTS " + CHANNEL_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ITEM_TABLE_NAME);

        // create
        onCreate(db);
    }

    public static void refreshChannel( Context context, long channelId ) {
        SQLiteDatabase db = (new DbHelper(context)).getWritableDatabase();

        Cursor cursor = db.query(
                DbHelper.CHANNEL_TABLE_NAME,
                null,
                "_id=?", new String[]{String.valueOf(channelId)},
                null, null,
                null);

        cursor.moveToFirst();
        String podcastUrl = cursor.getString(cursor.getColumnIndex(DbHelper.C_PODCAST_URL));

        RssFeedParser.ParseFeed pf = new RssFeedParser.ParseFeed();
        pf.execute(podcastUrl);
        Channel channel = null;
        int counter = 0;
        try {
            channel = pf.get();
            for( Item item : channel ){
                if( insertItem(db, channelId, item) != -1 )
                    counter++;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if( counter > 0 ){
            Intent intent = new Intent(context, ChannelListActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("There are " + counter + " new episodes.")
                    .setContentIntent(pIntent)
                    .setAutoCancel(true)
                    .build();
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, notification);
        }

        db.close();
    }

    public static void refreshAllChannels( Context context ){
        SQLiteDatabase db = (new DbHelper(context)).getWritableDatabase();

        Cursor cursor = db.query(
                DbHelper.CHANNEL_TABLE_NAME,
                null,
                null, null,
                null, null,
                null);

        cursor.moveToFirst();

        while( !cursor.isAfterLast() ){
            long channelId = cursor.getLong(cursor.getColumnIndex(DbHelper.C_ID));
            refreshChannel( context, channelId );
            cursor.moveToNext();
        }

        db.close();
    }

    public static long insertItem(SQLiteDatabase db, Long channelId, Item item){
        ContentValues values = new ContentValues();

        values.put(DbHelper.I_CHID, channelId);
        values.put(DbHelper.I_DESCRIPTION, item.description);
        values.put(DbHelper.I_LINK, item.link);
        values.put(DbHelper.I_TITLE, item.title);
        values.put(DbHelper.I_PUBLISHED, item.published.toString());
        values.put(DbHelper.I_MEDIA_URL, item.mediaUrl);
        values.put(DbHelper.I_MEDIA_LENGTH, item.mediaLength);
        values.put(DbHelper.I_MEDIA_TYPE, item.mediaType);

        long rowId = db.insert(DbHelper.ITEM_TABLE_NAME, null, values);
        Log.i(TAG, String.format("INSERT INTO %s VALUES (0, %d, '%s', '%s', '%s', '%s', '%s', '%s', %d)",
                DbHelper.ITEM_TABLE_NAME, channelId, item.title, item.link, item.description, item.published,
                item.mediaUrl, item.mediaType, item.mediaLength));

        return rowId;
    }

    public static void unsubscribeChannel(Context context, long channelId ){
        SQLiteDatabase db = (new DbHelper(context)).getWritableDatabase();
        String[] params = new String[]{String.valueOf(channelId)};

        // delete channel from channel table
        db.delete(
                CHANNEL_TABLE_NAME,
                "_id=?",
                params
        );

        // delete all related items
        db.delete(
                ITEM_TABLE_NAME,
                "chid=?",
                params
        );

        db.close();
    }
}
