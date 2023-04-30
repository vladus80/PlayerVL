package com.example.myapplication;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.upstream.HttpDataSource;

import java.io.IOException;

public class PlayerListener  {

    public static Player.Listener listener(){
        Player.Listener listener = new Player.Listener() {
            @Override
            public void onEvents(Player player, Player.Events events) {
                Player.Listener.super.onEvents(player, events);

            }

            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                error.getMessage();
                Log.d("ErrorMessage: ", error.getErrorCodeName());

                Throwable cause = error.getCause();
                if (cause instanceof HttpDataSource.InvalidResponseCodeException) {
                    HttpDataSource.InvalidResponseCodeException httpError = (HttpDataSource.InvalidResponseCodeException) cause;
                    int responseCode = httpError.responseCode;
                    if (responseCode == 403) {
                        // Обработка ошибки 403



                    }
                }


            }

            /* Смена MediaItem*/
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
            }
        };

        return listener;
    }

}
