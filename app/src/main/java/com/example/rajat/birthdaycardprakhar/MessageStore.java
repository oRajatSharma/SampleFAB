package com.example.rajat.birthdaycardprakhar;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by hscuser on 21/03/16.
 */
public class MessageStore extends ContentProvider {


    private static final String TAG = "MessageStore";
    private Context context;

    MsgDbHelper mDbHelper;

    static final String PROVIDER_NAME = "com.example.rajat.birthdaycardprakhar";
    static final String URL = "content://" + PROVIDER_NAME + "/MessageStore";
//    static final String MAX_MSG_URL = "content://" + PROVIDER_NAME + "/MessageStore/#";

    static final Uri CONTENT_URI = Uri.parse(URL);
//    static final Uri MAX_MSG_URI = Uri.parse(MAX_MSG_URL);

    static final int ALL_MESSAGES = 1;
    static final int MESSAGE_WITH_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "MessageStore", ALL_MESSAGES);
        uriMatcher.addURI(PROVIDER_NAME, "MessageStore/#", MESSAGE_WITH_ID);
    }

//
//    public MessageStore(Context context) {
//        this.context = context;
//        mDbHelper = new MsgDbHelper(this.context);
//    }

    /* Inner class that defines the table contents */
    public static abstract class MsgEntry implements BaseColumns {
        public static final String TABLE_NAME = "MsgTable";
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_MSG_TEXT = "msg";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String LONG_TYPE = " LONG";

    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + MsgEntry.TABLE_NAME + " (" +
                    MsgEntry._ID + " INTEGER PRIMARY KEY," +
                    MsgEntry.COLUMN_NAME_ENTRY_ID + LONG_TYPE + COMMA_SEP +
                    MsgEntry.COLUMN_NAME_MSG_TEXT + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MsgEntry.TABLE_NAME;


    public class MsgDbHelper extends SQLiteOpenHelper {
        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Msg.db";

        public MsgDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.d(TAG, "Creating Database");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Creating tables");
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    @Override
    public boolean onCreate() {
        context = getContext();
        mDbHelper = new MsgDbHelper(this.context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        if (db == null) {
            Log.d(TAG, "Unable to create database");
            return false;
        }
        return true;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        newRowId = db.insert(
                MsgEntry.TABLE_NAME,
                MsgEntry.COLUMN_NAME_MSG_TEXT,
                values);

        Log.d(TAG, "Msg Added Row Id " + newRowId);
        if (newRowId > 0) {
            Uri addedUri = ContentUris.withAppendedId(CONTENT_URI, newRowId);
            getContext().getContentResolver().notifyChange(addedUri, null);
            return addedUri;
        }

        throw new SQLException("Failed to add message" + uri);
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case ALL_MESSAGES:
                Log.d(TAG, "Get all messages");
                qb.setTables(MsgEntry.TABLE_NAME);
                break;
            default:
                throw new SQLException("Invalid URI " + uri);
        }

        Cursor cursor = qb.query(db,
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        if (cursor != null) {
            return cursor;
        }

        throw new SQLException("Failed to query" + uri);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_MESSAGES:
                Log.d(TAG, "Get all messages");
                return "vnd.android.cursor.dir/";
            default:
               return null;
        }
    }

    //    public void addMsg(GreetingMsg g) {
//        // Gets the data repository in write mode
//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//// Create a new map of values, where column names are the keys
//        ContentValues values = new ContentValues();
//        values.put(MsgEntry.COLUMN_NAME_ENTRY_ID, g.id);
//        values.put(MsgEntry.COLUMN_NAME_MSG_TEXT, g.msg);
//
//// Insert the new row, returning the primary key value of the new row
//        long newRowId;
//        newRowId = db.insert(
//                MsgEntry.TABLE_NAME,
//                MsgEntry.COLUMN_NAME_MSG_TEXT,
//                values);
//
//        Log.d(TAG, "Msg Added Row Id " + newRowId);
//    }

//    public ArrayList<GreetingMsg> getMessages() {
//
//        // Gets the data repository in write mode
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        String[] projection = {MsgEntry._ID, MsgEntry.COLUMN_NAME_ENTRY_ID, MsgEntry.COLUMN_NAME_MSG_TEXT};
//        // How you want the results sorted in the resulting Cursor
//        String sortOrder = MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";
//        String selection = null;
//        String[] selectionArgs = null;
//
//        ArrayList<GreetingMsg> msgList = new ArrayList<>();
//
//        Cursor cursor = db.query(
//                MsgEntry.TABLE_NAME,  // The table to query
//                projection,                               // The columns to return
//                selection,                                // The columns for the WHERE clause
//                selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                sortOrder                                 // The sort order
//        );
//
//        int colId = cursor.getColumnIndex(MsgEntry.COLUMN_NAME_ENTRY_ID);
//        int colMsg = cursor.getColumnIndex(MsgEntry.COLUMN_NAME_MSG_TEXT);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            while (cursor.isAfterLast() == false) {
//
//                GreetingMsg g = new GreetingMsg(cursor.getLong(colId), cursor.getString(colMsg));
//                msgList.add(g);
////                Log.v(TAG, "Adding msg to list");
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        return msgList;
//    }

//    public long getMaxMsgId() {
//
//        // Gets the data repository in write mode
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        String[] projection = {MsgEntry._ID, MsgEntry.COLUMN_NAME_ENTRY_ID, MsgEntry.COLUMN_NAME_MSG_TEXT};
//        // How you want the results sorted in the resulting Cursor
//        String sortOrder = MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";
//        String selection = null;
//        String[] selectionArgs = null;
//
//        Cursor cursor = db.query(
//                MsgEntry.TABLE_NAME,  // The table to query
//                projection,                               // The columns to return
//                selection,                                // The columns for the WHERE clause
//                selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                sortOrder                                 // The sort order
//        );
//
//        int colId = cursor.getColumnIndex(MsgEntry.COLUMN_NAME_ENTRY_ID);
//        long maxMsgId = 0;
//
//        if (cursor != null && cursor.moveToFirst()) {
//            maxMsgId = cursor.getLong(colId); // First cursor element will give maximum value
//        }
//        cursor.close();
//        return maxMsgId;
//    }


//    public Cursor getMsgListCursor() {
//        SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//        String[] projection = {MsgEntry._ID, MsgEntry.COLUMN_NAME_ENTRY_ID, MsgEntry.COLUMN_NAME_MSG_TEXT};
//        // How you want the results sorted in the resulting Cursor
//        String sortOrder = MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";
//        String selection = null;
//        String[] selectionArgs = null;
//
//        Cursor cursor = db.query(
//                MsgEntry.TABLE_NAME,  // The table to query
//                projection,                               // The columns to return
//                selection,                                // The columns for the WHERE clause
//                selectionArgs,                            // The values for the WHERE clause
//                null,                                     // don't group the rows
//                null,                                     // don't filter by row groups
//                sortOrder                                 // The sort order
//        );
//
//        return cursor;
//
//    }

}
