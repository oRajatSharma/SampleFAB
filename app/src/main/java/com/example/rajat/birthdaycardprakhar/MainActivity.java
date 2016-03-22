package com.example.rajat.birthdaycardprakhar;

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
import android.util.TypedValue;
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

import rajat.birthdaycardprakhar.R;

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
    EditText inputText;
    Timer imageTimer;
//    FloatingActionButton volumeFab;
    ImageButton sendButton;
    LinearLayout sendMsgLayout;
    ImageButton newMsgButton;
    LinearLayout showMsgLayout;
    TextView greetingText;

    View msgLayoutToRemove;


    private static int ANIM_IMAGE_CHANGE_DURATION = 800;
//    Animator.AnimatorListener animListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");
        activityContext = this;
        setContentView(R.layout.activity_main);

        imageView1 = (ImageView) findViewById(R.id.image1);
        imageView2 = (ImageView) findViewById(R.id.image2);
        inputText = (EditText) findViewById(R.id.inputText);
        sendMsgLayout = (LinearLayout) findViewById(R.id.sendMsgLayout);
        sendButton = (ImageButton) findViewById(R.id.sendMsgButton);
        showMsgLayout = (LinearLayout) findViewById(R.id.showMsgLayout);
        newMsgButton = (ImageButton) findViewById(R.id.newMsgButton);
        greetingText = (TextView) findViewById(R.id.greetingText);

        fillImageList();

        sendButton.setOnClickListener(sendMsgOnClick);
        imageView1.setOnClickListener(imgOnClick);
        imageView2.setOnClickListener(imgOnClick);
        newMsgButton.setOnClickListener(fabOnClick);

        EndpointGetMessages getMsg = new EndpointGetMessages(this);

        MessageStore m = new MessageStore(activityContext);
        long maxMsgId = m.getMaxMsgId();
        Log.d(TAG, "Max Msg Id " + maxMsgId);
        getMsg.execute(new Pair<Context, Long>(activityContext, maxMsgId));

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

    private void switchLayout(View viewToRemove, View viewToShow) {

        ObjectAnimator animRotateOut = ObjectAnimator.ofFloat(viewToRemove, View.ROTATION_X, 0f, 90f);
        ObjectAnimator animFadeOut = ObjectAnimator.ofFloat(viewToRemove, View.ALPHA, 1f, 0f);

        ObjectAnimator animRotateIn = ObjectAnimator.ofFloat(viewToShow, "rotationX", -270f, 0f);
        ObjectAnimator animFadeIn = ObjectAnimator.ofFloat(viewToShow, "alpha", 0f, 1f);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animRotateOut, animFadeOut, animRotateIn, animFadeIn);
        animSet.setDuration(600);

        viewToShow.setVisibility(View.VISIBLE);
        msgLayoutToRemove = viewToRemove;
        animFadeOut.addListener(textAnimListener);
        animSet.start();

    }


    View.OnClickListener fabOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switchLayout(showMsgLayout, sendMsgLayout);

//            sendMsgLayout.setVisibility(View.VISIBLE);
//            ObjectAnimator animFadeIn = ObjectAnimator.ofFloat(sendMsgLayout, "alpha", 0f, 1f);

//            animFadeIn.setDuration(600);
//            animFadeIn.start();

            Log.d(TAG, "Showing Input Box");

//            showMsgLayout.setVisibility(View.GONE);

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

        switchLayout(sendMsgLayout, showMsgLayout);
        // Hide message box
//        ObjectAnimator animFadeOut = ObjectAnimator.ofFloat(sendMsgLayout, "alpha", 1f, 0f);
//
//        animFadeOut.setDuration(600);
//        animFadeOut.start();
        Log.d(TAG, "Hiding Input Box");

//        sendMsgLayout.setVisibility(View.GONE);
//        showMsgLayout.setVisibility(View.VISIBLE);



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
            return "Happy Birthday"; // If no msg in list return from this function
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
                    int len = greeting.length();
                    if (len < 25) {
                        Log.v(TAG, "Setting text size to 18");
                        greetingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    } else if (len < 45)
                    {
                        Log.v(TAG, "Setting text size to 16");
                        greetingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    } else {
                        Log.v(TAG, "Setting text size to 14");
                        greetingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    }

                    greetingText.setText(greeting);

                }
            });
        }
    }

    private void bgImageChange(ImageView viewToRemove, ImageView viewToAdd) {

        /* Image Animation */
        ObjectAnimator animFadeOutDetail = ObjectAnimator.ofFloat(viewToRemove, "alpha", 1f, 0f);
        ObjectAnimator animFadeInDetail = ObjectAnimator.ofFloat(viewToAdd, "alpha", 0.6f, 1f);

        viewToAdd.setVisibility(View.VISIBLE);
        AnimatorSet animSetDetail = new AnimatorSet();

        animSetDetail.playTogether(animFadeOutDetail, animFadeInDetail);
        animSetDetail.setDuration(ANIM_IMAGE_CHANGE_DURATION);

        Log.d(TAG, "Starting Animation");

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


    Animator.AnimatorListener textAnimListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {}

        @Override
        public void onAnimationEnd(Animator animation) {
            msgLayoutToRemove.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {}
        @Override
        public void onAnimationRepeat(Animator animation) {}
    };

}
