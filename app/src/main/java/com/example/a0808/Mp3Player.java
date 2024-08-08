package com.example.a0808;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

//<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"/>
//<application
//android:requestLegacyExternalStorage="true"

public class Mp3Player extends AppCompatActivity {
    ArrayList<String> songList = new ArrayList<String>();
    ListView listview;
    Button bPlay, bStop;
    String curSong;
    MediaPlayer mp = null;
    String songPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp3player);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // Android 11 이상인 경우
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                initializeMusicList();
            }
        } else {
            // Android 11 미만인 경우
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializeMusicList();
        } else {
            Log.e("KKK", "Permission denied");
        }
    }

    private void initializeMusicList() {
        songPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getPath() + "/";
        Log.i("KKK", "Music path: " + songPath);

        FileFilter filter = file -> !file.isDirectory() && file.getName().endsWith(".mp3");
        File musicDir = new File(songPath);
        File[] files = musicDir.listFiles(filter);

        if (files != null) {
            for (File file : files) {
                Log.i("KKK", "Found music file: " + file.getName());
                songList.add(file.getName());
            }
        } else {
            Log.i("KKK", "No music files found or directory not accessible");
        }

        if (songList.isEmpty()) {
            Log.e("KKK", "No songs found in the directory.");
        }

        listview = findViewById(R.id.listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, songList);
        listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listview.setAdapter(adapter);
        listview.setItemChecked(0, true);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                curSong = songList.get(arg2);
            }
        });

        if (!songList.isEmpty()) {
            curSong = songList.get(0);
        }
    }

    public void play(View v) {
        try {
            mp = new MediaPlayer();
            mp.setDataSource(songPath + curSong);
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            Log.i("KKK", e.toString());
        }
    }

    public void stop(View v) {
        if (mp != null) {
            mp.stop();
            mp.release();
        }
        mp = null;
    }
}
