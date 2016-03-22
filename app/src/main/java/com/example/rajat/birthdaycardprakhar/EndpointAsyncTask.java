package com.example.rajat.birthdaycardprakhar;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;

import com.example.rajat.prakharbirthdaycard2016.backend.myApi.MyApi;
import com.example.rajat.prakharbirthdaycard2016.backend.myApi.model.GreetingMsg;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by hscuser on 18/03/16.
 */

class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {
    private static MyApi myApiService = null;
    private Context context;
    private static final String TAG = "EndpointAsyncTask";

    @Override
    protected String doInBackground(Pair<Context, String>... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
//                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setRootUrl("https://prakharbirthdaycard2016.appspot.com/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        context = params[0].first;
        String name = params[0].second;

        try {
//            return myApiService.sayHi(name).execute().getData();

            name = name.replaceAll("\\\\n", " ");

            GreetingMsg g = myApiService.sendGreeting(name).execute();
            Log.d(TAG, "Msg Id = " + g.getMsgId() + "Msg = " + g.getMsgDetail());
            return g.getMsgDetail();
        } catch (IOException e) {
            Log.d(TAG, "Exception " + e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
//        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Result - " + result);
    }
}
