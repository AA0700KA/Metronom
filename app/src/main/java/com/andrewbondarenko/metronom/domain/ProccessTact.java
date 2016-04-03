package com.andrewbondarenko.metronom.domain;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.util.Log;

import com.andrewbondarenko.metronom.R;
import com.andrewbondarenko.metronom.activity.MainActivity;
import com.andrewbondarenko.metronom.service.MetronomService;

public class ProccessTact {

    private Camera mCamera;
    private Camera.Parameters mParams;
    private Context context;
    private Vibrator mVibrator;
    private MediaPlayer mediaPlayer;

    public static final String STATUS = "status";
    public static final String START = "start";
    public static final String FINISH = "finish";

    public ProccessTact(Context context) {
        this.context = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mediaPlayer = MediaPlayer.create(context, R.raw.metronom_sound_v2);
        mediaPlayer.setLooping(false);
        Log.i("Track Duration", String.valueOf(mediaPlayer.getDuration()));

        if (checkCameraHardware()) {
            mCamera = MainActivity.getCameraInstance();
            checkFlash();
        }
    }

    private void checkFlash() {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
        }
    }

    private boolean checkCameraHardware() {
        boolean result = false;
        PackageManager pm = context.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {

            result = true;
        }

        return result;
    }

    public void proccesFlash() throws InterruptedException {
        if (mCamera != null && MetronomService.flash_pack) {

            //Thread.currentThread().sleep(250);
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mParams);
            mCamera.startPreview();
            Thread.currentThread().sleep(50);
            mParams.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mParams);
            mCamera.startPreview();
        }
    }

    public void proccessVibro() throws InterruptedException {
       if (MetronomService.vibro_pack)
        mVibrator.vibrate(50);
    }

    public void cameraRealise() {
        Log.i("Relise", "Relise Camera");
        if (mCamera != null) {
            mCamera.release();
        }
    }

    public void proccessAudio() throws InterruptedException {
        synchronized (this) {
            notifyAll();
        }
        if (MetronomService.sound_pack) {
            mediaPlayer.start();
        }
    }

    public void proccessIndicator() throws InterruptedException {
        Intent intent = new Intent(MainActivity.BROADCAST_ACTION);

        intent.putExtra(STATUS, START);
        context.sendBroadcast(intent);

        Thread.sleep(50);

        intent.putExtra(STATUS, FINISH);
        context.sendBroadcast(intent);
    }
    public void releaseAudio() {
        Log.i("Relise", "Relise Audio");
        mediaPlayer.release();
    }

}
