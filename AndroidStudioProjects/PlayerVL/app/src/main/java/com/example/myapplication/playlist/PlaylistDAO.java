package com.example.myapplication.playlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.Channel;

import java.util.List;

@Dao
public interface PlaylistDAO {

    @Query("SELECT * FROM playlist")
    List<PlaylistData> getAll();

    @Query("SELECT * FROM playlist WHERE `id` = :id")
    PlaylistData getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert (PlaylistData playlistData);

    @Delete
    void delete (PlaylistData playlistData);

    @Update
    void updatePlaylist(PlaylistData playlistData);

    @Query("SELECT * FROM playlist")
    List<PlaylistData> getAllPlaylists();

    @Query("SELECT * FROM channels WHERE playlist_id = :playlistId")
    List<Channel> getChannelsForPlaylist(long playlistId);

}
