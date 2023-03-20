package com.example.recovermessages.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.recovermessages.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import timber.log.Timber;

public class SaveFiles {
    public static String getWhatsappFolder() {
        if (new File(Environment.getExternalStorageDirectory() + File.separator + "Android/media/com.whatsapp/WhatsApp" + File.separator + "Media").isDirectory()) {
            return "Android/media/com.whatsapp/WhatsApp/Media/";
        } else {
            return "WhatsApp/Media/";
        }
    }


    public static String WhatsappPath = getWhatsappFolder();
    public static String IMAGE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Images").getAbsolutePath();
    public static String AUDIO = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Audio").getAbsolutePath();
    public static String VOICENOTE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Voice Notes").getAbsolutePath();
    public static String IMAGE_PRIVATE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Images/Private").getAbsolutePath();
    public static String VIDEO_PRIVATE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Video/Private").getAbsolutePath();
    public static String DOCS_PRIVATE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Documents/Private").getAbsolutePath();
    public static String AUDIO_PRIVATE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Audio/Private").getAbsolutePath();
    public static String VOICENOTES_PRIVATE = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Voice Notes/Private").getAbsolutePath();
    public static String VIDEO = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Video").getAbsolutePath();
    public static String DOCUMENT = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Documents").getAbsolutePath();
    public static String GIF = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), WhatsappPath + "WhatsApp Animated Gifs").getAbsolutePath();
    public static String[] ArrayFiles = new String[]{IMAGE, IMAGE_PRIVATE, VIDEO, GIF, DOCUMENT, AUDIO, VIDEO_PRIVATE, DOCS_PRIVATE, AUDIO_PRIVATE, VOICENOTE, VOICENOTES_PRIVATE};

    @SuppressLint("StaticFieldLeak")
    public void save(final String s, final Context context, String type, boolean isPrivate) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                String newFileName;
                try {

                    Timber.d("Value of type received string s is %s", type);

                    Timber.d(" inside length >0 Value of received string s is %s", s);

                    if (s.endsWith(".lock")) {
                        String s0 = s.substring(1);
                        int i = s0.indexOf(".lock");
                        newFileName = s0.substring(0, i);
                    } else {
                        newFileName = s;
                    }


                    String filePath = "";
                    if (isPrivate) {
                        if (newFileName.endsWith(".jpg") || newFileName.endsWith(".png") || newFileName.endsWith(".jpeg")) {
                            filePath = ArrayFiles[1];
                            Timber.d("Image conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".mp4") || newFileName.endsWith(".3gp")) {
                            filePath = ArrayFiles[6];
                            Timber.d("Video conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".doc") ||
                                newFileName.endsWith(".docx") ||
                                newFileName.endsWith(".txt") ||
                                newFileName.endsWith(".ppt") || newFileName.endsWith(".pptx")
                                || newFileName.endsWith(".xls") || newFileName.endsWith(".xlsx")
                                || newFileName.endsWith(".csv") || newFileName.endsWith(".pdf")
                                || newFileName.endsWith(".zip") || newFileName.endsWith(".rar")) {

                            filePath = ArrayFiles[7];
                            Timber.d("DOC conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".mp3") || newFileName.endsWith(".wav")
                                || newFileName.endsWith(".amr") || newFileName.endsWith(".wma")) {
                            filePath = ArrayFiles[8];
                            Timber.d("Audio conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".opus")) {
                            filePath = ArrayFiles[10];
                            Timber.d("VOICE NOTE conditoin path is %s", filePath);
                        }


                    } else {
                        if (newFileName.endsWith(".jpg") || newFileName.endsWith(".png") || newFileName.endsWith(".jpeg")) {
                            filePath = ArrayFiles[0];
                            Timber.d("Image conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".mp4") || newFileName.endsWith(".3gp")) {
                            filePath = ArrayFiles[2];
                            Timber.d("Video conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".doc") || newFileName.endsWith(".docx") || newFileName.endsWith(".txt") ||
                                newFileName.endsWith(".ppt") || newFileName.endsWith(".pptx")
                                || newFileName.endsWith(".xls") || newFileName.endsWith(".xlsx")
                                || newFileName.endsWith(".csv") || newFileName.endsWith(".pdf")
                                || newFileName.endsWith(".zip") || newFileName.endsWith(".rar")) {
                            filePath = ArrayFiles[4];
                            Timber.d("DOC conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".gif")) {
                            filePath = ArrayFiles[3];
                            Timber.d("GIF conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".mp3") || newFileName.endsWith(".wav")
                                || newFileName.endsWith(".amr") || newFileName.endsWith(".wma")) {
                            filePath = ArrayFiles[5];
                            Timber.d("Audio conditoin path is %s", filePath);
                        } else if (newFileName.endsWith(".opus")) {
                            filePath = ArrayFiles[9];
                            Timber.d("VOICE NOTE conditoin path is %s", filePath);
                        }

                    }

                    Timber.d("type is  " + newFileName + " file path is " + filePath);

                    File externalStorageDirectory = Environment.getExternalStorageDirectory();
                    File file = new File(filePath + "/" + newFileName);

                    Timber.d("file path is %s", file.getAbsolutePath());

                    if (file.exists()) {
                        File file1 = new File(externalStorageDirectory, context.getResources().getString(R.string.app_name) + "/.Cached Files");

                        if (!file1.exists()) {
                            file1.mkdirs();
                        }
                        File file2 = new File(file1.getAbsolutePath() + "/" + newFileName + ".cached");
                        Timber.d("cahced file path is %s", file2.getAbsolutePath());

                        if (!file2.exists()) {
                            InputStream fileInputStream = new FileInputStream(file);
                            OutputStream fileOutputStream = new FileOutputStream(file2);
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int read = fileInputStream.read(bArr);
                                if (read <= 0) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                            }
                            fileInputStream.close();
                            fileOutputStream.close();
                        }
                    } else {
                        Timber.d("wa file not exists");
                    }
                } catch (Exception e) {
                    Timber.d("copy error: %s", e.toString());
                }
                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void move(final String str, final Context context) {
        new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                int i = 0;
                boolean exists;
                try {
                    File externalStorageDirectory = Environment.getExternalStorageDirectory();
                    File file = new File(externalStorageDirectory, (context.getResources().getString(R.string.app_name) + "/.Cached Files/") + str + ".cached");
                    exists = file.exists();
                    if (exists) {
                        try {
                            File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getResources().getString(R.string.app_name) + "/" + str);
                            InputStream fileInputStream = new FileInputStream(file);
                            OutputStream fileOutputStream = new FileOutputStream(file2);
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int read = fileInputStream.read(bArr);
                                if (read <= 0) {
                                    break;
                                }
                                fileOutputStream.write(bArr, 0, read);
                            }
                            fileInputStream.close();
                            fileOutputStream.close();
                            file.delete();
                        } catch (Exception e2) {
                            Timber.d("error moving- %s", e2.toString());
                        }
                        return exists ? 1 : 0;
                    }
                    return exists ? 1 : 0;
                } catch (Exception e3) {
                    exists = false;

                    Timber.d("error moving- %s", e3.toString());
                    return exists ? 1 : 0;
                }
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                try {
                    if (integer == 1) {
                        new SendNotification().sendBackground(context, "Deleted File Found", "Tap to check deleted message now.");
                    }
                    final Intent intent = new Intent(context.getString(R.string.files));
                    intent.putExtra(context.getString(R.string.files), context.getString(R.string.refresh_files));
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}
