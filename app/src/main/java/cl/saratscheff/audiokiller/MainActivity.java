package cl.saratscheff.audiokiller;

        import android.Manifest;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    public static String TAG;
    public static String whatsappVoicePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = getString(R.string.tag);
        whatsappVoicePath = Environment.getExternalStorageDirectory().getPath() + getString(R.string.whatsapp_voice_path);

        if (ask_for_permission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && ask_for_permission(Manifest.permission.INTERNET)) {
            TextView tv = (TextView)findViewById(R.id.textview_filechanged);
            tv.setText(getString(R.string.observers_working_message));
            Intent iFileObserver = new Intent(MainActivity.this, FilesObservingService.class);
            MainActivity.this.startService(iFileObserver);
        } else {
            Toast.makeText(getBaseContext(), "ERROR 004! Couldn't get the required permissions", Toast.LENGTH_LONG).show();
        }
    }

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
            if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Couldn't get necessary permissions!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Log.d(TAG, "Permission " + permission_type + "granted!");
        return true;
    }
}
