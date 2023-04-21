package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pools;

import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.EventLogger;


import java.util.AbstractMap;

public class PlayerInit {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private Context context;
    private Player.Listener listener;

    public PlayerInit(SimpleExoPlayer player, PlayerView playerView, Context context) {

        this.context = context;
        this.player = player;
        this.playerView = playerView;

        /*Определяем слушатель player*/
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
                if (error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS){
                    Toast.makeText(context, "Ошибка источника 404", Toast.LENGTH_SHORT).show();
                    player.play();

                }

                error.getMessage();
            }

            @Override
            public void onTimelineChanged(Timeline timeline, int reason) {
                Player.Listener.super.onTimelineChanged(timeline, reason);

                Timeline.Period period = timeline.getPeriod(0, new Timeline.Period());

                Object manifest = player.getCurrentManifest();
                if (manifest != null) {
                    HlsManifest hlsManifest = (HlsManifest) manifest;
                    // Do something with the manifest.
                    Log.d("getDuration", "getDuration: " +
                            hlsManifest.multivariantPlaylist.mediaPlaylistUrls);
                }

            }

        };
        /*Слушатели*/
        player.addListener(listener);

        /*Аналитика*/
        player.addAnalyticsListener(new EventLogger());

        /*Обработка ошибок*/


        playerView.setPlayer(player);
        player.prepare();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);/*навигация канал по кругу*/
        player.setPlayWhenReady(true);

        /*Жесты */
        GestureEventListener gestureEventListener = new GestureEventListener(context, player);
        player.addListener(gestureEventListener);
        playerView.setOnTouchListener(gestureEventListener);


        /*Пробуем переопределить кнопки*/

//        forwardingPlayer = new CustomForwardingPlayer(player);/*Копия Player для переназначения кнопок*/
//        playerView.setPlayer(forwardingPlayer);
//        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);/*Прогрес буфферизации*/
//        playerView.setShowFastForwardButton(false); /*скрываем кнопки перемотки*/
//        playerView.setShowRewindButton(false);
//
//
//        PlayerControlView playerControlView = new PlayerControlView(this);
//        playerControlView.setPlayer(forwardingPlayer);
//        playerControlView.setShowNextButton(false);


    }
}
