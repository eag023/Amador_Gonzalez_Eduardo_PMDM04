package dam.pmdm.spyrothedragon;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        VideoView videoView = findViewById(R.id.videoView);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.easter_egg_video); // Asegúrate de tener el video en res/raw
        videoView.setVideoURI(videoUri);

        videoView.setOnCompletionListener(mp -> {
            // Regresar automáticamente a la pestaña de coleccionables
            Intent intent = new Intent(VideoActivity.this, MainActivity.class);
            intent.putExtra("navigate_to", R.id.navigation_collectibles);
            startActivity(intent);
            finish();
        });

        videoView.start();
    }
}
