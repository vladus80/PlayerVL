package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnClickListenerBtnLike, OnClickListenerItem {

    private static final long LONG_PRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout();
    private PlayerView playerView;
    private RecyclerView recyclerView;
    private SimpleExoPlayer player;
    private PowerManager.WakeLock mWakeLock; //Чтобы держать устройство включенным
    private ItemTouchHelper itemTouchHelper; // перетаскивать каналы в recycleView
    private ChannelAdapterRecyclerView channelAdapterRecyclerView;
    private MainActivityViewModel viewModel;

    private LinearLayout layoutPanelChannel;
    private Spinner spinnerGroup;
    private String nameGroup;
    private CustomForwardingPlayer forwardingPlayer;
    private PlayerManager playerManager = null;
    private int stateDownloadGroups = 0;
    private ArrayAdapter<String> adapterSpinner;
    private int positionItem = 0;
    private int countLayout = 0;


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
        layoutPanelChannel = findViewById(R.id.layout_panel_channel);
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView = findViewById(R.id.recycler_view);
            spinnerGroup = findViewById(R.id.spinner_group_port);

        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView = findViewById(R.id.recyclerView_land);
            spinnerGroup = findViewById(R.id.spinner_group);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            layoutPanelChannel.layout(screenWidth + recyclerView.getWidth(), 0,
                    screenWidth + recyclerView.getWidth() * 2, recyclerView.getHeight());

        }

        recyclerView.setItemAnimator(null); // отключить анимацию при обновлении item
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Если приходит с активити групп каналов то устанавливаем имя группы в LiveData
        if (getIntent() != null) {
            nameGroup = getIntent().getStringExtra("name_group");

        }


        // Заполняем спиннер
        viewModel.getGroupsLD().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> groups) {


                if (stateDownloadGroups == 0) {
                    groups.add(0, "Избранное");
                    adapterSpinner = new ArrayAdapter<>(
                            MainActivity.this, R.layout.simple_spinner_item,
                            groups);
                    spinnerGroup.setAdapter(adapterSpinner);
                    stateDownloadGroups++;
                    int spinnerPosition = adapterSpinner.getPosition(nameGroup); // получаем позицию элемента в адаптере
                    spinnerGroup.setSelection(spinnerPosition); // устанавливаем выбранным элемент с найденной позицией

                    if (getIntent() != null) {
                        nameGroup = getIntent().getStringExtra("name_group");
                        Log.d("nameGroupMainActivity-106", nameGroup);
                        //viewModel.setGroupName(nameGroup);

                    }
                }

            }
        });


        // Подписываемся на изменение имени группы в LiveData (Основная работа здесь)
        viewModel.getChannelsLD().observe(this, new Observer<List<Channel>>() {
            @Override
            public void onChanged(List<Channel> channelList) {

                runPlayer(channelList); // Инициализируем плеер и заполняем данными при зменеии группы
            }
        });


        // При выборе значения в спинере устанавливаем новое значение имени группы в LiveData
        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {

                nameGroup = adapterView.getItemAtPosition(pos).toString();
                viewModel.setGroupName(nameGroup);
                Log.d("CountCallInit1", "Count: " + countLayout);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                recyclerView.requestFocus(); // Если ничего не выбрано то фокус на список  каналов
            }
        });

        spinnerGroup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recyclerView.requestFocus();
                }
            }
        });
    }

    private void runPlayer(List<Channel> channels) {

        countLayout++;

        if (playerManager == null) {
            //Log.d("CountCallInit1","Count: " + countLayout);
            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
            playerManager = new PlayerManager(channels, MainActivity.this);
            player = playerManager.getPlayer();
            playerView.setPlayer(player);
            recyclerView.setLayoutManager(layoutManager);
            channelAdapterRecyclerView = new ChannelAdapterRecyclerView(channels,
                    MainActivity.this,
                    MainActivity.this);
        } else {
            //Log.d("CountCallInit2","Count: " + countLayout);
            channelAdapterRecyclerView.setChannels(channels);
            player.setMediaItems(playerManager.getMediaItems(channels));

        }
        recyclerView.setAdapter(channelAdapterRecyclerView);
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
        player.prepare();
        player.setPlayWhenReady(true);
        recyclerView.requestFocus();
        playerView.setUseController(false);

    }

    @Override
    public void onClickBtnLike(Channel channel) {
        viewModel.setLike(channel);
    }

    @Override
    public void onClickItem(int position) {
        player.seekTo(position, 0);
        player.prepare();
        player.setPlayWhenReady(true);

        Channel channel = (Channel) Objects.requireNonNull(player.getCurrentMediaItem().localConfiguration).tag;
        Log.d("getCurrentMediaItem", channel.toString());

    }

    @Override
    public void onClickItem(Channel channel) {
        viewModel.setActivated(channel);

    }

    @Override
    public int getPosition(int position) {
        positionItem = position;
        return positionItem;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:

                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                //Toast.makeText(this, ""+getCurrentFocus().toString(), Toast.LENGTH_SHORT).show();
                if (getCurrentFocus() == spinnerGroup) {
                    playerView.requestFocus();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:

                Log.d("ItemPositin", "" + positionItem);
                if (layoutPanelChannel.getVisibility() == View.INVISIBLE) {

                    layoutPanelChannel.startAnimation(translateAnimation(1000, 0, 500));
                    layoutPanelChannel.setVisibility(View.VISIBLE);
                    playerView.setAnimation(scaleAnimation(1f, 0.54f, 1, 0.54f, 500));
                    recyclerView.requestFocus();

                    //recyclerView.addItemDecoration();

                    //recyclerView.scrollToPosition(positionItem);

                } else {
                    recyclerView.scrollToPosition(positionItem);
                    layoutPanelChannel.startAnimation(translateAnimation(0, 1000, 500));
                    playerView.setAnimation(scaleAnimation(0.54f, 1f, 0.54f, 1f, 500));
                    layoutPanelChannel.setVisibility(View.INVISIBLE);

                }

                return true;

            case KeyEvent.KEYCODE_DPAD_RIGHT:

                spinnerGroup.performClick();

            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private Animation translateAnimation(int fromDelta, int toDelta, int duration) {

        Animation animation = new TranslateAnimation(fromDelta, toDelta, 0, 0);
        animation.setDuration(duration);
        animation.setFillAfter(false);
        return animation;
    }

    private ScaleAnimation scaleAnimation(float fromX, float toX, float fromY, float toY, int duration) {

        ScaleAnimation animation = new ScaleAnimation(
                fromX, toX, // Начальный и конечный масштаб по оси X
                fromY, toY, // Начальный и конечный масштаб по оси Y
                Animation.RELATIVE_TO_PARENT, 0f, // Ось X относительно центра виджета
                Animation.RELATIVE_TO_PARENT, 0f // Ось Y относительно центра виджета
        );
        animation.setDuration(duration);
        animation.setFillAfter(true);
        return animation;
    }

    /* В зависимости от ориентации прячем заголовок и разворачиваем на весь экран*/
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            spinnerGroup.setVisibility(View.GONE);

            //Toast.makeText(this, "ORIENTATION_LANDSCAPE", Toast.LENGTH_SHORT).show();
            recyclerView.setVisibility(View.GONE); // Если ландшафтная ориентация то прячем  recyclerView (список каналов)

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            recyclerView.setVisibility(View.VISIBLE); // Если портретная ориентация то показываем  recyclerView (список каналов)
            spinnerGroup.setVisibility(View.VISIBLE);
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
        if (null != player) {

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

        if (null != player) {

            player.stop();
            player.release();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}

