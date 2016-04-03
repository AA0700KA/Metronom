package com.andrewbondarenko.metronom.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.andrewbondarenko.metronom.R;
import com.andrewbondarenko.metronom.activity.MainActivity;
import com.andrewbondarenko.metronom.domain.ProccessTact;


public class MetronomService extends Service {

    private boolean proccess;
    private ProccessTact tact;

    public static boolean vibro_pack = false;
    public static boolean flash_pack= false;
    public static boolean sound_pack = false;

    private final String SPEED_BPM = "Time";
    public static int startId = 1;
    public static int statusButton = R.string.start;

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

    private Thread activityIndicatorThread() {
        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (proccess) {
                        try {
                            synchronized (tact) {
                                tact.wait();
                            }
                            tact.proccessIndicator();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                }
            }
        });
    }

    private Thread mainIndicatorThread(double tactBpm) {

        double result =   1000 / (tactBpm / 60);
        final long interval = (long) result;

        return new Thread(new Runnable() {
            @Override
            public void run() {
                while (proccess) {

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


            }
        });
    }

    private void gameOver() {
        proccess = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            double tactBpm = intent.getDoubleExtra(SPEED_BPM, 0);

            vibrationThread().start();
            flashThread().start();
            activityIndicatorThread().start();
            mainIndicatorThread(tactBpm).start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gameOver();
        Log.i("Service", "Service stopped");
    }



}
