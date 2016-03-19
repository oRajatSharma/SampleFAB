package com.example.rajat.samplefab;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    Context activityContext;
    public static final String TAG = "MainActivity";

    ArrayList<String> imageList;
    Iterator<String> imageListIterator;
    private static final long IMAGE_CHANGE_DELTA = 5000; // in millisec

    ImageView imageView1, imageView2;
    ImageView imageViewToRemove;
    FloatingActionButton fab;
    TextView inputText;
    Timer imageTimer;

    private static int ANIM_IMAGE_CHANGE_DURATION = 1000;
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        inputText = (TextView) findViewById(R.id.inputText);

        fillImageList();
        fab.setOnClickListener(fabOnClick);

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

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        // Stop timer
        stopImageTimer();
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

        EndpointsAsyncTask getMsg = new EndpointsAsyncTask();
        getMsg.execute(new Pair<Context, String>(this, "Rajat"));
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
            inputText.setVisibility(View.VISIBLE);
            ObjectAnimator animFadeIn = ObjectAnimator.ofFloat(inputText, "alpha", 0f, 1f);
//            ObjectAnimator animX = ObjectAnimator.ofFloat(inputText, "x", 20);
//            ObjectAnimator animY = ObjectAnimator.ofFloat(inputText, "y", 20);

//            ObjectAnimator animW = ObjectAnimator.ofFloat(inputText, "width", 200);
//            ObjectAnimator animH = ObjectAnimator.ofFloat(inputText, "height", 200);

//            AnimatorSet animSetDetail = new AnimatorSet();
//            animSetDetail.playTogether(animFadeIn, animX, animY, animW, animH);
//            animSetDetail.playTogether(animFadeIn, animX);


            animFadeIn.setDuration(2000);
            animFadeIn.start();
//            animSetDetail.setDuration(2000);
//            animSetDetail.start();
            Log.d(TAG, "Showing Input Box");
        }
    };

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
        ObjectAnimator animFadeInDetail = ObjectAnimator.ofFloat(viewToAdd, "alpha", 0f, 1f);


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
//            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

}
