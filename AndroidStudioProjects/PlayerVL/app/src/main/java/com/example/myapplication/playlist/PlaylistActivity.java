package com.example.myapplication.playlist;

/**Данная активити представляет список (RecyclerView) с плэйлистами*/

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.Channel;
import com.example.myapplication.GroupChannelActivity;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    //private final AppDateBase db = AppDateBase.getInstance(this);
    PlaylistAdapterRecyclerView playlistAdapterRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        PlaylistViewModel viewModel = new ViewModelProvider(this).get(PlaylistViewModel.class);

        RecyclerView recyclerView = findViewById(R.id.playlist_recycler_view);
        FloatingActionButton btnAddPlaylist = findViewById(R.id.floatingAddPlaylist);
        FloatingActionButton btnBackGroup = findViewById(R.id.btnBackGroupActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this); // Создаем Layout
        recyclerView.setLayoutManager(layoutManager); // Уставливаем Layout в recyclerView

        playlistAdapterRecyclerView = new PlaylistAdapterRecyclerView(); // Получаем адаптер

        /* Обновляем плэйлисты при изменении в базе даннных*/
        viewModel.getChannels().observe(this, new Observer<List<Channel>>() {
            @Override
            public void onChanged(List<Channel> channelList) {
                recyclerView.setAdapter(playlistAdapterRecyclerView);
            }
        });

        /*Обновляем каналы  при изменении в базе даннных*/
        viewModel.getPlaylists().observe(this, new Observer<List<PlaylistData>>() {
            @Override
            public void onChanged(List<PlaylistData> playlistData) {
                playlistAdapterRecyclerView.setPlaylistDataList(playlistData);
                recyclerView.setAdapter(playlistAdapterRecyclerView); // Устанавливаем адаптер в recyclerView

            }
        });


        btnBackGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistActivity.this, GroupChannelActivity.class);
                startActivity(intent);

            }
        });
        btnAddPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistActivity.this, PlaylistAddActivity.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PlaylistActivity.this, GroupChannelActivity.class));
    }
}