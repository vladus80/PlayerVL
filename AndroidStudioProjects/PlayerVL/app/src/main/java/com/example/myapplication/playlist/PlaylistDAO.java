package com.example.myapplication.playlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.Channel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface PlaylistDAO {

    @Query("SELECT * FROM playlist WHERE `playlist_id` = :id")
    PlaylistData getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert (PlaylistData playlistData);

    @Delete
    void delete (PlaylistData playlistData);

    @Update
    void updatePlaylist(PlaylistData playlistData);

    @Query("SELECT * FROM playlist")
   List<PlaylistData> getAllPlaylists();

    @Query("SELECT * FROM playlist")
    LiveData<List<PlaylistData>> getAllPlaylistsAll();

    @Query("SELECT * FROM channels WHERE playlist_id = :playlistId")
    List<Channel> getChannelsForPlaylist(long playlistId);

    /** Устанавливает статус активности плэйлиста в поле active */
    @Query("UPDATE playlist SET active = :active_state WHERE playlist_id = :id")
    void setActive(long id, int active_state);

    /** Подсчитывает колво плэйлистов с указанным именем*/
    @Query("SELECT COUNT(*) FROM playlist WHERE playlist_name = :name ")
    int getCountPlaylistByName(String name);



}
