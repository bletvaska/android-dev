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
import sk.cde.yapco.activities.ChannelListActivity;
import sk.cde.yapco.rss.Channel;
import sk.cde.yapco.rss.Item;
import sk.cde.yapco.rss.RssFeedParser;

import java.util.concurrent.ExecutionException;


/**
 * Created by mirek on 14.1.2014.
 */
public class Repository {
    private static String TAG = "Repository";

    // table names
    public static final String CHANNEL_TABLE_NAME = "channel";
    public static final String ITEM_TABLE_NAME = "item";

    // column names
    public static final String C_ID             = "_id";
    public static final String C_TITLE          = "title";
    public static final String C_LINK           = "link";
    public static final String C_DESCRIPTION    = "description";
    public static final String C_RSS_FEED       = "rssFeed";
    public static final String C_PUBLISHED      = "published";
    public static final String C_MEDIA_URL      = "mediaUrl";
    public static final String C_MEDIA_TYPE     = "mediaType";
    public static final String C_MEDIA_LENGTH   = "mediaLength";
    public static final String C_CHID           = "chid";

    private DbHelper dbHelper;
    private Context context;
    private SQLiteDatabase db;

    public Repository(Context context){
        this.context = context;
        this.dbHelper = new DbHelper();
    }

    public void refreshChannel( long channelId ) {
        db = dbHelper.getWritableDatabase();

        // SELECT * FROM channel WHERE _id=?;
        Cursor cursor = db.query( CHANNEL_TABLE_NAME, null, "_id=?", new String[]{String.valueOf(channelId)},
                null, null, null);

        cursor.moveToFirst();
        String podcastUrl = cursor.getString(cursor.getColumnIndex(C_RSS_FEED));

        RssFeedParser.ParseFeed pf = new RssFeedParser.ParseFeed();
        pf.execute(podcastUrl);
        Channel channel = null;
        int counter = 0;
        try {
            channel = pf.get();
            if( channel == null )
                return;
            for( Item item : channel ){
                if( insertItem( channelId, item) != -1 )
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

            Notification.Builder notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText("There are " + counter + " new episodes.")
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, notification.build());
        }
    }

    public void refreshAllChannels(){
        this.db = dbHelper.getReadableDatabase();

        // SELECT * FROM channel;
        Cursor cursor = db.query( CHANNEL_TABLE_NAME, null, null, null, null, null, null );

        while( cursor.moveToNext() )
            refreshChannel( cursor.getLong(cursor.getColumnIndex(C_ID)));
    }

    public long insertChannel( Channel channel ){
        this.db = dbHelper.getWritableDatabase();

        // insert channel first
        ContentValues values = new ContentValues();

        values.put(Repository.C_TITLE, channel.title);
        values.put(Repository.C_LINK, channel.link);
        values.put(Repository.C_DESCRIPTION, channel.description);
        values.put(Repository.C_RSS_FEED, channel.podcastUrl);

        long rowId = db.insert(Repository.CHANNEL_TABLE_NAME, null, values);
        Log.i(TAG, String.format("Channel '%s' inserted with rowid %d", channel.title, rowId));

        // insert all of the items
        for (Item item : channel)
            insertItem(rowId, item);

        return rowId;
    }


    public long insertItem( Long channelId, Item item){
        this.db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(C_CHID, channelId);
        values.put(C_DESCRIPTION, item.description);
        values.put(C_LINK, item.link);
        values.put(C_TITLE, item.title);
        values.put(C_PUBLISHED, item.published.toString());
        values.put(C_MEDIA_URL, item.mediaUrl);
        values.put(C_MEDIA_LENGTH, item.mediaLength);
        values.put(C_MEDIA_TYPE, item.mediaType);

        long rowId = db.insert(ITEM_TABLE_NAME, null, values);
        Log.i(TAG, String.format("Episode '%s' inserted with rowid: %d", item.title, rowId));
        return rowId;
    }

    public void deleteChannel(long channelId){
        this.db = dbHelper.getWritableDatabase();
        db.delete( CHANNEL_TABLE_NAME, "_id=?", new String[]{String.valueOf(channelId)} );
    }

    public Cursor queryChannels() {
        db = dbHelper.getReadableDatabase();
        return db.query( Repository.CHANNEL_TABLE_NAME, null, null, null, null, null, Repository.C_TITLE + " ASC");
    }

