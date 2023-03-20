package com.example.recovermessages.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.recovermessages.R;
import com.example.recovermessages.db.AppDatabase;
import com.example.recovermessages.services.MultiLanguageComparator;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class SaveMsg {
    private int del;

    static class savetoDb extends AsyncTask<String, Void, Long> {
        WeakReference<Context> contextWeakReference;
        int del;

        public savetoDb(WeakReference<Context> weakReference, int i) {
            this.contextWeakReference = weakReference;
            this.del = i;
        }

        protected Long doInBackground(String... strArr) {
            Long n = null;
            String s = strArr[2];
            String s2 = strArr[0];
            Long value;
            try {
                Timber.d("oldtitle: %s", s2);
                String s3 = strArr[1];
                Timber.d(s2 + " " + s3);
                String s4 = s3;
                Timber.d("testoldtitle: %s", s3);

                MultiLanguageComparator comparator = new MultiLanguageComparator();
                if (comparator.contain(s3, (contextWeakReference.get()).getString(R.string.thismsgwasdeleted))) {
                    s4 = "\ud83d\udc46 new deleted message detected\u26a0";
                }


                int contains = s2.contains("messages):") ? 1 : 0;

                String s5;
                if (contains != 0) {
                    s5 = s2.substring(0, s2.indexOf(" ("));
                    Timber.d("newtitle: %s" , s5);
                } else if (s2.contains(":")) {
                    s5 = s2.substring(contains, s2.indexOf(":"));
                } else {
                    s5 = s2;
                }

                value = n;
                if (!s5.equals("WhatsApp")) {
                    value = n;

                    if (!s4.contains("new messages") && !s4.contains("deleted")) {
                        String format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd", Locale.US);
                        String substring = format.substring(11, 13);
                        String substring2 = format.substring(14, 16);
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(simpleDateFormat.format(Calendar.getInstance().getTime()));
                        sb4.append(" ");
                        sb4.append(substring);
                        sb4.append(":");
                        sb4.append(substring2);
                        String string = sb4.toString();
                        AppDatabase AppDatabase = new AppDatabase(contextWeakReference.get());
                        value = n;
                        if (!AppDatabase.isPresent(s5, s4, s)) {
                            value = AppDatabase.addData(s5, s, s4, string, format);
                        }
                    } else if (s4.contains("deleted")) {
                        value = 1L;
                    }
                }
            } catch (Exception ex) {
                Timber.d("savemsg bg %s", ex.toString());
                value = n;
            }
            return value;
        }

        protected void onPostExecute(Long l) {
            super.onPostExecute(l);
            Timber.d("numadded: %s", String.valueOf(l));
            if (l != null && l > 0L) {
                try {
                    if (del == 1) {
                        new SendNotification().sendBackground(contextWeakReference.get(), (contextWeakReference.get()).getString(R.string.message_deleted), (contextWeakReference.get()).getString(R.string.message_was_deleted));
                    }
                    Intent intent = new Intent("refresh");
                    intent.putExtra("refresh", "refresh");
                    LocalBroadcastManager.getInstance(contextWeakReference.get()).sendBroadcast(intent);
                } catch (Exception ex) {
                    Timber.d("savemsg post%s", ex.toString());
                }
            }
        }
    }

    public SaveMsg(Context context, String str, String str2, String str3) {

        MultiLanguageComparator comparator = new MultiLanguageComparator();
        if (comparator.contain(str2, context.getString(R.string.thismsgwasdeleted))) {
            del = 1;
        } else {
            del = 0;
        }
        new savetoDb(new WeakReference(context), del).execute(str, str2, str3);
    }
}
