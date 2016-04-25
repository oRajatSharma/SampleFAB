package com.example.rajat.birthdaycardprakhar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;

import rajat.birthdaycardprakhar.R;

public class GreetingList extends AppCompatActivity {

    public static final String TAG = "GreetingListAdapter";
    Cursor greetingCursor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");
        setContentView(R.layout.activity_greeting_list);

        MessageStore messageStore = new MessageStore(this);
        greetingCursor = messageStore.getMsgListCursor();

        CursorAdapter greetingListAdapter = new GreetingListAdapter(this, greetingCursor, 0);

        ListView lv = (ListView) findViewById(R.id.greeting_list_view);
        lv.setAdapter(greetingListAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called, closing cursor");
        greetingCursor.close();
    }
}
