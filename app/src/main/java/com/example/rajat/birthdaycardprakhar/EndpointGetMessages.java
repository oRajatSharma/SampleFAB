package com.example.rajat.birthdaycardprakhar;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.JsonReader;
import android.util.Log;

import com.example.rajat.prakharbirthdaycard2016.backend.myApi.MyApi;
import com.example.rajat.prakharbirthdaycard2016.backend.myApi.model.MyBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hscuser on 20/03/16.
 */
public class EndpointGetMessages extends AsyncTask<Pair<Context, Long>, Void, Long> {
    private static MyApi myApiService = null;
    private Context context;
    private static final String TAG = "EndpointAsyncTask";
    private onFetchCompletion fetchCompletionListener;

    public EndpointGetMessages(onFetchCompletion listener) {
        fetchCompletionListener = listener;
    }

    @Override
    protected Long doInBackground(Pair<Context, Long>... params) {
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
        long id = params[0].second;

        try {
//            return myApiService.sayHi(name).execute().getData();
            Log.d(TAG, "Requesting for greetings with id > " + id);
            MyBean g = myApiService.receiveGreetings(id).execute();
            Log.d(TAG, "Received Greetings = " + g.getData());

            InputStream ins = new ByteArrayInputStream(g.getData().getBytes());

            List <GreetingMsg> msgList = readJsonStream(ins);


            MessageStore m = new MessageStore(context);
            for (GreetingMsg item: msgList) {
                item.print();
                m.addMsg(item);
            }

            return 1L;
        } catch (IOException e) {
            Log.d(TAG, "Exception " + e.getMessage());
            return 2L;
        }
    }

    @Override
    protected void onPostExecute(Long result) {
//        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        Log.d(TAG, "Result = " + result);

//        MessageStore m = new MessageStore(context);
//
//        ArrayList<GreetingMsg> msgList = m.getMessages();
//        Log.d(TAG, "Msg from Database");
//        for (GreetingMsg g : msgList) {
//            g.print();
//        }

        if (result == 1) {
            fetchCompletionListener.onMsgFetchComplete();
        }

    }




    public List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readMessagesArray(reader);
        }
        finally {
            reader.close();
        }
    }

    public List readMessagesArray(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginObject();
        String data = reader.nextName();

        if(data.equals("messages")) {

            reader.beginArray();
            while (reader.hasNext()) {
                messages.add(readMessage(reader));
            }
            reader.endArray();
        }
        reader.endObject();

        return messages;
    }

    public GreetingMsg readMessage(JsonReader reader) throws IOException {
        long id = -1;
        String text = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
            } else if (name.equals("msg")) {
                text = reader.nextString();
            }  else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new GreetingMsg(id, text);
    }




}
