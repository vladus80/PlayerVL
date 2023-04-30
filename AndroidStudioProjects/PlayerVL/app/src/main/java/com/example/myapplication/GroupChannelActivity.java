package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private GroupChannelViewModel  viewModel;
    private ListView mListView;
    private TextView textViewInfo;
    private List<ModelItemListViewGroup> mListItemGroupAndCountChannels = null;
    private TextView nameGroupTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        mListView = findViewById(R.id.list_view_group);
        textViewInfo = findViewById(R.id.textViewInfo);
       // db = AppDateBase.getInstance(this);
        viewModel = new ViewModelProvider(this).get(GroupChannelViewModel.class);

        setTitle("Группы каналов"); // Устанавливаем заголовок активити

        /*Загружаем каналы из базы данных */
        viewModel.getCountChannelsInGroup();
        viewModel.getchannelsActiveList().observe(this, new Observer<List<Channel>>() {
            @Override
            public void onChanged(List<Channel> channels) {
                viewModel.getCountChannelsInGroup();

                if(channels !=null){
                    mListItemGroupAndCountChannels = initData(channels); //
                    textViewInfo.setText(getString(R.string.count_channels, channels.size()));// Отобразим общее количество каналов


                    /* Адаптер для ListView */
                    AdapterListViewGroup mAdapter = new AdapterListViewGroup(
                            GroupChannelActivity.this, mListItemGroupAndCountChannels);

                    /* Добавляем в структуру (map) категорию избранное с установкой колва каналов */
                    mListItemGroupAndCountChannels.add(0,new ModelItemListViewGroup("" +
                            "Избранное", Long.valueOf(viewModel.getCountChannelsInFavorit())));

                    /*Устанавливаем адаптер в ListView*/
                    mListView.setAdapter(mAdapter); /*Устанавливаем адаптер в ListView*/

                    Log.d("updateLokeInfo", "updateLokeInfo");
                }

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /*Устанавливаем слушатель на клик по строке ListView*/
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                /*При клике считываем название группы и отправляем интент в активити с пллеером*/
                nameGroupTextView = view.findViewById(R.id.item_name_group);
                String nameGroup = nameGroupTextView.getText().toString();
                Intent intent = new Intent(mListView.getContext(), MainActivity.class);
                intent.putExtra("name_group", nameGroup);
                mListView.getContext().startActivity(intent);

            }
        });


    }

    private List<ModelItemListViewGroup> initData(List<Channel> channelList) {

        List<String> stringList = channelList.stream().map(Channel::getGroupChannel)
                .collect(Collectors.toList()); /*получаем название групп каналов*/
        /*Создаем список групп и сортируем по количеству по убыванию*/
        Map<String, Long> result = stringList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        /*Наполним  структуру ModelItemListViewGroup*/
        List<ModelItemListViewGroup> mListItemGroupAndCountChannels = new ArrayList<>();
        for (Map.Entry<String, Long> entry : result.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            mListItemGroupAndCountChannels.add(new ModelItemListViewGroup(key, Long.valueOf(value)));
        }
     return    mListItemGroupAndCountChannels;
    }

    @Override
    public void onStop(){
        super.onStop();
        //finish();

    }


    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getCountChannelsInGroup();
    }
}