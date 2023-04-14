package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.Tracks;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.hls.HlsManifest;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements OnClickListenerBtnLike, OnClickListenerItem {

    private PlayerView playerView;
    private RecyclerView recyclerView;

    private SimpleExoPlayer player;
    private PowerManager.WakeLock mWakeLock; //Чтобы держать устройство включенным
    private ItemTouchHelper itemTouchHelper; // перетаскивать каналы в recycleView

    private ChannelAdapterRecyclerView channelAdapterRecyclerView;
    private MainActivityViewModel viewModel;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        // Инициализируем менеджер чтобы экран не гас
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakelockTag");

        playerView = findViewById(R.id.player_view);
        recyclerView = findViewById(R.id.recycler_view);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        String nameGroup = getIntent().getStringExtra("name_group");

        viewModel.getChannelsLD(nameGroup).observe(this, new Observer<List<Channel>>() {
            @Override
            public void onChanged(List<Channel> channels) {

                PlayerManager playerManager = new PlayerManager(channels, MainActivity.this);
                player = playerManager.getPlayer();
                initPlayerPlay(player, channels);

                channelAdapterRecyclerView = new ChannelAdapterRecyclerView(channels,
                        MainActivity.this,
                        MainActivity.this);
                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                if(recyclerView != null){
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(channelAdapterRecyclerView);
                }

                /* Перемещение item в списке*/
                ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(new ItemTouchHelperAdapter() {

                    @Override
                    public void onItemMove(int fromPosition, int toPosition) {
                        // Обменять элементы в списке
                        Collections.swap(channels, fromPosition, toPosition);
                        // Уведомить адаптер об изменениях
                        channelAdapterRecyclerView.notifyItemMoved(fromPosition, toPosition);
                    }
                });
                itemTouchHelper = new ItemTouchHelper(callback);
                itemTouchHelper.attachToRecyclerView(recyclerView);

            }
        });

    }

    private void initPlayerPlay(SimpleExoPlayer player, List<Channel> channelList){




        /*Определяем слушатель player*/
        Player.Listener listener = new Player.Listener() {
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

        player.addListener(listener);

        playerView.setPlayer(player);
        player.prepare();
        player.setRepeatMode(Player.REPEAT_MODE_ALL);/*навигация канал по кругу*/
        player.setPlayWhenReady(true);

        /*Жесты */
        GestureEventListener gestureEventListener = new GestureEventListener(this, player);
        player.addListener( gestureEventListener);
        playerView.setOnTouchListener(gestureEventListener);


        /*Пробуем переопределить кнопки*/

        CustomForwardingPlayer forwardingPlayer = new CustomForwardingPlayer(player);/*Копия Player для переназначения кнопок*/
        playerView.setPlayer(forwardingPlayer);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);/*Прогрес буфферизации*/
        playerView.setShowFastForwardButton(false); /*скрываем кнопки перемотки*/
        playerView.setShowRewindButton(false);


        PlayerControlView playerControlView = new PlayerControlView(this);
        playerControlView.setPlayer(forwardingPlayer);
        playerControlView.setShowNextButton(false);

//        Log.d("getDuration", "getDuration: " + player.getDuration());
//        Log.d("getContentDuration", "getDuration: " + player.getContentDuration());
//        Log.d("getTotalBufferedDuration", "getDuration: " + player.getTotalBufferedDuration());

    }


    @Override
    public void onClickBtnLike(Channel channel) {viewModel.setLike(channel);}

    @Override
    public void onClickItem(int position) {
        player.seekTo(position, 0);
        player.setPlayWhenReady(true);
    }

    /* В зависимости от ориентации прячем заголовок и разворачиваем на весь экран*/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            Toast.makeText(this, "ORIENTATION_LANDSCAPE", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE); // Если ландшафтная ориентация то прячем  recyclerView (список каналов)

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            recyclerView.setVisibility(View.VISIBLE); // Если портретная ориентация то показываем  recyclerView (список каналов)
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(this, "Сработал onStart", Toast.LENGTH_SHORT).show();
        if(null != player){

            player.setPlayWhenReady(true);
            player.seekTo(0);
            //playerView.setUseController(false);
        }


        mWakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "Сработал onStop", Toast.LENGTH_SHORT).show();
        mWakeLock.release();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(this, "Сработал onDestroy", Toast.LENGTH_SHORT).show();

        if(null != player){

            player.stop();
            player.release();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainActivity.this, GroupChannelActivity.class));
    }

}

