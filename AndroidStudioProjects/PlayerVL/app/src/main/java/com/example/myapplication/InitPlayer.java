package com.example.myapplication;


import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ALLOW_NON_IDR_KEYFRAMES;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS;
import static com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory.FLAG_IGNORE_SPLICE_INFO_STREAM;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class InitPlayer {
    @SuppressLint("WrongConstant")
    public static SimpleExoPlayer initPlayer(Context context, SimpleExoPlayer player, List<Channel> vidUri,
                                             final Player.Listener listener,
                                             final boolean tunneled, final boolean silent, final boolean forceMaxBit) {

        if (null != player) {
            player.release();
        }

        DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                .setUserAgent("PlayVl")
                .setConnectTimeoutMs(10000)
                .setReadTimeoutMs(10000)
                .setAllowCrossProtocolRedirects(true);

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(context).setEnableDecoderFallback(true);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF);

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setBufferDurationsMs(50000, 50000, 2500, 5000)
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
                .setForceHighestSupportedBitrate(forceMaxBit)
                .setExceedRendererCapabilitiesIfNecessary(true)  // адаптивное качество
                .setTunnelingEnabled(tunneled); /*Туннелирование звука, на телефоне вызывает проблемы с воспроизведением*/
        trackSelector.setParameters(tsParamsBuilder);

        /*final Uri vidUri = Uri.parse(videoUrl);
        @C.ContentType int contentType = Util.inferContentType(vidUri);
        if (contentType == C.TYPE_OTHER && videoUrl.toLowerCase().contains("m3u8"))
            contentType = C.TYPE_HLS;*/


       /* MediaSourceFactory mediaSourceFactory = null;
        if (contentType == C.TYPE_HLS) { // init HLS
            HlsExtractorFactory hlsFactory = new DefaultHlsExtractorFactory();
            mediaSourceFactory = new HlsMediaSource.Factory(dataSourceFactory)
                    .setAllowChunklessPreparation(true)
                    .setExtractorFactory(hlsFactory);
        }*/ /*else {
            if (contentType == C.TYPE_DASH) { // init DASH
                mediaSourceFactory = new DashMediaSource.Factory(dataSourceFactory);
            } else {
                if (contentType == C.TYPE_SS) { // init SmoothStream
                    mediaSourceFactory = new SsMediaSource.Factory(dataSourceFactory);
                } else { // init Extractors or Progressive
                    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
                            .setTsExtractorFlags(FLAG_ALLOW_NON_IDR_KEYFRAMES
                                    | FLAG_DETECT_ACCESS_UNITS
                                    | FLAG_IGNORE_SPLICE_INFO_STREAM
                                    | FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS
                            )
                            .setMp4ExtractorFlags(Mp4Extractor.FLAG_WORKAROUND_IGNORE_EDIT_LISTS)
                            .setFragmentedMp4ExtractorFlags(
                                    FragmentedMp4Extractor.FLAG_WORKAROUND_IGNORE_EDIT_LISTS
                                            | FragmentedMp4Extractor.FLAG_WORKAROUND_IGNORE_TFDT_BOX
                                            | FragmentedMp4Extractor.FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME
                                            | FragmentedMp4Extractor.FLAG_ENABLE_EMSG_TRACK
                            )
                            .setTsExtractorMode(TsExtractor.MODE_MULTI_PMT);
                    trackSelector = new DefaultTrackSelector(context);

                }
            }
        }*/


//        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory()
//                .setTsExtractorFlags(FLAG_ALLOW_NON_IDR_KEYFRAMES
//                                | FLAG_DETECT_ACCESS_UNITS
//                                | FLAG_IGNORE_SPLICE_INFO_STREAM
//                                | FLAG_ENABLE_HDMV_DTS_AUDIO_STREAMS);
//       MediaSourceFactory mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory)
//                .setContinueLoadingCheckIntervalBytes(ProgressiveMediaSource.DEFAULT_LOADING_CHECK_INTERVAL_BYTES / 2);


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



        if (null != listener) player.addListener(listener);
        player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        if (silent) player.setVolume(0);

        List<MediaSource> urlChannel = getMediaSourceList(context, vidUri);


        player.setMediaSources(urlChannel);

        Toast.makeText(context, "Запущен player Init", Toast.LENGTH_SHORT).show();
        return player;
    }

    private static List<MediaSource> getMediaSourceList (Context context, List<Channel> channelList){

        List<MediaSource> mediaSourceList = new ArrayList<>();
        //urlList = new ArrayList<>();
        for (Channel urlChannel: channelList) {

            try {
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,  Util.getUserAgent(context, "PlayVl"));
                HlsMediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(MediaItem.fromUri(Uri.parse(urlChannel.getUrlChannel())));
                mediaSourceList.add(mediaSource);
                System.out.println(urlChannel);

            }catch (Exception e){
                e.getMessage();
            }
        }
        return mediaSourceList;
    }
}