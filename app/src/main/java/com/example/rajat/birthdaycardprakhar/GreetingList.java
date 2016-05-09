package com.example.rajat.birthdaycardprakhar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;

import rajat.birthdaycardprakhar.R;

public class GreetingList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "GreetingList";
    Cursor greetingCursor = null;
    CursorAdapter greetingListAdapter;
    public static final int MESSAGE_LOADER_ID = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_greeting_list);

//        Simple cursor Adapter
/*
        MessageStore messageStore = new MessageStore(this);
        greetingCursor = messageStore.getMsgListCursor();
*/

        // Using Content Resolver
/*
        String[] mNewProjection = {MessageStore.MsgEntry._ID,
                MessageStore.MsgEntry.COLUMN_NAME_ENTRY_ID, MessageStore.MsgEntry.COLUMN_NAME_MSG_TEXT};
        String mSelectionClause = null;
        String[] mSelectionArgs = null;
        String mSortOrder = MessageStore.MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";

        try {
                greetingCursor = getContentResolver().query(MessageStore.CONTENT_URI, mNewProjection, mSelectionClause,
                    mSelectionArgs, mSortOrder);

//            if (msgCursor != null && msgCursor.moveToFirst()) {


            CursorAdapter greetingListAdapter = new GreetingListAdapter(this, greetingCursor, 0);

            ListView lv = (ListView) findViewById(R.id.greeting_list_view);
            lv.setAdapter(greetingListAdapter);
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
*/

        // Using Cursor Loader
        greetingListAdapter = new GreetingListAdapter(this, null, 0);

        ListView lv = (ListView) findViewById(R.id.greeting_list_view);
        lv.setAdapter(greetingListAdapter);
        // End using Cursor Loader

        getSupportLoaderManager().initLoader(MESSAGE_LOADER_ID, null, this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
//        greetingCursor.close();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == MESSAGE_LOADER_ID) {

            Log.d(TAG, "onCreateLoader called");
            String[] mNewProjection = {MessageStore.MsgEntry._ID,
                    MessageStore.MsgEntry.COLUMN_NAME_ENTRY_ID, MessageStore.MsgEntry.COLUMN_NAME_MSG_TEXT};
            String mSelectionClause = null;
            String[] mSelectionArgs = null;
            String mSortOrder = MessageStore.MsgEntry.COLUMN_NAME_ENTRY_ID + " DESC";

            return new CursorLoader(this, MessageStore.CONTENT_URI, mNewProjection, mSelectionClause,
                    mSelectionArgs, mSortOrder);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished called");
        greetingListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset called");
        greetingListAdapter.swapCursor(null);
    }

}
