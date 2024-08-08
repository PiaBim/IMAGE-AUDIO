package com.example.a0808;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AudioPlay extends AppCompatActivity {
    MediaPlayer mp = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioplay);
    }
    public void startResAudio(View v){
        mp=MediaPlayer.create(this,R.raw.old_pop);
        mp.start();
    }
    public void stopResAudio(View v){
        if(mp!=null){
            mp.stop();
            mp.release();
        }
        mp=null;
    }
}
