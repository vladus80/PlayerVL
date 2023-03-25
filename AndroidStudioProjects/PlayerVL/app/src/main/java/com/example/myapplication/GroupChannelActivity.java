package com.example.myapplication;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GroupChannelActivity extends AppCompatActivity {

    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setTitle("Группы каналов");


        File file = Utils.getPlaylistFromRaw(this );
        List<Channel> channelList = null;
        try {
            channelList = new Channel(file).getChannelList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //List listrGtoup = channelList.;

        List<String> stringList = channelList.stream().map(Channel::getGroupChannel).collect(Collectors.toList());

        Map<String, Long> result = stringList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        mListView = findViewById(R.id.list_view_group);
        List<ModelItemListViewGroup> mList = new ArrayList<>();


        for (Map.Entry<String, Long> entry : result.entrySet()) {
            String key = entry.getKey();
            Long value = entry.getValue();
            System.out.println(key + " = " + value);
            mList.add(new ModelItemListViewGroup(key, Long.valueOf(value)));
        }

        AdapterListViewGroup mAdapter = new AdapterListViewGroup(this,  mList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                TextView nameGroupTextView = view.findViewById(R.id.item_name_group);
                String nameGroup = nameGroupTextView.getText().toString();
                Intent intent = new Intent(mListView.getContext(), MainActivity.class);
                intent.putExtra("name_group", nameGroup);
                mListView.getContext().startActivity(intent);
            }
        });

    }
}