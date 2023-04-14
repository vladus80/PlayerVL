package com.example.myapplication;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ChannelEntityDAO {
    /**
     * Возвращает все каналы
     */
    @Query("SELECT * FROM channels")
    List<Channel> getAll();

    /**
     * Возвращает канал по id
     */
    @Query("SELECT * FROM channels WHERE `id` = :id")
    Channel getById(int id);

    /**
     * Вставляет новый канал
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Channel channel);

    /**
     * Вставляет все каналы в коллекции
     */
    @Insert
    void insertAll(List<Channel> channelList);

    /**
     * Удаляет канал
     */
    @Delete
    void delete(Channel channel);

    /**
     * Удаляет канал по id плэйлиста
     */
    @Query("DELETE  FROM channels WHERE `playlist_id` = :playlist_id")
    void deleteAllChannelsByPlaylistId(long playlist_id);

    /**
     * Выбирает все каналы по имени группы
     */
    @Query("SELECT * FROM channels WHERE `group` = :group")
    List<Channel> getByGroup(String group);

    /**
     * Скрывает каналы по id плэйлиста. Если плэйлист не активный то каналы принадлежащие плэйлисту будут скрыты
     */
    @Query("UPDATE channels SET visible = :active WHERE playlist_id = :playlist_id")
    void setActive(long playlist_id, int active);

    /**
     * Выбирает все каналы у которых в плэйлистах видимость 0 или 1
     */
    @Query("SELECT channels.* FROM channels LEFT JOIN  playlist " +
            "ON channels.playlist_id=playlist.id " +
            "WHERE channels.visible = :active")
    List<Channel> getChannelsByActivePlaylist(int active);

    @Query("SELECT channels.* FROM channels LEFT JOIN  playlist " +
            "ON channels.playlist_id=playlist.id " +
            "WHERE channels.visible = :active")
    LiveData<List<Channel>> getChannelsByActivePlaylistLD(int active);



    /**
     * Возвращает сколько каналов содержит playlist по playlist_id
     */
    @Query("SELECT COUNT(*) FROM channels WHERE playlist_id = :playlist_id")
    int getSizePlaylist(long playlist_id);

    /**
     * Возвращает все channels в LiveData типе
     */
    @Query("SELECT * FROM channels")
    LiveData<List<Channel>> getAllChannels();

    @Query("SELECT `like` FROM channels WHERE id = :id")
    int isActive(long id);

    @Query("UPDATE channels SET `like` = :like WHERE id = :id")
    int updateLike(long id, int like);

    @Query("SELECT * FROM channels WHERE `like` = 1 AND `visible` = 1")
    List<Channel> getLikes();

    /*Возвращает активные каналы в избранном*/
    @Query("SELECT * FROM channels WHERE `like` = 1 AND `visible` = 1")
    LiveData<List<Channel>> getLikesLD();
}
