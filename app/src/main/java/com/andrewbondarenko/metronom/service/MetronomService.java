package com.andrewbondarenko.metronom.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import com.andrewbondarenko.metronom.domain.ProccessTact;
import com.andrewbondarenko.metronom.utils.PlayModeUtils;


public class MetronomService extends Service {

    private boolean proccess;
    private ProccessTact tact;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "Service created");
        proccess = true;
        tact = new ProccessTact(this);
    }

    private Thread vibrationThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (proccess) {

                    try {
                        synchronized (tact) {
                            tact.wait();
                        }
                        tact.proccessVibro();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private Thread flashThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (proccess) {

                    try {
                        synchronized (tact) {
                            tact.wait();
                        }
                        tact.proccesFlash();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                tact.cameraRealise();
            }
        });
    }

    private void gameOver() {
        proccess = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 1) {
            double tactBpm = Double.valueOf(intent.getStringExtra(PlayModeUtils.SPEED_BPM));
            Log.i("Tact speed", "tact - " + tactBpm);
            double result =   1000 / (tactBpm / 60);
            Log.i("Tact speed", "Result - " + result);
            final long interval = (long) result;
            vibrationThread().start();
            flashThread().start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (proccess && (PlayModeUtils.sound_pack || PlayModeUtils.flash_pack || PlayModeUtils.vibro_pack)) {

                        try {
                            tact.proccessAudio();
                            Thread.sleep(interval);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    synchronized (tact) {
                        tact.notifyAll();
                    }
                    stopSelf();

                }
            }).start();
        } else {
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameOver();
        Log.i("Service", "Service stopped");
    }



}
