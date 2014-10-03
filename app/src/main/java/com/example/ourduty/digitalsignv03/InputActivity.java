package com.example.ourduty.digitalsignv03;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup.LayoutParams;
import android.view.View.OnClickListener;

public class InputActivity extends Activity implements OnTouchListener {
    TextView tv;
    float x;
    float y;
    String sDown;
    String sMove;
    String sUp;
    String id = "-1";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tv = new TextView(this);

        // You can be pretty confident that the intent will not be null here.
        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("id")) {
                id = extras.getString("id");
                // TODO: Do something with the value of isNew.
            }
        }

        final InputActivity t = this;
        setContentView(tv);

        if(id != "-1") {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.popup);
            dialog.setTitle("Task id=" + id);

            // set the custom dialog components - text, image and button
            final TextView text = (TextView) dialog.findViewById(R.id.text);
            text.setText("Time left: 5");
            final ImageView image = (ImageView) dialog.findViewById(R.id.image);
            image.setImageResource(R.drawable.logo);

            Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
            // if button is clicked, close the custom dialog
            dialogButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();

            //                      ANIMATION CountDownTimer
            /*new CountDownTimer(5000, 200) {
                public void onTick(long t){
                    //if((t/200) % 3 == 0) image.setImageResource(R.drawable.img_weel_1);
                    //if((t/200) % 3 == 1) image.setImageResource(R.drawable.img_weel_2);
                    //if((t/200) % 3 == 2) image.setImageResource(R.drawable.img_weel_3);
                };
                public void onFinish() {

                }
            }.start();*/

            new CountDownTimer(5000, 1000) {
                public void onTick(long millisUntilFinished) {
                    text.setText("Time left: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    tv.setText("Ready!");
                    dialog.dismiss();
                    tv.setOnTouchListener(t);
                }
            }.start();
        } else{
            tv.setText("Ready!");
            tv.setOnTouchListener(t);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        x = event.getX();
        y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // нажатие
                sDown = "Down: " + x + ", " + y;
                sMove = ""; sUp = "";
                break;
            case MotionEvent.ACTION_MOVE: // движение
                sMove = "Move: " + x + ", " + y;
                break;
            case MotionEvent.ACTION_UP: // отпускание
            case MotionEvent.ACTION_CANCEL:
                sMove = "";
                sUp = "Up: " + x + ", " + y;
                break;
        }
        tv.setText(sDown + "\n" + sMove + "\n" + sUp);
        return true;
    }
}