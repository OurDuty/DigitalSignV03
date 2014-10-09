package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.VideoView;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputActivity extends Activity implements OnTouchListener {
    TextView tv;                //TextView, which will have a TouchListener
    InputActivity t;            //this
    float x;                    //X-coordinate of touch
    float y;                    //Y-coordinate of touch
    String sDown;               //String, which will be shown on the event "Touch down"
    String sMove;               //String, which will be shown on the event "Touch move"
    String sUp;                 //String, which will be shown on the event "Touch up"
    String id = "0";           //id of the current task ('0' means free mode)
    String videoPath = "";
    int counter = 0;            //Counter to understand when we should save our data
    boolean mark = true;        //Mark which helps us to save coordinates every 1 ms
    VideoView video;            //This video will be shown in the message box
    String outstring = "";      //Our data string
    RelativeLayout relative;      //Supporting layout
    SharedPreferences prefs;    //Object which will help us to get options
    SQLiteDatabase db;
    ContentValues cv;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Values init
        cv = new ContentValues();
        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tv = new TextView(this);
        relative = new RelativeLayout(this);
        t = this;

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        relative.addView(tv, lp);

        //Getting current id of the task
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                id = extras.getString("id");
            }
            if (extras.containsKey("path")) {
                videoPath = extras.getString("path");
            }
        }

        //if it is the task mode
        if (id != "0") {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.popup);
            dialog.setTitle("Task id=" + id);

            //set the custom dialog components
            final TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText("Time left: 25");

            //creating video object
            video = (VideoView) dialog.findViewById(R.id.video);
            String viewSource = videoPath;
            Log.d("myLog", viewSource);
            video.setVideoURI(Uri.parse(viewSource));
            video.setMediaController(new MediaController(this));
            video.requestFocus(0);
            video.setOnCompletionListener(myVideoViewCompletionListener);
            video.setZOrderOnTop(true);

            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);

            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv.setText("Ready!");
                    tv.setOnTouchListener(t);
                    dialog.dismiss();
                }
            });

            //starting message box
            dialog.show();
            video.start();
            tv.setText(videoPath);

            //time before the message box will close
            new CountDownTimer(25000, 1000) {
                public void onTick(long millisUntilFinished) {
                    text.setText("Time left: " + millisUntilFinished / 1000);
                }
                public void onFinish() {
                    tv.setText("Ready!");
                    tv.setOnTouchListener(t);
                    dialog.dismiss();
                }
            }.start();
        } else { //If it is the free mode
            tv.setText("Ready!");
            tv.setOnTouchListener(t);
        }
        setContentView(relative);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Cursor c = db.query("statistics", null, null, null, null, null, null);
        int numOfSavedSamplesTmp = 0;
        do {
            numOfSavedSamplesTmp++;
            // переход на следующую строку
            // а если следующей нет (текущая - последняя), то false - выходим из цикла
        } while (c.moveToNext());
        final int numOfSavedSamples = numOfSavedSamplesTmp;
        //Getting x&y coordinates
        x = event.getX();
        y = event.getY();

        //Creating image for animation
        final ImageView circle = new ImageView(this);
        circle.setImageResource(R.drawable.light_blue_circle);
        circle.setX(x);
        circle.setY(y);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
        circle.setLayoutParams(layoutParams);

        //Getting animationPref state
        boolean animation = prefs.getBoolean("animationPref", true);

        //If(animationPref) then enable animation
        if(animation) {
            AlphaAnimation alpha = new AlphaAnimation(1.0F, 0.0F);
            alpha.setDuration(2000); // Make animation instant
            alpha.setFillAfter(true); // Tell it to persist after the animation ends
            circle.startAnimation(alpha);
            relative.addView(circle);
        }

        //Process touches
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: //Touch down
                counter++;
                sDown = "Down: " + x + ", " + y;
                sMove = ""; sUp = "";
            case MotionEvent.ACTION_MOVE: //Touch moves
                sMove = "Move: " + x + ", " + y;
                if(mark){
                    outstring += (int)x + ", " + (int)y + ";\n";

                    //Disable saving coordinates for next 1 ms
                    mark = false;
                    new CountDownTimer(1, 1) {
                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            mark = true;
                        }
                    }.start();
                }
                tv.setText(sDown + "\n" + sMove + "\n" + sUp);
                break;
            case MotionEvent.ACTION_UP: //Touch ended
                //Getting touchesEndedPref
                outstring += "-1, -1;\n";
                String touchesEndedTimer = prefs.getString("touchesEndedPref", "0.7");
                int timer = Integer.valueOf(touchesEndedTimer);
                new CountDownTimer(timer, timer) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                        counter--;
                        if(counter <= 0){
                            counter = 0;
                            Log.d("myLog", outstring);
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            cv.put("date", dateFormat.format(date));
                            cv.put("type", Integer.valueOf(id));
                            cv.put("stats", outstring);
                            db.insert("statistics", null, cv);
                            tv.setText("Saved!\nNum of saved samples:" + numOfSavedSamples);
                            outstring = "";

                            if(id != "0") {
                                ContentValues args = new ContentValues();
                                args.put("id", id);
                                args.put("name", id + ". COMPLETED on " + date);
                                args.put("videoPath", videoPath);

                                db.update("tasks", args, "id=" + id, null);
                                Intent intent = new Intent();
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }
                    }
                }.start();

                break;
            case MotionEvent.ACTION_CANCEL:
                outstring = "";
                tv.setText("Ready!");
                break;
        }
        return true;
    }

    //Support method to restart video when it is completed
    MediaPlayer.OnCompletionListener myVideoViewCompletionListener
        = new MediaPlayer.OnCompletionListener(){
    @Override
    public void onCompletion(MediaPlayer arg0) {
        video.start();
    }};
}