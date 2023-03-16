package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    private SimpleExoPlayer player;
    private List<Channel> channelList;
    private Context  context;



    public PlayerManager(List<Channel> channelList, Context context) {
        // Ссылка на список каналов
        this.channelList = channelList;
        this.context = context;
        // Создаем player
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        player = new SimpleExoPlayer.Builder(context, renderersFactory).setTrackSelector(trackSelector).build();
        // Устанавливаем в player mediaresurs
        player.setMediaSources(getMediaSourceList());


    }

    // Метод достает из списка объектов  каналов URL и создает mediaresurs
    public List<MediaSource> getMediaSourceList (){

        List<MediaSource> mediaSourceList = new ArrayList<>();
        for (Channel channel: channelList) {
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,  Util.getUserAgent(context, "PlayVl"));
            MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(channel.getUrlChannel().toString()));
            mediaSourceList.add(mediaSource);

        }

        return mediaSourceList;
    }


    public void getInfoTrack(){

        TrackGroupArray trackGroups = player.getCurrentTrackGroups();
        TrackSelectionArray trackSelections = player.getCurrentTrackSelections();

        for (int i = 0; i < trackGroups.length; i++) {
            TrackGroup trackGroup = trackGroups.get(i);
            TrackSelection trackSelection = trackSelections.get(i);
            if (trackSelection != null) {
                int selectedTrackIndex = trackSelection.getIndexInTrackGroup(0); // выбираем первый трек из группы
                Format selectedFormat = trackGroup.getFormat(selectedTrackIndex);
                Log.d(TAG, "Selected format: " + selectedFormat.toString());

                // Получить разрешение
                int width = selectedFormat.width;
                int height = selectedFormat.height;
                Log.d(TAG, "Разрешение: " + width + "x" + height);

                // Получить битрейт
                int bitrate = selectedFormat.bitrate;
                Log.d(TAG, "Бит: " + bitrate);

                // Получить кодек
                String codec = selectedFormat.sampleMimeType;
                Log.d(TAG, "Кодек: " + codec);
            }
        }


    }

    public SimpleExoPlayer getPlayer() {

        return player;
    }

    public void setPlayer(SimpleExoPlayer player) {
        this.player = player;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
