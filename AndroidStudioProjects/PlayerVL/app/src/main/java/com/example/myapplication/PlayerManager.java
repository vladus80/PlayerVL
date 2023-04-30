package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    private SimpleExoPlayer player;
    private List<Channel> channelList;
    private final Context  context;
    private Player.Listener listener;


    public PlayerManager(List<Channel> channels,Context context) {
        // Ссылка на список каналов
        this.context = context;
        this.channelList = channels;
        // Создаем player


        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("PlayVl")
                .setConnectTimeoutMs(10000)
                .setReadTimeoutMs(10000)
                .setAllowCrossProtocolRedirects(true);

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context).setEnableDecoderFallback(true);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(5000, 5000, 2000, 5000)
                .setTargetBufferBytes(C.LENGTH_UNSET)
                .setPrioritizeTimeOverSizeThresholds(false)
                .build();

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(context, new AdaptiveTrackSelection.Factory());
        DefaultTrackSelector.Parameters.Builder tsParamsBuilder = trackSelector.buildUponParameters()
                .setAllowAudioMixedChannelCountAdaptiveness(true)
                .setAllowAudioMixedSampleRateAdaptiveness(true)
                .setAllowAudioMixedMimeTypeAdaptiveness(true)
                .setAllowVideoMixedMimeTypeAdaptiveness(true)
                .setAllowVideoNonSeamlessAdaptiveness(true) //Плавное переключение
                .setExceedAudioConstraintsIfNecessary(true)
                .setExceedVideoConstraintsIfNecessary(true)  // адаптивный Выбор лучшего  bitrate
                .setForceHighestSupportedBitrate(false) //  флаг, указывающий на то, что необходимо выбирать максимально возможный битрейт
                .setExceedRendererCapabilitiesIfNecessary(true)  // адаптивное качество
                .setTunnelingEnabled(false); /*Туннелирование звука, на телефоне вызывает проблемы с воспроизведением*/
        trackSelector.setParameters(tsParamsBuilder);

        HlsExtractorFactory hlsFactory = new DefaultHlsExtractorFactory();
        MediaSourceFactory mediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory)
                .setAllowChunklessPreparation(true)
                .setExtractorFactory(hlsFactory);

        player = new SimpleExoPlayer.Builder(context, renderersFactory)
                .setMediaSourceFactory(mediaSourceFactory)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .setUseLazyPreparation(true)
                .build();

        //player = new SimpleExoPlayer.Builder(context, renderersFactory).setTrackSelector(trackSelector).build();
        // Устанавливаем в player mediasours
        //new MediaMetadata.Builder().set

        listener = new Player.Listener() {
            @Override
            public void onEvents(@NonNull Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);


            }

            @Override
            public void onPositionDiscontinuity(@NonNull Player.PositionInfo oldPosition, @NonNull Player.PositionInfo
                    newPosition, @Player.DiscontinuityReason int reason) {
                Player.Listener.super.onPositionDiscontinuity(reason);
                if (reason == Player.DISCONTINUITY_REASON_SEEK) {

                }
            }


            /*Обработка ошибок*/
            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                if (error.errorCode == ExoPlaybackException.TYPE_SOURCE) {
                    Toast.makeText(context, error.getErrorCodeName() + "  " + error.errorCode, Toast.LENGTH_SHORT).show();
                } else if(error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS) {
                    //Toast.makeText(context, error.getErrorCodeName() + "  " + error.errorCode, Toast.LENGTH_SHORT).show();
                    Snackbar.make(context, ((Activity) context).findViewById(R.id.player_view),
                                                error.getErrorCodeName() + "  " +
                                                     error.errorCode, Snackbar.ANIMATION_MODE_SLIDE).show();
                    // Другая ошибка в проигрывателе
                    // Вывод сообщения об ошибке
                } else if (error.errorCode == PlaybackException.CUSTOM_ERROR_CODE_BASE) {
                    Toast.makeText(context, error.getErrorCodeName() + "  " + error.errorCode, Toast.LENGTH_SHORT).show();
                }
            }

                //error.getMessage();


            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);

                Timeline.Period period = timeline.getPeriod(0, new Timeline.Period());

                Object manifest = player.getCurrentManifest();
                if (manifest != null) {
                    HlsManifest hlsManifest = (HlsManifest) manifest;
                    // Do something with the manifest.
//                    Log.d("getDuration", "getDuration: " +
//                            hlsManifest.multivariantPlaylist.variants.get(0).format.);
//                    Snackbar.make(context, ((Activity) context).findViewById(R.id.player_view),
//                            ""+hlsManifest.mediaPlaylist.trailingParts, Snackbar.ANIMATION_MODE_SLIDE).show();
                }

            }


        };

        //MediaItem mediaItem = new MediaItem.Builder().

        //player.setMediaSources( getMediaSourceList());
        player.setMediaItems(getMediaItems(channels));
        player.addListener(listener);


        Toast.makeText(context, "Запущен Player Manager", Toast.LENGTH_SHORT).show();

        //MediaMetadata mediaMetadata = new MediaMetadata.Builder().

    }

    // Метод достает из списка объектов  каналов URL и создает mediasource
    public List<MediaSource> getMediaSourceList (){

        List<MediaSource> mediaSourceList = new ArrayList<>();

        for (Channel channel: getChannelList()) {
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

    public List<MediaItem> getMediaItems(List<Channel> channelList){

        List<MediaItem> mediaItemList = new ArrayList<>();
        for (Channel channel : channelList) {
            mediaItemList.add(new MediaItem.Builder().setUri(channel.getUrlChannel())

                    .setTag(channel)
                    .build()

            );
        }

        return mediaItemList;

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
