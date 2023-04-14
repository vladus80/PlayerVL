package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    private SimpleExoPlayer player;
    private List<Channel> channelList;
    private final Context  context;


    public PlayerManager(List<Channel> channelList, Context context) {
        // Ссылка на список каналов
        this.channelList = channelList;
        this.context = context;
        // Создаем player
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
        player = new SimpleExoPlayer.Builder(context, renderersFactory).setTrackSelector(trackSelector).build();
        // Устанавливаем в player mediaresurs
        player.setMediaSources( getMediaSourceList());

        Toast.makeText(context, "Запущен player Maneger", Toast.LENGTH_SHORT).show();

    }

    // Метод достает из списка объектов  каналов URL и создает mediasource
    public List<MediaSource> getMediaSourceList (){

        List<MediaSource> mediaSourceList = new ArrayList<>();
        for (Channel channel: channelList) {

            try {
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,  Util.getUserAgent(context, "PlayVl"));
                HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                                                    .createMediaSource(MediaItem.fromUri(Uri.parse(channel.getUrlChannel())));
                mediaSourceList.add(mediaSource);
            }catch (Exception e){
                e.getMessage();
            }
        }
        return mediaSourceList;
    }


    public List<HlsMediaSource> getHlsMediaSourceList (){

        List<HlsMediaSource> hlsMediaSourceList = new ArrayList<>();
        for (Channel channel: channelList) {

            try {
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,  Util.getUserAgent(context, "PlayVl"));
                HlsMediaSource hlsmediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                                                    .createMediaSource(MediaItem.fromUri(Uri.parse(channel.getUrlChannel())));
                hlsMediaSourceList.add(hlsmediaSource);
            }catch (Exception e){
                e.getMessage();
            }


        }
        return hlsMediaSourceList;
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
                Log.d(TAG, "Selected format: " + selectedFormat);
                Toast.makeText(context, "Selected format: " + selectedFormat, Toast.LENGTH_SHORT).show();

                // Получить разрешение
                int width = selectedFormat.width;
                int height = selectedFormat.height;
                Log.d("trackSelection", "Разрешение: " + width + "x" + height);

                // Получить битрейт
                int bitrate = selectedFormat.bitrate;
                Log.d("trackSelection", "Бит: " + bitrate);

                // Получить кодек
                String codec = selectedFormat.sampleMimeType;
                Log.d("trackSelection", "Кодек: " + codec);
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
