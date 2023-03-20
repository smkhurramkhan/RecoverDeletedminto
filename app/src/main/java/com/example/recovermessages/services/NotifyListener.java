package com.example.recovermessages.services;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.recovermessages.R;
import com.example.recovermessages.db.AppDatabase;
import com.example.recovermessages.utils.SaveFiles;
import com.example.recovermessages.utils.SaveMsg;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import timber.log.Timber;

public class NotifyListener extends NotificationListenerService {
    private static observer observer;
    BroadcastReceiver broadcastReceiver;
    Context context;
    boolean onserving = false;
    private ArrayList<String> packs;
    private PrivateObserver privateObserver;
    private String mType = "";

    public NotifyListener() {
        this.onserving = false;
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Timber.d("received");
                    final String stringExtra = intent.getStringExtra(context.getString(R.string.noti_obserb));
                    final boolean equals = stringExtra.equals("true");

                    Timber.d("Notify Listener onReceive %s", stringExtra);
                    if (equals) {
                        if (!onserving) {
                            Timber.d("NotifyListener COnstructor mTYPE at begining is %s", mType);
                            startOnServing(mType);
                            onserving = equals;
                        }
                    } else if (stringExtra.equals("update")) {
                        updateList();
                    } else {
                        onserving = equals;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
    }

    private class observer extends FileObserver {
        @SuppressLint("WrongConstant")
        public observer(String str) {
            super(str, 4095);
            Timber.d("start");
        }

        public void onEvent(int i, String str) {
            if (i == 256 || (i == 128 && !str.equals(".probe"))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("create File path--> ");
                stringBuilder.append(str);
                Timber.d(stringBuilder.toString());
                try {
                    new SaveFiles().save(str, context, mType, false);
                } catch (Exception e) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("create error: ");
                    stringBuilder2.append(e.toString());
                    Timber.d(stringBuilder2.toString());
                }
            }
            if ((i & 512) != 0 || (i & 1024) != 0) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("khurram--> ");
                sb3.append(str);
                Timber.d(sb3.toString());
                try {
                    new SaveFiles().move(str, context);
                } catch (Exception i2) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("del error: ");
                    sb4.append(i2.toString());
                    Timber.d(sb4.toString());
                }
            }
        }
    }

    private class PrivateObserver extends FileObserver {
        @SuppressLint("WrongConstant")
        public PrivateObserver(String str) {
            super(str, 4095);
        }

        public void onEvent(int i, String str) {
            String str2 = "khurram--->";
            if (i == 256 || (i == 128 && !str.equals(".probe"))) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("onEvent create File path--> ");
                stringBuilder.append(str);
                Timber.d(stringBuilder.toString());
                try {
                    new SaveFiles().save(str, context, mType, true);
                } catch (Exception e) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Exception create error: ");
                    stringBuilder2.append(e.toString());
                    Timber.d(stringBuilder2.toString());
                }
            }
            if ((i & 512) != 0 || (i & 1024) != 0) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Exceptiondlete File path--> ");
                sb3.append(str);
                Timber.d(sb3.toString());
                try {
                    new SaveFiles().move(str, context);
                } catch (Exception i2) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Exception del error: ");
                    sb4.append(i2.toString());
                    Timber.d(sb4.toString());
                }
            }
        }
    }

    public IBinder onBind(Intent intent) {
        Timber.d("bind");
        return super.onBind(intent);
    }

    public boolean onUnbind(Intent intent) {
        Timber.d("unb ");
        return super.onUnbind(intent);
    }

    public void onCreate() {
        super.onCreate();
        this.context = getApplicationContext();
        isNotificationServiceRunning();
        this.packs = new ArrayList();
        updateList();

        LocalBroadcastManager.getInstance(this.context).registerReceiver(this.broadcastReceiver, new IntentFilter(this.context.getString(R.string.noti_obserb)));
    }

    public void onListenerConnected() {
        super.onListenerConnected();
        Timber.d("on connect");
    }

    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Timber.d("on dis connect");
    }

    public int onStartCommand(Intent intent, int i, int i2) {
        Timber.d("on cresate");
        tryReconnectService();
        return START_STICKY;
    }


    public File getWhatsappFolder(String type) {
        switch (type) {
            case "video":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Video").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Video");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Video");
                }

            case "document":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Documents").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Documents");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Documents");
                }
            case "gif":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Gifs").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Gifs");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Gifs");
                }
            case "audio":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Audio").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Audio");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Audio");
                }
            case "voice": {
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Voice Notes").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Voice Notes");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Voice Notes");
                }
            }
            default:  //image
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Images").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Images");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Images");
                }
        }


    }


    public File getPrivateWhatsappFolder(String type) {
        switch (type) {
            case "video":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Video/Private").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Video/Private");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Video/Private");
                }

            case "document":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Documents/Private").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Documents/Private");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Documents/Private");
                }
            case "gif":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp /Private").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Gifs/Private");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Gifs/Private");
                }
            case "audio":
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Audio/Private").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Audio/Private");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Audio/Private");
                }
            case "voice": {
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Voice Notes/Private").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Voice Notes/Private");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Voice Notes/Private");
                }
            }
            default:  //image
                if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Images/Private").isDirectory()) {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Images/Private");
                } else {
                    return new File(Environment.getExternalStorageDirectory() + File.separator + "WhatsApp" + File.separator + "Media" + File.separator + "WhatsApp Images/Private");
                }
        }
    }

    private void startOnServing(String mType) {
        Timber.d("onstartserving %s", mType);
        File file = getWhatsappFolder(mType);
        observer view_deleted_messages_Noti_observer = observer;
        if (view_deleted_messages_Noti_observer != null) {
            view_deleted_messages_Noti_observer.stopWatching();
        }
        observer = new observer(file.getPath());

        Timber.d("startonserving%s", file.getAbsolutePath());


        observer.startWatching();

        if (privateObserver != null) privateObserver.stopWatching();
        privateObserver = new PrivateObserver(getPrivateWhatsappFolder(mType).getPath());
        privateObserver.startWatching();
    }

    public void onNotificationPosted(StatusBarNotification statusBarNotification) {
        super.onNotificationPosted(statusBarNotification);
        Timber.d("on posted");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("");
        stringBuilder.append(this.packs.size());
        Timber.d(stringBuilder.toString());
        for (String pack : this.packs) {
            Timber.d(pack);
        }
        try {
            String packageName = statusBarNotification.getPackageName();
            if (this.packs.contains(packageName)) {
                Bundle extras = statusBarNotification.getNotification().extras;
                String string = extras.getString(NotificationCompat.EXTRA_TITLE);
                String string1 = extras.getCharSequence(NotificationCompat.EXTRA_TEXT).toString();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("on posted pack: ");
                stringBuilder2.append(string1);
                Timber.d(stringBuilder2.toString());

                if ((!Objects.equals(string, "WhatsApp") &&
                        !Objects.equals(string, "WhatsApp Web") &&
                        !string.contains("@") &&
                        !string.equals("Backup in progress") &&
                        !string.equals("Finished backup")) &&
                        !string1.contains("WhatsApp") &&
                        !string1.contains("new messages") &&
                        !string.contains("missed voice call") &&
                        !string.contains("missed video call") &&
                        !string.contains("You have new messages") &&
                        !string.contains("new messages") &&
                        !string.contains("messages from") &&
                        !string1.contains("Backup paused") &&
                        !string1.contains("Missed") &&
                        !string1.contains("missed calls") &&
                        !string1.contains("Deleting") &&
                        !string1.contains("Backup in progress") &&
                        !string1.contains("Finished backup") &&
                        !string1.contains("Couldn't complete")
                ) {
                    if (string1.contains("\uD83D\uDCF7") || string1.contains("image")) { // Image
                        mType = "image";
                    } else if (string1.contains("\uD83C\uDFA5") || string1.contains("Video")) {// Video
                        mType = "video";
                    } else if (string1.contains("\uD83C\uDFA4")) {//  Voice
                        mType = "voice";
                    } else if (string1.contains("\uD83D\uDCC4") || string1.endsWith(".doc") ||
                            string1.endsWith(".docx") ||
                            string1.endsWith(".txt") ||
                            string1.endsWith(".ppt") || string1.endsWith(".pptx")
                            || string1.endsWith(".xls") || string1.endsWith(".xlsx")
                            || string1.endsWith(".csv") || string1.endsWith(".pdf")
                            || string1.endsWith(".zip") || string1.endsWith(".rar")
                    ) { // Document
                        mType = "document";
                    } else if (string1.contains("\uD83C\uDFB5") || string1.contains("Audio")) {//  Audio
                        mType = "audio";
                    } else {
                        mType = "image";
                    }
                }

                if (VERSION.SDK_INT < 23) {
                    startOnServing(mType);
                } else if (check()) {
                    startOnServing(mType);
                }

                Timber.d("string1 of notification on posted is %s", string);
                Timber.d("Text of notification on posted is %s", string1);


                new SaveMsg(getApplicationContext(), string, string1, packageName);
                //  new MessageSaver().saveToDB(getApplicationContext(), string, string1, packageName);
            }
        } catch (Exception statusBarNotification2) {
            StringBuilder stringBuilder1 = new StringBuilder();
            stringBuilder1.append("error: ");
            stringBuilder1.append(statusBarNotification2.toString());
            Timber.d(stringBuilder1.toString());
        }
    }

    public void onNotificationRemoved(StatusBarNotification statusBarNotification) {
        super.onNotificationRemoved(statusBarNotification);
        Timber.d("on removed");
    }

    public void onDestroy() {
        super.onDestroy();
        Timber.d("on destroy");
    }

    public void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        Timber.d("on task removed");
    }

    @RequiresApi(api = 23)
    private boolean check() {
        return this.context.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("StaticFieldLeak")
    private void updateList() {
        this.packs.clear();
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... voidArr) {
                packs = new AppDatabase(context).getAllPackages();
                return null;
            }
        }.execute(new Void[0]);
    }

    public void tryReconnectService() {
        toggleNotificationListenerService();
        if (VERSION.SDK_INT >= 24) {
            requestRebind(new ComponentName(getApplicationContext(), NotifyListener.class));
        }
    }

    private void toggleNotificationListenerService() {
        PackageManager packageManager = getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(this, NotifyListener.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(new ComponentName(this, NotifyListener.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public boolean isNotificationServiceRunning() {
        String string = Secure.getString(this.context.getContentResolver(),
                "enabled_notification_listeners");
        CharSequence packageName = this.context.getPackageName();
        if (string != null) {
            boolean contains = string.contains(packageName);
            if (contains) {
                return contains;
            }
        }
        return false;
    }
}