    public Channel getChannel(Long channelId) {
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query( Repository.CHANNEL_TABLE_NAME, null,
                "_id=?", new String[]{channelId.toString()}, null, null, null);

        cursor.moveToFirst();

        return new Channel(
                cursor.getString(cursor.getColumnIndex(Repository.C_RSS_FEED)),
                cursor.getString(cursor.getColumnIndex(Repository.C_TITLE)),
                cursor.getString(cursor.getColumnIndex(Repository.C_DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(Repository.C_LINK)),
                null
        );
    }

    public Cursor queryItemsFromChannel(Long channelId) {
        db = dbHelper.getReadableDatabase();

        return db.query(
                Repository.ITEM_TABLE_NAME,
                null,
                "chid=?", new String[]{channelId.toString()},
                null, null, null);
    }

    public Item getItem(Long itemId) {
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query( Repository.ITEM_TABLE_NAME, null,
                "_id=?", new String[]{itemId.toString()},
                null, null, null);

        cursor.moveToFirst();

        return new Item(
                cursor.getString(cursor.getColumnIndex(C_TITLE)),
                cursor.getString(cursor.getColumnIndex(C_DESCRIPTION)),
                null,
                cursor.getString(cursor.getColumnIndex(C_LINK)),
                cursor.getString(cursor.getColumnIndex(C_MEDIA_URL)),
                cursor.getString(cursor.getColumnIndex(C_MEDIA_TYPE)),
                cursor.getInt(cursor.getColumnIndex(C_MEDIA_LENGTH))
        );
    }


    private class DbHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "database.db";
        private static final int DB_VERSION = 25;

        public DbHelper() {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            db.execSQL("PRAGMA foreign_keys = ON;");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(TAG, "onCreate()");

            String createTableItemQuery = String.format( "CREATE TABLE %s ( " +
                    "%s INTEGER PRIMARY KEY, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, " +
                    "%s TEXT UNIQUE, %s TEXT, %s LONG, " +
                    "FOREIGN KEY(%s) REFERENCES %s(%s) ON DELETE CASCADE)",
                    ITEM_TABLE_NAME, C_ID, C_CHID, C_TITLE, C_LINK, C_DESCRIPTION, C_PUBLISHED,
                    C_MEDIA_URL, C_MEDIA_TYPE, C_MEDIA_LENGTH,
                    C_CHID, CHANNEL_TABLE_NAME, C_ID
            );

            String createTableChannelQuery = String.format( "CREATE TABLE %s ( " +
                    "%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT, %s TEXT UNIQUE )",
                    CHANNEL_TABLE_NAME, C_ID, C_TITLE, C_LINK, C_DESCRIPTION, C_RSS_FEED
            );

            db.execSQL(createTableChannelQuery);
            db.execSQL(createTableItemQuery);

            // Java portál
            db.execSQL("INSERT INTO channel (title, link, description, rssFeed) VALUES(" +
                    "'Java portál', " +
                    "'http://www.java.cz', " +
                    "'Portál o programovacím jazyku Java a souvisejích technologiích (JAVA, JSP, XML, XSLT, HTML, EJB, SQL)', " +
                    "'http://java.cz/rss-2.0/articles.do?articleTypeId=2682&displayDownloads=true')");

            // Android Central Podcast
            db.execSQL("INSERT INTO channel (title, link, description, rssFeed) VALUES(" +
                    "'Android Central Podcast', " +
                    "'http://www.androidcentral.com', " +
                    "'Android podcast - Get all the latest news on the Android Platform with Phil and Mickey: Android Apps, the Droid, Nexus One, and more.', " +
                    "'http://feeds.feedburner.com/AndroidCentralPodcast?format=xml')");

            // English as a Second Language (ESL) Podcast - Learn English Online
            db.execSQL("INSERT INTO channel (title, link, description, rssFeed) VALUES(" +
                    "'English as a Second Language (ESL) Podcast - Learn English Online', " +
                    "'http://www.eslpod.com/index.html', " +
                    "'A podcast for those wanting to learn or improve their English - great for any ESL or EFL learner.  Visit us at http://www.eslpod.com.', " +
                    "'http://feeds.feedburner.com/EnglishAsASecondLanguagePodcast?format=xml')");

            // The Linux Action Show! MP3
            db.execSQL("INSERT INTO channel (title, link, description, rssFeed) VALUES(" +
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
    }
}
