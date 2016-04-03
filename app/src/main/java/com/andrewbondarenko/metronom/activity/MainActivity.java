package com.andrewbondarenko.metronom.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrewbondarenko.metronom.R;
import com.andrewbondarenko.metronom.domain.ProccessTact;
import com.andrewbondarenko.metronom.service.MetronomService;
import com.rey.material.widget.Slider;

public class MainActivity extends AppCompatActivity {

    private Slider bpmSlider;
    private EditText bpmEdit;
    private CardView btnStart;
    private ImageView vibroImg;
    private ImageView flashImg;
    private ImageView soundImg;
    private TextView manualMode;
    private TextView otherText;
    private TextView bpm;
    private TextView indicatorTxt;
    private TextView btnStartTxt;
    private ImageView indicator;
    private BroadcastReceiver br;


    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    private final String SPEED_BPM = "Time";

    private final int GREEN_INDICATOR = Color.parseColor("#36ff00");
    private final int WHITE_INDICATOR = Color.parseColor("#ffffff");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        vibroImg = (ImageView) findViewById(R.id.vibro_mode);
        if (MetronomService.vibro_pack) {
            Drawable drawable = getResources().getDrawable(R.drawable.vibration_on);
            vibroImg.setImageDrawable(drawable);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.vibration_off);
            vibroImg.setImageDrawable(drawable);

        }
        flashImg = (ImageView) findViewById(R.id.flash_mode);
        if (MetronomService.flash_pack) {
            Drawable drawable = getResources().getDrawable(R.drawable.flash_on);
            flashImg.setImageDrawable(drawable);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.flash_off);
            flashImg.setImageDrawable(drawable);

        }
        soundImg = (ImageView) findViewById(R.id.sound_mode);
        if (MetronomService.sound_pack) {
            Drawable drawable = getResources().getDrawable(R.drawable.sound_on);
            soundImg.setImageDrawable(drawable);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.sound_off);
            soundImg.setImageDrawable(drawable);

        }
        btnStart = (CardView) findViewById(R.id.card_view_start);
        bpmSlider = (Slider) findViewById(R.id.bpm_slider);
        bpmEdit = (EditText) findViewById(R.id.bpm_edit);
        manualMode = (TextView) findViewById(R.id.textView);
        otherText = (TextView) findViewById(R.id.other_text);
        bpm = (TextView) findViewById(R.id.bpm);
        indicatorTxt = (TextView) findViewById(R.id.indicator_txt);
        btnStartTxt = (TextView) findViewById(R.id.btn_start);

        if (MetronomService.statusButton != 0)
        btnStartTxt.setText(MetronomService.statusButton);

        indicator = (ImageView) findViewById(R.id.indicator);
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String tact = intent.getStringExtra(ProccessTact.STATUS);

                switch(tact) {
                    case ProccessTact.START: indicator.setBackgroundColor(GREEN_INDICATOR);
                        break;
                    case ProccessTact.FINISH: indicator.setBackgroundColor(WHITE_INDICATOR);
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(br, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        vibroImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MetronomService.vibro_pack) {
                    Drawable drawable = getResources().getDrawable(R.drawable.vibration_off);
                    vibroImg.setImageDrawable(drawable);
                    MetronomService.vibro_pack = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.vibration_on);
                    vibroImg.setImageDrawable(drawable);
                    MetronomService.vibro_pack = true;
                }
            }
        });
        flashImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MetronomService.flash_pack) {
                    Drawable drawable = getResources().getDrawable(R.drawable.flash_off);
                    flashImg.setImageDrawable(drawable);
                    MetronomService.flash_pack = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.flash_on);
                    flashImg.setImageDrawable(drawable);
                    MetronomService.flash_pack = true;
                }
            }
        });
        soundImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MetronomService.sound_pack) {
                    Drawable drawable = getResources().getDrawable(R.drawable.sound_off);
                    soundImg.setImageDrawable(drawable);
                    MetronomService.sound_pack = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.sound_on);
                    soundImg.setImageDrawable(drawable);
                    MetronomService.sound_pack = true;
                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MetronomService.sound_pack || MetronomService.flash_pack || MetronomService.vibro_pack) {
                    String result = bpmEdit.getText().toString();
                    Intent intent = new Intent(MainActivity.this, MetronomService.class).putExtra(SPEED_BPM, Double.valueOf(result));
                    if (Integer.parseInt(result) <= 200 && MetronomService.startId == 1) {
                        MetronomService.statusButton = R.string.stop;
                        btnStartTxt.setText(R.string.stop);
                        startService(intent);
                        MetronomService.startId++;

                    } else if (Integer.parseInt(result) > 200){
                        Toast.makeText(MainActivity.this, "Value may be less than 200 BPM", Toast.LENGTH_SHORT).show();
                    } else {
                        MetronomService.statusButton = R.string.start;
                        btnStartTxt.setText(R.string.start);
                        stopService(intent);
                        MetronomService.startId = 1;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "You must ON minimum one of three modes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bpmEdit.setText(String.valueOf(bpmSlider.getValue()));
        bpmEdit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/museosans_300.ttf"));
        bpmSlider.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                bpmEdit.setText(String.valueOf(newValue));
            }
        });

        manualMode.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/museosans_500.ttf"));
        otherText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/museosans_100.ttf"));
        bpm.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/museosans_300.ttf"));
        indicatorTxt.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/museosans_500.ttf"));
        btnStartTxt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/museosans_700.ttf"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    public static Camera getCameraInstance() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera c = null;

        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    c = Camera.open(i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return c;
    }

}
