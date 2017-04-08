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
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "--DEBUG--";
    public static FileObserver observer;
    public static String pathToWatch = Environment.getExternalStorageDirectory().getPath() + "/WhatsApp/Media/WhatsApp Voice Notes/201714/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "PATH BEING OBSERVED: [" + pathToWatch + "]");

        //setContentView(R.layout.yourlayout): ???
        TextView tv = (TextView)findViewById(R.id.textview_filechanged);

        if (ask_for_permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            tv.setText("Looking for new audio files...");
            observer = new FileObserver(pathToWatch) { // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    //Toast.makeText(getBaseContext(), file + " was saved!", Toast.LENGTH_LONG).show();
                    //if (event == FileObserver.CREATE){
                    Log.d(TAG, "File created [" + pathToWatch + file + "]");
                    //}
                }
            };
            observer.stopWatching();
            observer.startWatching(); //START OBSERVING
            System.out.println(pathToWatch);
        } else {
            tv.setText("NOT WATCHING \n App couldn't get permission to read files");
        }

        // http://stackoverflow.com/questions/7265906/how-do-you-implement-a-fileobserver-from-an-android-service

        // TODO: Copiar recursive solution http://www.java2s.com/Open-Source/Android_Free_Code/Example/course/com_toraleap_collimator_utilRecursiveFileObserver_java.htm


    }

    public void show_notification(View view) {

        // https://developer.android.com/guide/topics/ui/notifiers/notifications.html#CreateNotification

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.cherry)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

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

    public void translate_audio(View view) {
        TextView tv = (TextView)findViewById(R.id.textview_filechanged);
        if (ask_for_permission(Manifest.permission.INTERNET)) {
            try {

            } catch(Error e) {
                Toast.makeText(getBaseContext(), "AAAAAAA" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            Toast.makeText(getBaseContext(), "AAAAAAA", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getBaseContext(), "ERROR! \n App couldn't get permission to use internet", Toast.LENGTH_LONG).show();
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

// CREATE FILE SNIPPET FOR TESTING AUDIO FILE CREATION

// ALT+c .. + (l to uncomment)/(b to comment)
//    // Get the directory for the user's public pictures directory.
//    final File path = new File(pathToWatch);
//
//    // Make sure the path directory exists.
//        if(!path.exists())
//                {
//                // Make it, if it doesn't exit
//                path.mkdirs();
//                }
//
//    final File file = new File(path, "config.txt");
//
//        // Save your stream, don't forget to flush() it before closing it.
//
//        try
//        {
//        file.createNewFile();
//        FileOutputStream fOut = new FileOutputStream(file);
//        OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//        myOutWriter.append("kaka");
//
//        myOutWriter.close();
//
//        fOut.flush();
//        fOut.close();
//        }
//        catch (IOException e)
//        {
//        Log.e("Exception", "File write failed: " + e.toString());
//        }