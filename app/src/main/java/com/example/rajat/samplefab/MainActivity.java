package com.example.rajat.samplefab;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements onFetchCompletion {

    Context activityContext;
    public static final String TAG = "MainActivity";

    ArrayList<String> imageList;
    Iterator<String> imageListIterator;
    private static final long IMAGE_CHANGE_DELTA = 8000; // in millisec

    ArrayList<GreetingMsg> greetingList;
    Iterator<GreetingMsg> greetingListIterator;
    Timer greetingTimer;
    private static final long GREETING_CHANGE_DELTA = 4000; // in millisec


    ImageView imageView1, imageView2;
    ImageView imageViewToRemove;
//    FloatingActionButton fab;
    EditText inputText;
    Timer imageTimer;
//    FloatingActionButton volumeFab;
    ImageButton sendButton;
    LinearLayout sendMsgLayout;
    ImageButton newMsgButton;
    LinearLayout showMsgLayout;
    TextView greetingText;

    private static int ANIM_IMAGE_CHANGE_DURATION = 800;
//    Animator.AnimatorListener animListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");
        activityContext = this;
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
//        fab = (FloatingActionButton) findViewById(R.id.fab);
        inputText = (EditText) findViewById(R.id.inputText);
        sendMsgLayout = (LinearLayout) findViewById(R.id.sendMsgLayout);
        sendButton = (ImageButton) findViewById(R.id.sendMsgButton);
        showMsgLayout = (LinearLayout) findViewById(R.id.showMsgLayout);
        newMsgButton = (ImageButton) findViewById(R.id.newMsgButton);
        greetingText = (TextView) findViewById(R.id.greetingText);

        fillImageList();
//        fab.setOnClickListener(fabOnClick);
        sendButton.setOnClickListener(sendMsgOnClick);
        imageView1.setOnClickListener(imgOnClick);
        imageView2.setOnClickListener(imgOnClick);
        newMsgButton.setOnClickListener(fabOnClick);

        EndpointGetMessages getMsg = new EndpointGetMessages();

        MessageStore m = new MessageStore(activityContext);
        long maxMsgId = m.getMaxMsgId();
        Log.d(TAG, "Max Msg Id " + maxMsgId);
        getMsg.execute(new Pair<Context, Long>(activityContext, maxMsgId));

//        greetingText.setTextColor(0xFFFFFF);
        greetingList = m.getMessages();
        greetingListIterator = greetingList.iterator();
    }

    private void startImageTimer() {
        imageTimer = new Timer();
        ImageChangeTimerTask imageChangeTimerTask = new ImageChangeTimerTask();
        imageTimer.schedule(imageChangeTimerTask, 0, IMAGE_CHANGE_DELTA);
    }

    private void stopImageTimer() {
        imageTimer.cancel();
        imageTimer.purge();
        imageTimer = null;
    }



    private void startGreetingTimer() {
        greetingTimer = new Timer();
        GreetingChangeTimerTask greetingChangeTimerTask = new GreetingChangeTimerTask();
        greetingTimer.schedule(greetingChangeTimerTask, 0, GREETING_CHANGE_DELTA);
    }

    private void stopGreetingTimer() {
        greetingTimer.cancel();
        greetingTimer.purge();
        greetingTimer = null;
    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        // Stop timer
        stopImageTimer();
        stopGreetingTimer();
//        imageTimer.cancel();

        //TODO: Stop animation
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        // Start timer again
        startImageTimer();
        startGreetingTimer();

    }

    View.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
////            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
////                    .setAction("Action", null).show();
//
//            inputText.setX(view.getX());
//            inputText.setY(view.getY());
//            inputText.setWidth(view.getWidth());
//            inputText.setHeight(view.getHeight());
//
//            inputText.setVisibility(View.VISIBLE);
//            ObjectAnimator animFadeIn = ObjectAnimator.ofFloat(inputText, "alpha", 0f, 1f);

            sendMsgLayout.setVisibility(View.VISIBLE);
            ObjectAnimator animFadeIn = ObjectAnimator.ofFloat(sendMsgLayout, "alpha", 0f, 1f);

//            ObjectAnimator animX = ObjectAnimator.ofFloat(inputText, "x", 20);
//            ObjectAnimator animY = ObjectAnimator.ofFloat(inputText, "y", 20);

//            ObjectAnimator animW = ObjectAnimator.ofFloat(inputText, "width", 200);
//            ObjectAnimator animH = ObjectAnimator.ofFloat(inputText, "height", 200);

