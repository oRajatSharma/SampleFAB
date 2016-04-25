package com.example.rajat.birthdaycardprakhar;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import rajat.birthdaycardprakhar.R;

/**
 * Created by hscuser on 13/04/16.
 */
public class GreetingListAdapter extends CursorAdapter {

    public static final String TAG = "GreetingListAdapter";

    public GreetingListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.v(TAG, "newView called");
        View v = LayoutInflater.from(context).inflate(R.layout.greeting_list_view_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.v(TAG, "bindView called");
        TextView greetingView = (TextView) view.findViewById(R.id.greeting_text_view);
        String greetingMsg = cursor.getString(cursor.getColumnIndexOrThrow(MessageStore.MsgEntry.COLUMN_NAME_MSG_TEXT));
        greetingView.setText(greetingMsg);

        TextView greetingIdView = (TextView) view.findViewById(R.id.greeting_id_view);
        String greetingId = cursor.getString(cursor.getColumnIndexOrThrow(MessageStore.MsgEntry.COLUMN_NAME_ENTRY_ID));
        greetingIdView.setText(greetingId);

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    //    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
//    }
}
