package com.example.myapplication.playlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.myapplication.AppDateBase;

import com.example.myapplication.GroupChannelActivity;
import com.example.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private RecyclerView recyclerView;
    private FloatingActionButton btnSavePlaylist;
    private AppDateBase db = AppDateBase.getInstance(this);
    private FloatingActionButton btnBackGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);


        // Загружаем макет other_layout в объект View
        View otherLayout = LayoutInflater.from(this).inflate(R.layout.item_recycle_playlist, null);

        recyclerView = findViewById(R.id.playlist_recycler_view);
        btnSavePlaylist = findViewById(R.id.floatingAddPlaylist);
        btnBackGroup = findViewById(R.id.btnBackGroupActivity);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        List<PlaylistData> playlistData = new ArrayList<>(db.PlaylistDAO().getAll());
        PlaylistAdapterRecyclerView playlistAdapterRecyclerView = new PlaylistAdapterRecyclerView(playlistData, (playlist, position) -> {

            Toast.makeText(PlaylistActivity.this, playlist.getName() + " Item", Toast.LENGTH_SHORT).show();

        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(playlistAdapterRecyclerView);

        btnBackGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistActivity.this, GroupChannelActivity.class);
                startActivity(intent);
            }
        });

        btnSavePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlaylistActivity.this, PlaylistAddActivity.class);
                startActivity(intent);

            }
        });

    }
}