//            AnimatorSet animSetDetail = new AnimatorSet();
//            animSetDetail.playTogether(animFadeIn, animX, animY, animW, animH);
//            animSetDetail.playTogether(animFadeIn, animX);


            animFadeIn.setDuration(600);
            animFadeIn.start();
//            animSetDetail.setDuration(2000);
//            animSetDetail.start();
            Log.d(TAG, "Showing Input Box");

//            fab.setVisibility(View.GONE);
            showMsgLayout.setVisibility(View.GONE);
//            inputText.setFocusable(true);
//            inputText.requestFocus();
//            showSoftKeyboard(inputText);
        }
    };

    View.OnClickListener sendMsgOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String msg = (String) inputText.getText().toString();

            if(msg == null || msg.equals("")) {
                Log.d(TAG, "Empty message");
                return;
            }

            // Sending Message to server
            EndpointsAsyncTask getMsg = new EndpointsAsyncTask();
            getMsg.execute(new Pair<Context, String>(activityContext, msg));

            hideKeyboard();
            hideSendMsgLayout();
            inputText.setText("");
        }
    };

    private void hideSendMsgLayout() {

        // Hide message box
        ObjectAnimator animFadeOut = ObjectAnimator.ofFloat(sendMsgLayout, "alpha", 1f, 0f);

        animFadeOut.setDuration(600);
        animFadeOut.start();
        Log.d(TAG, "Hiding Input Box");

        // TODO: on completion of animation set visibility to gone
        sendMsgLayout.setVisibility(View.GONE);
//        fab.setVisibility(View.VISIBLE);
        showMsgLayout.setVisibility(View.VISIBLE);
//        inputText.clearFocus();
//        InputMethodManager imm = (InputMethodManager)
//                getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(sendMsgLayout, InputMethodManager.RESULT_HIDDEN);

    }


    private void fillImageList() {
        imageList = new ArrayList<String>();

        try {
            String[] fileList = this.getAssets().list("imagesToDisplay");
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

            int j = 0;
            for (int i = 0; i < fileList.length; i++) {
                String fileName = fileList[i];

                String fileExt = MimeTypeMap.getFileExtensionFromUrl(fileName);
                if (null != fileExt) {
                    String mimeType = mimeTypeMap.getMimeTypeFromExtension(fileExt);
                    Log.d(TAG, "File " + fileName + "Ext " + fileExt + "MimeType " + mimeType);

                    if (mimeType != null && mimeType.contains("image/")) {
                        Log.d(TAG, "Adding Image file to imageList " + fileName);
                        imageList.add(fileName);
                    }
                }
            }
        } catch (Exception e) {

        }

        imageListIterator = imageList.iterator();

//        String imageToDisplay = getNextImage(imageListIterator);
//        imageView1.setVisibility(View.VISIBLE);
    }

    String getNextImage(Iterator it) {
        final String imageToDisplay;

        if (!imageListIterator.hasNext()) {
            // Reset iterator to first image if it has reached last image
            imageListIterator = imageList.iterator();
        }

        if (imageListIterator.hasNext())
        {
            imageToDisplay = imageListIterator.next();
        } else {
            return null; // If no image in list return from this function
        }

        return imageToDisplay;
    }

//    TimerTask imageChangeTimerTask = new TimerTask() {
        class ImageChangeTimerTask extends TimerTask {
        @Override
        public void run() {
            final String imageToDisplay;

//            if (!imageListIterator.hasNext()) {
//                // Reset iterator to first image if it has reached last image
//                imageListIterator = imageList.iterator();
//            }
//
//            if (imageListIterator.hasNext())
//            {
//                imageToDisplay = imageListIterator.next();
//            } else {
//                return; // If no image in list return from this function
//            }
            imageToDisplay = getNextImage(imageListIterator);
            final Drawable d;

            try {
                InputStream inputStream = activityContext.getAssets().open("imagesToDisplay/" + imageToDisplay);
                d = Drawable.createFromStream(inputStream, imageToDisplay);

                if (inputStream != null) {
                    inputStream.close();
                    inputStream = null;
                }

            } catch (Exception e) {
                Log.d(TAG, "Unable to display image");
                // TODO: show some background image
                return;
            }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Changing image to " + imageToDisplay);
//                        try {
//                            InputStream inputStream = activityContext.getAssets().open("imagesToDisplay/" + imageToDisplay);
//                            Drawable d = Drawable.createFromStream(inputStream, imageToDisplay);

                        // Alternate image display between 2 views for animations
                        if (imageView1.getVisibility() == View.GONE && imageView2.getVisibility() == View.GONE) {
                            // First time, display image without animation
                            Log.d(TAG, "Setting drawable to imageView1");
                            imageView1.setImageDrawable(d);
                            imageView1.setVisibility(View.VISIBLE);
                            imageView1.setAlpha(1f);
                        } else if (imageView1.getVisibility() == View.GONE) {
                            Log.d(TAG, "Setting drawable to imageView1");
                            imageView1.setImageDrawable(d);
                            bgImageChange(imageView2, imageView1);
                        } else {
                            Log.d(TAG, "Setting drawable to imageView2");
                            imageView2.setImageDrawable(d);
                            bgImageChange(imageView1, imageView2);
                        }

//                        } catch (Exception e) {
//                            Log.d(TAG, "Unable to display image");
//                            // TODO: show some background image
//                        }
                    }
                });
        }
    }
