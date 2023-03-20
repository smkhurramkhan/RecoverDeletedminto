package com.example.recovermessages.services;

import android.app.Service;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;

public class StartGetting extends Service {
    FileObserver fileObserver;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        return super.onStartCommand(intent, i, i2);
    }

    public void onCreate() {
        super.onCreate();
        FileObserver fileObserver = this.fileObserver;
        if (fileObserver != null) {
            fileObserver.stopWatching();
        }
    }
}
