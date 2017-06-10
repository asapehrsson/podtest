package podtest.jiki.se.podtest;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EpisodeActivity extends AppCompatActivity {
    public static void open(Context context) {
        Intent intent = new Intent(context, EpisodeActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode);
    }
}
