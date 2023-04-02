package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChannelEntityDAO {
    @Query("SELECT * FROM channels")
    List<Channel> getAll();

    @Query("SELECT * FROM channels WHERE `id` = :id")
    Channel getById(int id);

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    void insert (Channel channel);

    @Insert
    void insertAll(List<Channel> channelList);

    @Delete
    void delete (Channel channel);

    @Query("DELETE  FROM channels WHERE `playlist_id` = :playlist_id")
    void deleteAllChannelsByPlaylistId(long playlist_id);

    @Query("SELECT * FROM channels WHERE `group` = :group")
    List<Channel> getByGroup(String group);

    // Метод для проверки существования таблицы channels
//    @Query("SELECT sqlite_master.name FROM sqlite_master WHERE type='table' AND name = :channels")
//    String getChannelTableName(String channels);

}
