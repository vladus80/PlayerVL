package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private PlayerView playerView;
    private RecyclerView recyclerView;
    private SimpleExoPlayer player;
    private Path path;
    ArrayList <Channel> channels;
    private PowerManager.WakeLock mWakeLock; //Чтобы держать устройство включенным
    private int recyclerViewScrollPosition;  // позиция scroll (recyclerView) чтобы при повороте восстановить

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.player_view);
        recyclerView = findViewById(R.id.recycler_view);

        // Инициализируем менеджер чтобы экран не гас
        PowerManager powerManager = (PowerManager) getSystemService(this.POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakelockTag");



       /* int resId = getResources().getIdentifier("playlist", "raw", getPackageName()); // получение идентификатора ресурса
        InputStream inputStream = getResources().openRawResource(resId); // создание InputStream
        File file = new File(getFilesDir(), "playlist.m3u"); // создание объекта File для сохранения файла
        try {
            FileOutputStream outputStream = new FileOutputStream(file); // создание FileOutputStream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length); // запись данных в FileOutputStream
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            File file = Common.getPlaylistFromRaw(this);
            List<Channel> channelList = new Channel(file).getChannelList();
            PlayerManager playerManager = new PlayerManager(channelList, this);
            player = playerManager.getPlayer();
            setFullScreen();
            player.prepare();
            player.setPlayWhenReady(true);
            playerView.setPlayer(player);
            //.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);

            /*Жесты и касания  устанавливаем в классе GestureEventListener*/
            GestureEventListener gestureEventListener = new GestureEventListener(this, player);
            player.addListener(gestureEventListener);
            playerView.setOnTouchListener(gestureEventListener);

            /*Сетка ListviewRecycle*/
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);

            // Если находимся в портретном режиме, то  заполняем список каналов ListviewRecycle
            if (recyclerView != null) {
                recyclerView.setLayoutManager(layoutManager);
                // Обработка клика  на строке ListviewRecycle (переключаем канал при клике на строку)
                MyAdapter adapter = new MyAdapter(channelList, (channel, position) -> {
                    player.seekTo(position, 0);
                    player.setPlayWhenReady(true);

                });
                recyclerView.setAdapter(adapter);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        /*
/// получаем ссылку на Handler, связанный с основным потоком приложения
        Handler playerHandler = new Handler(Looper.getMainLooper());

// создаем новый объект Runnable
        Runnable playerRunnable = new Runnable() {
            @Override
            public void run() {
                // выполняем необходимые действия с ExoPlayer
                float getVolume = player.getVolume();
                Log.d(TAG, "Max video bitrate: " + getVolume);

                // постим задачу для выполнения через 1 секунду
                playerHandler.postDelayed(this, 1000);
            }
        };

// постим первую задачу для выполнения через 1 секунду
        playerHandler.postDelayed(playerRunnable, 1000);

    }

    Проверка коммента

 */

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

        // Установить размеры PlayerView на весь экран
//        playerView.setLayoutParams(new RelativeLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }

    // Запоминаем состояние player
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Сохраняем состояние ExoPlayer
        if (player != null) {
            savedInstanceState.putLong("position", player.getCurrentPosition());
            savedInstanceState.putInt("windowIndex", player.getCurrentWindowIndex());
        }

        if(recyclerView != null){
            // Сохраняем позицию прокрутки RecyclerView
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerViewScrollPosition = layoutManager.findFirstVisibleItemPosition();
            savedInstanceState.putInt("recyclerViewScrollPosition", recyclerViewScrollPosition);
        }

    }

    // Восстанавливаем состояние player
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Восстанавливаем состояние ExoPlayer
        if (savedInstanceState != null) {
            long position = savedInstanceState.getLong("position");
            int windowIndex = savedInstanceState.getInt("windowIndex");

            if (player != null) {
                player.seekTo(windowIndex, position);
            }
        }

        // Восстанавливаем позицию прокрутки RecyclerView из сохраненного состояния
        if(recyclerView != null) {
            recyclerViewScrollPosition = savedInstanceState.getInt("recyclerViewScrollPosition");
            recyclerView.scrollToPosition(recyclerViewScrollPosition);
        }

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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setFullScreen(true);
            getSupportActionBar().hide();

            Toast.makeText(this, "ORIENTATION_LANDSCAPE", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE); // Если ландшафтная ориентация то прячем  recyclerView (список каналов)

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            setFullScreen(false);
            recyclerView.setVisibility(View.VISIBLE); // Если портретная ориентация то показываем  recyclerView (список каналов)
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            //playbackPosition = player.getCurrentPosition();
            //currentWindow = player.getCurrentWindowIndex();
            player.setPlayWhenReady(false);
        }
        //recyclerView.setLayoutManager(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            //player.seekTo(currentWindow, playbackPosition);
            // player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mWakeLock.acquire();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWakeLock.release();
    }
}

