package cl.saratscheff.audiokiller;

        import android.Manifest;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Environment;
        import android.os.FileObserver;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.NotificationCompat;
        import android.support.v4.app.TaskStackBuilder;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.Gravity;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "--DEBUG--";
    public static FileObserver observer;
    public static String pathToWatch = Environment.getExternalStorageDirectory().getPath() + "/WhatsApp/Media/WhatsApp Voice Notes/201714/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "PATH BEING OBSERVED: " + pathToWatch);

        TextView tv = (TextView)findViewById(R.id.textview_filechanged);

        if (ask_for_permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            tv.setText("Looking for new audio files...");
            observer = new FileObserver(pathToWatch) { // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    //Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                    if (event == FileObserver.CREATE || event == FileObserver.MOVED_TO){

                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "New audio file found!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        Log.d(TAG, "File created: " + pathToWatch + file);
                        translate_audio(pathToWatch + file);
                    }
                }
            };
            observer.stopWatching();
            observer.startWatching(); //START OBSERVING
        } else {
            tv.setText("NOT WATCHING \n App couldn't get permission to read files");
        }

        // http://stackoverflow.com/questions/7265906/how-do-you-implement-a-fileobserver-from-an-android-service

        // TODO: Copiar recursive solution http://www.java2s.com/Open-Source/Android_Free_Code/Example/course/com_toraleap_collimator_utilRecursiveFileObserver_java.htm

        // TODO: CUSTOM SOLUTION, One fileobserver for new folders, one fileobserver for the latest folder (The only one receiving new audios)

    }

    public void create_sample_audio_file(View view) {
        String file_path = pathToWatch + "PTT-20170408-WA1111.opus";
        File f0 = new File(file_path);
        boolean d0 = f0.delete();
        Log.d(TAG, "File deleted: " + file_path + d0);

        try {
            InputStream in = getResources().openRawResource(R.raw.sampleaudioen);
            FileOutputStream out = new FileOutputStream(pathToWatch + "PTT-20170408-WA1111.opus");
            byte[] buff = new byte[1024];
            int read = 0;
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch(IOException e) {
            Log.e(TAG, "ERROR 001! " + e.getMessage());
        }
    }

    public void show_notification(String transcriptResult) {

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

    public void translate_audio(String new_audio_path) {
        if (ask_for_permission(Manifest.permission.INTERNET)) {
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
                    show_notification(httpResult);
                    Log.d(TAG, httpResult);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, httpResult, Toast.LENGTH_SHORT).show();
                        }
                    });

                    //---------------------------------------------------------------------------------------------
                } catch(IOException e) {
                    Log.e(TAG, "ERROR 3! " + e.getMessage());
                    //Toast.makeText(getBaseContext(), "ERROR! " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).start();
        } else {
            Log.e(TAG, "ERROR 4! " + "App couldn't get permission to use internet");
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(getBaseContext(), "ERROR! \n App couldn't get permission to use internet", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean ask_for_permission(String permission_type) {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, permission_type);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{permission_type}, 1);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Couldn't get necessary permissions!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Log.d(TAG, "Permission granted: " + (permissionCheck == PackageManager.PERMISSION_GRANTED));
        return true;
    }
}
