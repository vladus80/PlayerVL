package com.example.myapplication;

import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.ForwardingPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

public class CustomForwardingPlayer extends ForwardingPlayer {
    /**
     * Creates a new instance that forwards all operations to {@code player}.
     *
     * @param player
     */

    private Player player;

    public CustomForwardingPlayer(Player player) {
        super(player);

        this.player = player;
        //super(player);
    }



    @Override
    public void play() {
        // Добавьте дополнительную логику здесь перед вызовом play()

       // Utils.Toast(this,"");

        //Log.d("Forwading_player", "Сработала кнопка play" );

        super.play();
    }

    @Override
    public void seekToPrevious() {


        int curMediaItem = player.getCurrentMediaItemIndex();
        int allMediaItems = player.getMediaItemCount();
        Log.d("getCurrentMediaItemIndexAllItem", String.valueOf(allMediaItems));
        Log.d("getCurrentMediaItemIndex", String.valueOf(curMediaItem));
//
        super.seekToPreviousMediaItem();
    }

    @Override
    public void seekBack() {


        super.seekBack();
    }
}
