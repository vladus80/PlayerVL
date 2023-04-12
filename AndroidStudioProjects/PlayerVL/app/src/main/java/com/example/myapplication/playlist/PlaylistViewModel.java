package com.example.myapplication.playlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.Channel;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {

    private AppDateBase db;

    public PlaylistViewModel(@NonNull Application application) {
        super(application);
        db = AppDateBase.getInstance(application);
    }


    public LiveData<List<PlaylistData>> getPlaylists(){

        return  db.playlistDAO().getAllPlaylistsAll();
    }

    public LiveData<List<Channel>> getChannels(){

        return db.channelEntityDAO().getAllChannels();
    }
}
