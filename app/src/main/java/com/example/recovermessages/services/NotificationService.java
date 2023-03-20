package com.example.recovermessages.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.recovermessages.MyApplication;
import com.example.recovermessages.R;
import com.example.recovermessages.ui.home.HomeActivity;

import timber.log.Timber;

public class NotificationService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Timber.d("NotificationService onStartCommand");
        startForeground(1, new NotificationCompat.Builder(this, MyApplication.channelId)
                .setContentTitle("Service Running")
                .setContentText("Waiting for a deleted messege")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(PendingIntent.getActivity(this, 1,
                        new Intent(this, HomeActivity.class), 0)).build());
        return START_NOT_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
    }
}
