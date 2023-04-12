package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.myapplication.playlist.PlaylistData;
import com.example.myapplication.playlist.PlaylistActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class GroupChannelActivity extends AppCompatActivity {

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menuItemPlaylist:
                Intent intent = new Intent(getApplicationContext(), PlaylistActivity.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //finishAffinity();
                // Действие при выборе пункта меню 1
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    ListView mListView;
    TextView textViewInfo;
    List<Channel> channelList = null;
    List<ModelItemListViewGroup> mList = null;
    AppDateBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mListView = findViewById(R.id.list_view_group);
        textViewInfo = findViewById(R.id.textViewInfo);
        db = AppDateBase.getInstance(this);

        setTitle("Группы каналов"); // Устанавливаем заголовок активити
        /*Загружаем каналы из базы данных */
        if (channelList == null) {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    channelList = db.channelEntityDAO().getChannelsByActivePlaylist(1);
                    /* Заполняем список каналов в стат. объект, чтобы активность с плеером могла обращаться к каналам*/
                    SingletonListChannel.getInstance().setChannelList(channelList);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                            textViewInfo.setText(getString(R.string.count_channels, channelList.size()));// Отобразим общее количество каналов
                            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                /*Устанавливаем слушатель на клик по строке ListView*/
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {

                                    /*При клике считываем название группы и отправляем интент в активити с пллеером*/
                                    TextView nameGroupTextView = view.findViewById(R.id.item_name_group);
                                    String nameGroup = nameGroupTextView.getText().toString();
                                    Intent intent = new Intent(mListView.getContext(), MainActivity.class);
                                    intent.putExtra("name_group", nameGroup);
                                    mListView.getContext().startActivity(intent);

                                }
                            });
                        }
                    });
                }
            }).start();
        }
    }

    private void initData(){

        List<String> stringList = channelList.stream().map(Channel::getGroupChannel).collect(Collectors.toList()); /*получаем название групп каналов*/
        /*Создаем список групп и сортируем по количеству по убыванию*/
        Map<String, Long> result = stringList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        /*Наполним  структуру ModelItemListViewGroup*/
        List<ModelItemListViewGroup> mList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            mList.add(new ModelItemListViewGroup(key, Long.valueOf(value)));
        }


        AdapterListViewGroup mAdapter = new AdapterListViewGroup(this, mList);

        Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                Integer res = db.channelEntityDAO().getLikes().size();
                System.out.println(res);
                return res;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer res) throws Throwable {
                        mList.add(0,new ModelItemListViewGroup("Избранное", Long.valueOf(res)));
                        /*Устанавливаем структуру в адаптер*/
                        mListView.setAdapter(mAdapter); /*Устанавливаем адаптер в ListView*/
                    }
                });
    }

    @Override
    public void onStop(){
        super.onStop();
        //finish();

    }
}