//    };

    String getNextGreeting(Iterator it) {
        String greeting = "";
        GreetingMsg g;

        if (!greetingListIterator.hasNext()) {
            // Reset iterator to first msg if it has reached last msg
            greetingListIterator = greetingList.iterator();
        }

        if (greetingListIterator.hasNext())
        {
            g = (GreetingMsg) greetingListIterator.next();
            greeting = g.msg;
        } else {
            return null; // If no image in list return from this function
        }

        return greeting;
    }


    class GreetingChangeTimerTask extends TimerTask {
        @Override
        public synchronized void run() {
            final String greeting = getNextGreeting(greetingListIterator);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Changing msg to " + greeting);
                    greetingText.setText(greeting);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sendMsg) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void bgImageChange(ImageView viewToRemove, ImageView viewToAdd) {

//        ObjectAnimator animRotateOut = ObjectAnimator.ofFloat(heading, View.ROTATION_X, 0f, 90f);
//        ObjectAnimator animFadeOut = ObjectAnimator.ofFloat(heading, View.ALPHA, 1f, 0f);
//
//        ObjectAnimator animRotateIn = ObjectAnimator.ofFloat(headingNew, "rotationX", -270f, 0f);
//        ObjectAnimator animFadeIn = ObjectAnimator.ofFloat(headingNew, "alpha", 0f, 1f);

        /* Image Animation */
//        ObjectAnimator animMoveOut = ObjectAnimator.ofFloat(viewToRemove, "x", 0f, 80f);
        ObjectAnimator animFadeOutDetail = ObjectAnimator.ofFloat(viewToRemove, "alpha", 1f, 0f);

//        ObjectAnimator animMoveIn = ObjectAnimator.ofFloat(viewToAdd, "x", -80f, 0f);
        ObjectAnimator animFadeInDetail = ObjectAnimator.ofFloat(viewToAdd, "alpha", 0.6f, 1f);


//        AnimatorSet animSet = new AnimatorSet();
//        animSet.playTogether(animRotateOut, animFadeOut, animRotateIn, animFadeIn);

        viewToAdd.setVisibility(View.VISIBLE);
        AnimatorSet animSetDetail = new AnimatorSet();
//        animSetDetail.playTogether(animMoveOut, animFadeOutDetail, animMoveIn, animFadeInDetail);
        animSetDetail.playTogether(animFadeOutDetail, animFadeInDetail);
        animSetDetail.setDuration(ANIM_IMAGE_CHANGE_DURATION);

        Log.d(TAG, "Starting Animation");
//        animSet.start();


        imageViewToRemove = viewToRemove;
        animFadeOutDetail.addListener(animListener);

        animSetDetail.start();
    }

    Animator.AnimatorListener animListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {

//            if (animation == animFadeOutDetail) {
                imageViewToRemove.setVisibility(View.GONE);
                imageViewToRemove.setImageDrawable(null);
//            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Log.d(TAG, "onBackPressed");
//        fab.setFocusable(true);
//        fab.requestFocus();
        newMsgButton.setFocusable(true);
        newMsgButton.requestFocus();
        if (sendMsgLayout.getVisibility() == View.VISIBLE) {
//            hideKeyboard();
            hideSendMsgLayout();
        }
//        } else {
//            super.onBackPressed();
//        }
    }

    View.OnClickListener imgOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (sendMsgLayout.getVisibility() == View.VISIBLE) {
                hideKeyboard();
                hideSendMsgLayout();
            }
        }
    };

    public void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) activityContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        Log.v(TAG, "Showing keyboard");
        // imm.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activityContext.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            Log.v(TAG, "Hiding Keyboard");
//            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
        }
    }

    @Override
    public synchronized void onMsgFetchComplete() {
        MessageStore m = new MessageStore(activityContext);
        greetingList = m.getMessages();
        greetingListIterator = greetingList.iterator();
    }
}
