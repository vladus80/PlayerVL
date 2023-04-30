package com.example.myapplication.playlist;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.Channel;
import com.vladus.parser.VLM3uEntity;
import com.vladus.parser.VlM3uParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistDownload {

    public static void downloadPlaylist(AppDateBase db, String namePlaylist, String filePlaylist, int active) {


        List<Channel> channels = new ArrayList<>();

        PlaylistData playlist = new PlaylistData(0, namePlaylist, "", filePlaylist, active);
        long playlistId = db.playlistDAO().insert(playlist);
        List<VLM3uEntity> vlm3uEntities = VlM3uParser.parse(filePlaylist);

        for (VLM3uEntity vlm3u : vlm3uEntities) {
            channels.add(new Channel(0, vlm3u.getNameChannel(),   //имя канала
                    vlm3u.getGroupChannel(), // группа
                    vlm3u.getEpgChannelId(), // епг
                    vlm3u.getUriChannel(),   // url канала
                    vlm3u.getLogoChannel(),  // url логотипа
                    Channel.LIKE,            // нравится
                    active,                 // видимый
                    playlistId,             // id плэйлиста
                    Channel.NOT_ACTIVATED    // Текущий активный канал: 0- не активный 1- активный

            ));

        }

        db.channelEntityDAO().insertAll(channels);

    }

}
