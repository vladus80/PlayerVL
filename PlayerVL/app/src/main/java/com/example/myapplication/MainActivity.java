package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.playlist.PlaylistActivity;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private PlayerView playerView;
    private RecyclerView recyclerView;
    private ImageButton btnStarLike;
    private SimpleExoPlayer player;
    private PowerManager.WakeLock mWakeLock; //Чтобы держать устройство включенным
    private ItemTouchHelper itemTouchHelper; // перетаскивать каналы в recycleView

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        playerView = findViewById(R.id.player_view);
        recyclerView = findViewById(R.id.recycler_view);
        View itemRecycler = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_recycleview_channel, null);
        btnStarLike = itemRecycler.findViewById(R.id.btn_star_like);

        // Инициализируем менеджер чтобы экран не гас
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakelockTag");


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
                   // Log.d("onPositionDiscontinuity", "Старая позиция "+ oldPosition.mediaItemIndex + " Новая позиция " + newPosition.mediaItemIndex);
                    //Log.d("onPositionDiscontinuity",String.valueOf( format.height));

                }
            }

        };

        // channelList = new Channel(file.getAbsoluteFile()).getChannelList();
        List<Channel> channelList = SingletonListChannel.getInstance().getChannelList();

        String nameGroupFromIntent = getIntent().getStringExtra("name_group");
        List<Channel> channels = channelList.stream().filter(channel -> channel.getGroupChannel().equals(nameGroupFromIntent)).collect(Collectors.toList());

        String playerEx = "PlayerManager";
        if(playerEx.equals("PlayerManager")){
            PlayerManager playerManager = new PlayerManager(channels, this);

            player = playerManager.getPlayer();
            player.addListener(listener);
        } else if(playerEx.equals("PlayerInit")) {
            player = InitPlayer.initPlayer(this, new SimpleExoPlayer.Builder(this).build(),
                    channels, listener, false, false, false);
        }


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


        /*Сетка ListviewRecycle*/
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Если находимся в портретном режиме, то  заполняем список каналов ListviewRecycle
        if (recyclerView != null) {
            recyclerView.setLayoutManager(layoutManager);
            // Обработка клика  на строке ListviewRecycle (переключаем канал при клике на строку)
            //Log.d("onPositionDiscontinuity", Utils.getMediaInfoCodec(this, player.getCurrentMediaItem()).get().toString());
            MyAdapter adapter = new MyAdapter(channels, (channel, position) -> {
                player.seekTo(position, 0);
                player.setPlayWhenReady(true);
                //Log.d("onPositionDiscontinuity", Utils.getMediaInfoCodec(this, player.getCurrentMediaItem()).get().toString());

            });

            recyclerView.setAdapter(adapter);
            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
            itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }


    }


    private void setFullScreen() {
        // Скрыть все элементы управления системы
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        // Установить флаги, чтобы Activity оставалась в полноэкранном режиме
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // Будет устанавливать full экран в зависимости от ориентации
    public void setFullScreen(boolean isFullScreen) {
        if (isFullScreen) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().show();
        }
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
        //Toast.makeText(this, "Сработал onResume", Toast.LENGTH_SHORT).show();
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

