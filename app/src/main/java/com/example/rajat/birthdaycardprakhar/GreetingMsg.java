package com.example.rajat.birthdaycardprakhar;

import android.util.Log;

/**
 * Created by hscuser on 21/03/16.
 */


public class GreetingMsg {
    long id;
    String msg;


    public GreetingMsg(long id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    private GreetingMsg() {

    };

    public void print() {
        Log.d("GreetingMsg", "Msg Id = " + id + "Msg = " + msg);
    }

}