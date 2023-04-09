package com.example.myapplication;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.playlist.PlaylistData;
import com.example.myapplication.playlist.PlaylistDAO;

@Database(entities = {Channel.class, PlaylistData.class}, version = 1, exportSchema = false)
public abstract class AppDateBase extends RoomDatabase {


    private static AppDateBase instance;

    public static synchronized AppDateBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDateBase.class, "iptv.db")
                            //.allowMainThreadQueries()
                            .build();
        }
        return instance;
    }
    public abstract ChannelEntityDAO channelEntityDAO();
    public abstract PlaylistDAO playlistDAO();

}
