package com.example.rajat.prakharBirthdayCard2016.backend;

/** The object model for the data we are sending through endpoints */
public class MyBean {

    private String myData;

    public String getData() {
        return myData;
    }

    public void setData(String data) {
//        myData = data;
//        myData = myData.replace('\\', '/');
        String d = data.replaceAll("\\\\\"", "\"");
        System.out.println(d);
        myData = d;
//        myData = myData.replaceAll("\\\\", "");
    }

    public void appendData(String data) {
//        myData = myData + data;
//        myData = myData.replace('\\', '/');
        String d = data.replaceAll("\\\\", "");
        System.out.println(d);
        myData = myData + d;
    }
}