package com.andrewbondarenko.metronom.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.andrewbondarenko.metronom.service.MetronomService;
import com.andrewbondarenko.metronom.utils.PlayModeUtils;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        vibroImg = (ImageView) findViewById(R.id.vibro_mode);
        if (PlayModeUtils.vibro_pack) {
            Drawable drawable = getResources().getDrawable(R.drawable.vibration_on);
            vibroImg.setImageDrawable(drawable);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.vibration_off);
            vibroImg.setImageDrawable(drawable);

        }
        flashImg = (ImageView) findViewById(R.id.flash_mode);
        if (PlayModeUtils.flash_pack) {
            Drawable drawable = getResources().getDrawable(R.drawable.flash_on);
            flashImg.setImageDrawable(drawable);

        } else {
            Drawable drawable = getResources().getDrawable(R.drawable.flash_off);
            flashImg.setImageDrawable(drawable);

        }
        soundImg = (ImageView) findViewById(R.id.sound_mode);
        if (PlayModeUtils.sound_pack) {
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        vibroImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayModeUtils.vibro_pack) {
                    Drawable drawable = getResources().getDrawable(R.drawable.vibration_off);
                    vibroImg.setImageDrawable(drawable);
                    PlayModeUtils.vibro_pack = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.vibration_on);
                    vibroImg.setImageDrawable(drawable);
                    PlayModeUtils.vibro_pack = true;
                }
            }
        });
        flashImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayModeUtils.flash_pack) {
                    Drawable drawable = getResources().getDrawable(R.drawable.flash_off);
                    flashImg.setImageDrawable(drawable);
                    PlayModeUtils.flash_pack = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.flash_on);
                    flashImg.setImageDrawable(drawable);
                    PlayModeUtils.flash_pack = true;
                }
            }
        });
        soundImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayModeUtils.sound_pack) {
                    Drawable drawable = getResources().getDrawable(R.drawable.sound_off);
                    soundImg.setImageDrawable(drawable);
                    PlayModeUtils.sound_pack = false;
                } else {
                    Drawable drawable = getResources().getDrawable(R.drawable.sound_on);
                    soundImg.setImageDrawable(drawable);
                    PlayModeUtils.sound_pack = true;
                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlayModeUtils.sound_pack || PlayModeUtils.flash_pack || PlayModeUtils.vibro_pack) {
                    String result = bpmEdit.getText().toString();
                    if (Integer.parseInt(result) <= 200) {
                        startService(new Intent(MainActivity.this, MetronomService.class).putExtra(PlayModeUtils.SPEED_BPM, result));
                    } else {
                        Toast.makeText(MainActivity.this, "Value may be less than 200 BPM", Toast.LENGTH_SHORT).show();
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
        btnStartTxt.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/museosans_700.ttf"));
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
