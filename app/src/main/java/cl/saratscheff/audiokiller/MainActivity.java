package cl.saratscheff.audiokiller;

        import android.Manifest;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String TAG;
    public static String whatsappVoicePath;
    ArrayList<String> transcriptArray = new ArrayList<String>();
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = getString(R.string.tag);
        whatsappVoicePath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.whatsapp_voice_path);
        adapter = new ArrayAdapter<String>(this, R.layout.transcript_tv, transcriptArray);

        ListView listView = (ListView) findViewById(R.id.audio_list);
        listView.setAdapter(adapter);
        adapter.add("Hi, my name is Pedro and this is a test Audio");
        adapter.add("Facebook rocks!");
        adapter.add("Can you believe it? The world is about to end next year, they are saying so all over the non existant world.");
        adapter.add("Junemann is the man");
        adapter.add("Where's Bambi?");
        adapter.add("It's way too warm in here");
        adapter.add("Music is not loud enough...");
        adapter.add("I'm freezing!");
        adapter.add("Pump it up! We want louder music!");
        adapter.add("Testing Google Speech API");
        adapter.add("This is a test");
        adapter.add("Hi, I'm Pedro");

        TextView tv = (TextView)findViewById(R.id.textview_filechanged);
        if (ask_for_permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ask_for_permission(Manifest.permission.INTERNET);
            tv.setText(getString(R.string.observers_working_message));
            Intent iFileObserver = new Intent(MainActivity.this, FilesObservingService.class);
            MainActivity.this.startService(iFileObserver);
        } else {
            Log.e(TAG, "ERROR 008!");
            Toast.makeText(getBaseContext(), "ERROR 004! Couldn't get the required permissions", Toast.LENGTH_LONG).show();
            tv.setText(getString(R.string.permission_error_message));
        }
        registerReceiver(newAudioTranscriptBroadcast, new IntentFilter("newAudioTranscript"));
    }

    private BroadcastReceiver newAudioTranscriptBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent i) {
            adapter.insert(i.getStringExtra("transcript"), 0);
        }
    };

    public void create_sample_audio_file(View view) {
        String file_path = whatsappVoicePath + "PTT-20170408-WA1111.opus";
        File f0 = new File(file_path);
        boolean d0 = f0.delete();
        Log.d(TAG, "File deleted: " + file_path + d0);

        try {
            InputStream in = getResources().openRawResource(R.raw.sampleaudioen);
            FileOutputStream out = new FileOutputStream(whatsappVoicePath + "PTT-20170408-WA1111.opus");
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

    private boolean ask_for_permission(String permission_type) {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, permission_type);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{permission_type}, 1);
            int cnt = 0;
            while(cnt<200) {
                permissionCheck = ContextCompat.checkSelfPermission(this, permission_type);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    Log.e(TAG, "ERROR 007! " + e.getMessage());
                    return false;
                }
                cnt++;
            }
            if(cnt == 200) {
                Log.e(TAG, "ERROR 007-b! No permission granted");
                return false;
            }
        }
        Log.d(TAG, "Permission " + permission_type + "granted!");
        return true;
    }
}
