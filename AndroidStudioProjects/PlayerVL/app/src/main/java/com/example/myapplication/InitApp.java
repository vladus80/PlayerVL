package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.playlist.PlaylistActivity;
import com.example.myapplication.playlist.PlaylistData;

import java.util.List;

public class InitApp extends AppCompatActivity {


    private AppDateBase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_app);

        db = AppDateBase.getInstance(this);

        if(getPlaylistsInDB() == true){

            vlStartActivity(GroupChannelActivity.class);

        }else {
            vlStartActivity(PlaylistActivity.class);

        }
        Log.d("CountPlaylistDB", String.valueOf(getPlaylistsInDB()));
    }




    private boolean getPlaylistsInDB(){

        boolean res;
        List<PlaylistData> playlistData = db.PlaylistDAO().getAllPlaylists();
        Log.d("CountPlaylistDB", String.valueOf(playlistData.size()));
        if(playlistData.size() > 0 ){
            res = true;
        }else {
            res = false;
        }
        return res;
    }

    private void vlStartActivity(Class<? extends Activity> activityClass){

        Intent intent = new Intent(getApplicationContext(),activityClass);
        startActivity(intent);
    }


}
