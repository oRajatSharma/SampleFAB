package com.example.rajat.birthdaycardprakhar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by hscuser on 21/03/16.
 */
public class MessageStore {


        private static final String TAG = "MessageStore";
        private Context context;

        MsgDbHelper mDbHelper;

        // To prevent someone from accidentally instantiating the contract class,
        // give it an empty constructor.
        public MessageStore(Context context) {
            this.context = context;
             mDbHelper = new MsgDbHelper(this.context);
        }

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

        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "Creating tables");
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }

    public void addMsg(GreetingMsg g) {
        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(MsgEntry.COLUMN_NAME_ENTRY_ID, g.id);
        values.put(MsgEntry.COLUMN_NAME_MSG_TEXT, g.msg);

// Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                MsgEntry.TABLE_NAME,
                MsgEntry.COLUMN_NAME_MSG_TEXT,
                values);

        Log.d(TAG, "Msg Added Row Id " + newRowId);
    }

    public ArrayList<GreetingMsg> getMessages() {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {MsgEntry._ID, MsgEntry.COLUMN_NAME_ENTRY_ID, MsgEntry.COLUMN_NAME_MSG_TEXT};
        // How you want the results sorted in the resulting Cursor
        String sortOrder = MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        String selection = null;
        String[] selectionArgs = null;

        ArrayList<GreetingMsg> msgList = new ArrayList<>();

        Cursor cursor = db.query(
                MsgEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int colId = cursor.getColumnIndex(MsgEntry.COLUMN_NAME_ENTRY_ID);
        int colMsg = cursor.getColumnIndex(MsgEntry.COLUMN_NAME_MSG_TEXT);

        if (cursor != null && cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                GreetingMsg g = new GreetingMsg(cursor.getLong(colId), cursor.getString(colMsg));
                msgList.add(g);
//                Log.v(TAG, "Adding msg to list");
                cursor.moveToNext();
            }
        }
        cursor.close();
        return msgList;
    }

    public long getMaxMsgId() {

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {MsgEntry._ID, MsgEntry.COLUMN_NAME_ENTRY_ID, MsgEntry.COLUMN_NAME_MSG_TEXT};
        // How you want the results sorted in the resulting Cursor
        String sortOrder = MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        String selection = null;
        String[] selectionArgs = null;

        Cursor cursor = db.query(
                MsgEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        int colId = cursor.getColumnIndex(MsgEntry.COLUMN_NAME_ENTRY_ID);
        long maxMsgId = 0;

        if (cursor != null && cursor.moveToFirst()) {
            maxMsgId = cursor.getLong(colId); // First cursor element will give maximum value
        }
        cursor.close();
        return maxMsgId;
    }

}
