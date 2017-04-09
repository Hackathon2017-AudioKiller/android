package cl.saratscheff.audiokiller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class FilesObservingService extends Service {
    public static String TAG;
    public static FileObserver whatsappVoiceObserver;
    public static FileObserver whatsappAudioObserver;
    public static FileObserver whatsappVideoObserver;
    public static FileObserver telegramAudioObserver;
    public static FileObserver telegramVideoObserver;
    public static String whatsappVoicePath;
    public static String whatsappAudioPath;
    public static String whatsappVideoPath;
    public static String telegramAudioPath;
    public static String telegramVideoPath;
    

    public FilesObservingService() {

    }
    public void onCreate() {
        TAG = getString(R.string.tag);
        // Set paths
        whatsappVoicePath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.whatsapp_voice_path);
        whatsappAudioPath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.whatsapp_audio_path);
        whatsappVideoPath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.whatsapp_video_path);
        telegramAudioPath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.telegram_audio_path);
        telegramVideoPath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.telegram_video_path);

        /*
        // http://stackoverflow.com/questions/7265906/how-do-you-implement-a-fileobserver-from-an-android-service

        // TODO: Copiar recursive solution http://www.java2s.com/Open-Source/Android_Free_Code/Example/course/com_toraleap_collimator_utilRecursiveFileObserver_java.htm

        // TODO: CUSTOM SOLUTION, One fileobserver for new folders, one fileobserver for the latest folder (The only one receiving new audios)
        */
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags,  int startId) {

        int res = super.onStartCommand(intent, flags, startId);

        start_observer(whatsappVoiceObserver, whatsappVoicePath, "audio");
        start_observer(whatsappAudioObserver, whatsappAudioPath, "audio");
        start_observer(whatsappVideoObserver, whatsappVideoPath, "video");
        start_observer(telegramAudioObserver, telegramAudioPath, "audio");
        start_observer(telegramVideoObserver, telegramVideoPath, "video");
        Log.d(TAG, "####### FILE-OBSERVERS STARTED! #######");

        startServiceForeground(intent, flags, startId);

        return Service.START_STICKY;
    }

    public int startServiceForeground(Intent intent, int flags, int startId) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("File Observer Service")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();

        startForeground(300, notification);

        return START_STICKY;
    }

    public void show_audio_notification(String transcriptResult) {

        // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.cherry)
                        .setContentTitle("New Audio found!")
                        .setContentText("Click here to see transcript");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TranscriptActivity.class);
        try {
            JSONObject transcriptResultJson = new JSONObject(transcriptResult);
            Intent t_int = new Intent("newAudioTranscript");
            t_int.putExtra("transcript", transcriptResultJson.getString("result"));
            getApplicationContext().sendBroadcast(t_int);
            resultIntent.putExtra("transcript", transcriptResultJson.getString("result"));
            resultIntent.putExtra("status", transcriptResultJson.getString("status"));
        } catch(JSONException e) {
            Log.e(TAG, "ERROR 002! " + e.getMessage());
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void show_video_notification(String transcriptResult) {

        // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.cherry)
                        .setContentTitle("New Audio found!")
                        .setContentText("Click here to see transcript");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TranscriptActivity.class);
        try {
            JSONObject transcriptResultJson = new JSONObject(transcriptResult);
            resultIntent.putExtra("url", transcriptResultJson.getString("result"));
        } catch(JSONException e) {
            Log.e(TAG, "ERROR 002! " + e.getMessage());
        }

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void translate_audio(String new_audio_path) {
        // http://stackoverflow.com/questions/11766878/sending-files-using-post-with-httpurlconnection
        new Thread(() -> {
            try {
                //---------------------------------------------------------------------------------------------

                String charset = "UTF-8";
                String requestURL = getString(R.string.server_url) + "api/textify/";

                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                //multipart.addFormField("param_name_1", "param_value");
                multipart.addFilePart("audio", new File(new_audio_path));
                String httpResult = multipart.finish(); // response from server.
                show_audio_notification(httpResult);
                Log.d(TAG, httpResult);

                //---------------------------------------------------------------------------------------------
            } catch(IOException e) {
                Log.e(TAG, "ERROR 3! " + e.getMessage());
                //Toast.makeText(getBaseContext(), "ERROR! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    public void translate_video(String new_video_path) {
        // http://stackoverflow.com/questions/11766878/sending-files-using-post-with-httpurlconnection
        new Thread(() -> {
            try {
                //---------------------------------------------------------------------------------------------

                String charset = "UTF-8";
                String requestURL = getString(R.string.server_url) + "api/subtitlify/";

                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addFilePart("video", new File(new_video_path));
                String httpResult = multipart.finish(); // response from server.
                show_video_notification(httpResult);
                Log.d(TAG, httpResult);

                //---------------------------------------------------------------------------------------------
            } catch(IOException e) {
                Log.e(TAG, "ERROR 3! " + e.getMessage());
                //Toast.makeText(getBaseContext(), "ERROR! " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }).start();
    }

    public void start_observer(FileObserver obs, String path_to_observe, String type) {
        obs = new FileObserver(path_to_observe) { // set up a file whatsappVoiceObserver to watch this directory on sd card
            @Override
            public void onEvent(int event, String file) {
                //Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                if (event == FileObserver.CREATE || event == FileObserver.MOVED_TO){

                    toast_that("New audio file found!");
                    Log.d(TAG, "File created: " + path_to_observe + file);

                    if (type == "audio") {
                        translate_audio(path_to_observe + file);
                    } else if (type == "video"){
                        translate_video(path_to_observe + file);
                    } else {
                        Log.e(TAG, "ERROR! Unsupported type");
                    }
                }
            }
        };
        //obs.stopWatching();
        obs.startWatching(); //START OBSERVING
    }

    private void toast_that(String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),
                        msg,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
