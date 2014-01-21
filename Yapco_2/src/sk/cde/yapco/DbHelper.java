package sk.cde.yapco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mirek on 14.1.2014.
 */
public class DbHelper extends SQLiteOpenHelper{

    private static String TAG = "DbHelper";

    private static final String DB_NAME = "database.db";
    private static final int DB_VERSION = 6;

    // channel table
    public static final String CHANNEL_TABLE_NAME = "channel";
    public static final String C_ID = "_id";
    public static final String C_TITLE = "title";
    public static final String C_LINK = "link";
    public static final String C_DESCRIPTION = "description";

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
                    "description TEXT" +
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
                    "mediaLength INTEGER" +
                    " )";


    public DbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");

        db.execSQL(SQL_CREATE_TABLE_CHANNEL);
        db.execSQL(SQL_CREATE_TABLE_ITEM);
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
