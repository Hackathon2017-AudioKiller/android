package cl.saratscheff.audiokiller;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.VideoView;

public class SubtitledVideoActivity extends AppCompatActivity {
    private VideoView vv;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subtitled_video);

        vv = (VideoView)findViewById(R.id.videoView);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                url = null;
            } else {
                url = extras.getString("url");
            }
        } else {
            url= (String) savedInstanceState.getSerializable("url");
        }

        Uri video = Uri.parse(url);
        vv.setVideoURI(video);
        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                vv.start();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("url", url);
    }
}