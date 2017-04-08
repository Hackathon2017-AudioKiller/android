package cl.saratscheff.audiokiller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import org.json.JSONObject;

public class TranscriptActivity extends AppCompatActivity {

    public String transcript;
    public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcript);
        TextView tv = (TextView)findViewById(R.id.textview_transcript);
        tv.setMovementMethod(new ScrollingMovementMethod());

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                transcript = null;
            } else {
                transcript = extras.getString("transcript");
            }
        } else {
            transcript= (String) savedInstanceState.getSerializable("transcript");
        }

        tv.setText(transcript);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        //savedInstanceState.putBoolean("MyBoolean", true);
        //savedInstanceState.putDouble("myDouble", 1.9);
        //savedInstanceState.putInt("MyInt", 1);
        savedInstanceState.putString("transcript", transcript);
        // etc.
    }
}
