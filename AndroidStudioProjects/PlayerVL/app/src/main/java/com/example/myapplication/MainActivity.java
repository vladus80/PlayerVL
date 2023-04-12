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
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

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
    private ImageButton btnStarLike;
    private SimpleExoPlayer player;
    private PowerManager.WakeLock mWakeLock; //Чтобы держать устройство включенным
    private ItemTouchHelper itemTouchHelper; // перетаскивать каналы в recycleView
    private AppDateBase db = AppDateBase.getInstance(this);
    private List<Channel> channels;
    private Handler handler = new Handler(Looper.getMainLooper());
    private String nameGroupFromIntent;
    private ChannelAdapterRecyclerView channelAdapterRecyclerView;

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


        db.channelEntityDAO().getAllChannels().observe(this, new Observer<List<Channel>>() {
            @Override
            public void onChanged(List<Channel> channelList) {

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (Channel channel: channelList ) {
//                            if (db.channelEntityDAO().isActive(channel.getId())==1) {
//                                db.channelEntityDAO().updateLike(channel.getId(), 0 );
//
//                            }else{
//                                db.channelEntityDAO().updateLike(channel.getId(), 1 );
//
//                            }
//                        }
//                    }
//                }).start();

            }
        });

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

        List<Channel> channelList = SingletonListChannel.getInstance().getChannelList();
        nameGroupFromIntent = getIntent().getStringExtra("name_group");

        if(nameGroupFromIntent.equals("Избранное")){

            Observable.fromCallable(new Callable<List<Channel>>() {
                @Override
                public List<Channel> call() throws Exception {

                    List<Channel> result = db.channelEntityDAO().getLikes();
                    channels = db.channelEntityDAO().getLikes();
                    return result;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Consumer<List<Channel>>() {
                        @Override
                        public void accept(List<Channel> channelList) throws Throwable {
                            channelAdapterRecyclerView = new ChannelAdapterRecyclerView(channelList,
                                    MainActivity.this::onClickBtnLike, MainActivity.this::onClickItem);
                            String playerEx = "PlayerManager";
                            if(playerEx.equals("PlayerManager")){
                                PlayerManager playerManager = new PlayerManager(channelList, MainActivity.this);

                                player = playerManager.getPlayer();
                                player.addListener(listener);
                                initPlayerPlay(player);
                            } else if(playerEx.equals("PlayerInit")) {
                                player = InitPlayer.initPlayer(MainActivity.this, new SimpleExoPlayer.Builder(MainActivity.this).build(),
                                        channels, listener, false, false, false);
                                initPlayerPlay(player);
                            }
                        }
                    })
                    .subscribe();

        }else {
            channels = channelList.stream().filter(channel -> channel.getGroupChannel()
                            .equals(nameGroupFromIntent))
                            .collect(Collectors.toList());
            channelAdapterRecyclerView = new ChannelAdapterRecyclerView(channels, this, this);
            String playerEx = "PlayerManager";
            if(playerEx.equals("PlayerManager")){
                PlayerManager playerManager = new PlayerManager(channels, this);

                player = playerManager.getPlayer();
                player.addListener(listener);
                initPlayerPlay(player);
            } else if(playerEx.equals("PlayerInit")) {
                player = InitPlayer.initPlayer(this, new SimpleExoPlayer.Builder(this).build(),
                        channels, listener, false, false, false);
               // player = playerManager.getPlayer();
                player.addListener(listener);
                initPlayerPlay(player);
            }

        }

        // Если находимся в портретном режиме, то  заполняем список каналов ListviewRecycle
//        if (recyclerView != null) {
//            recyclerView.setLayoutManager(layoutManager);
//            // Обработка клика  на строке ListviewRecycle (переключаем канал при клике на строку)
//            Log.d("onPositionDiscontinuity", Utils.getMediaInfoCodec(this, player.getCurrentMediaItem()).get().toString());
//            MyAdapter adapter = new MyAdapter(channels, (channel, position) -> {
//                player.seekTo(position, 0);
//                player.setPlayWhenReady(true);
//                //Log.d("onPositionDiscontinuity", Utils.getMediaInfoCodec(this, player.getCurrentMediaItem()).get().toString());
//
//            });
//
//            ChannelAdapterRecyclerView channelAdapterRecyclerView = new ChannelAdapterRecyclerView(channels, this, this);
//
//            recyclerView.setAdapter(adapter);
//            recyclerView.setAdapter(channelAdapterRecyclerView);
//
//            ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(channelAdapterRecyclerView);
//           // ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(adapter);
//            itemTouchHelper = new ItemTouchHelper(callback);
//            itemTouchHelper.attachToRecyclerView(recyclerView);
//        }

    }

    private void initPlayerPlay(SimpleExoPlayer player){

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        if(recyclerView != null){
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(channelAdapterRecyclerView);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onClickBtnLike(int position, ImageButton imageButton) {
        //Toast.makeText(this, "position in Click: " + position, Toast.LENGTH_SHORT).show();

        Observable.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        Boolean result = false;
                        Channel channel = channels.get(position);
                        //Log.d("LickeResul", String.valueOf(db.channelEntityDAO().isActive(channels.get(position).getId())));
                        if (db.channelEntityDAO().isActive(channel.getId())==1) {
                            db.channelEntityDAO().updateLike(channel.getId(), 0 );

                            result = true;
                        }else{
                            db.channelEntityDAO().updateLike(channel.getId(), 1 );
                            result = false;
                        }
                        return result;
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean result) throws Throwable {

                        if (result == true){
                            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.star_empt));
                        }else {
                            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.star_full));
                        }

                    }

                });

    }

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

