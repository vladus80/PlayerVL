package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.example.myapplication.playlist.PlaylistData;
import com.example.myapplication.playlist.PlaylistActivity;


public class GroupChannelActivity extends AppCompatActivity {



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
                startActivity(intent);
                // Действие при выборе пункта меню 1
                return true;
            case R.id.menu_item2:
                // Действие при выборе пункта меню 2
                return true;
            case R.id.menu_item3:
                // Действие при выборе пункта меню 3
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }




    ListView mListView;
    TextView textViewInfo;
    List<Channel> channelList = null;
    AppDateBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mListView = findViewById(R.id.list_view_group);
        textViewInfo = findViewById(R.id.textViewInfo);
        db = AppDateBase.getInstance(this);

        /*Загружаем каналы из базы данных */
           if(channelList == null){
               channelList = db.channelEntityDAO().getAll();
               /* Заполняем список каналов в стат. объект, чтобы активность с плеером мога обращаться к каналам*/
               SingletonListChannel.getInstance().setChannelList(channelList);
           }

        List<String> stringList = channelList.stream().map(Channel::getGroupChannel).collect(Collectors.toList()); /*получаем название групп каналов*/
        setTitle("Группы каналов"); // Устанавливаем заголовок активити
        textViewInfo.setText(getString(R.string.count_channels, channelList.size()) );// Отобразим общее количество каналов

        /*Создаем список групп и сортируем по количеству по убыванию*/
        Map<String, Long> result = stringList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        /*Наполнием  структуру ModelItemListViewGroup*/
        List<ModelItemListViewGroup> mList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            mList.add(new ModelItemListViewGroup(key, Long.valueOf(value)));
        }

        /*Устанавливаем структуру в адапртер*/
        AdapterListViewGroup mAdapter = new AdapterListViewGroup(this,  mList);
        mListView.setAdapter(mAdapter); /*Устанавливаем адаптер в ListView*/

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
    /**/
    private void setDataInSingleton(String group){

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDateBase db = Room.databaseBuilder(getApplicationContext(),AppDateBase.class, "channel.db").build();
                ChannelEntityDAO channelEntityDAO = db.channelEntityDAO();
                List<Channel> channels =  channelEntityDAO.getByGroup(group);
                SingletonListChannel.getInstance().setChannelList(channels);
            }
        }).start();

    }

    /*Загружает каналы в базу данных*/
    private void downLoadBaseData(List<Channel> channelList){

        new Thread(new Runnable() {
            @Override
            public void run() {
                ChannelEntityDAO channelEntityDAO = db.channelEntityDAO();
                channelEntityDAO.insertAll(channelList);
            }
        }).start();

    }

    private boolean tableExists(SQLiteDatabase sqLiteDatabase, String table){
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen() || table == null){
            return false;
        }
        int count = 0;
        String[] args = {"table",table};
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type=? AND name=?",args,null);
        if (cursor.moveToFirst()){
            count = cursor.getInt(0);
        }
        cursor.close();
        return count > 0;
    }

}