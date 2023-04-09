package com.example.myapplication.playlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.R;

import java.util.List;

public class PlaylistAdapterRecyclerView extends RecyclerView.Adapter<PlaylistAdapterRecyclerView.ViewHolderPlaylistRecyclerView> {

    private Handler handler = new Handler(Looper.getMainLooper());

    private  List<PlaylistData> playlistDataList; // список данных, которые будут отображаться в RecyclerView

    //private Integer selectedPosition = RecyclerView.NO_POSITION;
    // Конструктор класса MyAdapter, который получает список данных
    public PlaylistAdapterRecyclerView(List<PlaylistData> playlistDataList) {
        this.playlistDataList = playlistDataList;
    }

    public PlaylistAdapterRecyclerView(){}

    public void setPlaylistDataList(List<PlaylistData> playlistDataList) {
        this.playlistDataList = playlistDataList;
        notifyDataSetChanged();
    }

    @Override
    public PlaylistAdapterRecyclerView.ViewHolderPlaylistRecyclerView onCreateViewHolder(ViewGroup parent,
                                                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recyclerview_playlist, parent, false);

        return new PlaylistAdapterRecyclerView.ViewHolderPlaylistRecyclerView(v);
    }
    @Override
    public void onBindViewHolder(ViewHolderPlaylistRecyclerView holder, @SuppressLint("RecyclerView") int position) {

        AppDateBase db = AppDateBase.getInstance(holder.itemView.getContext());

        // Установка имя группы
        holder.textViewNamePlaylist.setText(playlistDataList.get(position).getName());
        // Если плэйлист неактивен то выделим красным
        if(playlistDataList.get(position).getActive() == 0){
           holder.linearLayout.setBackgroundColor(Color.rgb(94, 10, 10));
        }else {
            holder.linearLayout.setBackgroundColor(Color.rgb(90, 125, 87));
        }

        //Установка колво каналов в группе

        new Thread(new Runnable() {
            @Override
            public void run() {

                int count = db.channelEntityDAO().
                        getSizePlaylist(playlistDataList.get(position).getId());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemCountChannelsTextView.setText(String.valueOf(count));
                    }
                });
            }
        }).start();




        // Слушатель на click, возвращает объект и номер позиции
        PlaylistData playlistData = playlistDataList.get(position);
        holder.itemView.setOnClickListener(v -> {

            senData(holder.itemView.getContext(), PlaylistAddActivity.class, "playlist", playlistData);
            //notifyDataSetChanged();
//            int previousSelectedPosition = selectedPosition;
//            selectedPosition = holder.getAdapterPosition();
//            notifyItemChanged(previousSelectedPosition);
//            notifyItemChanged(selectedPosition);

        });

    }

    /*Когда кликаем на item списка то передаем данные о объекте в item и переходим в активность редактирования*/
    private void senData(Context context, Class<? extends Activity> activityClass, String key, PlaylistData playlistData) {
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(key, playlistData);
        context.startActivity(intent);

    }

    // getItemCount возвращает общее количество элементов в списке данных.
    @Override
    public int getItemCount() {
        return playlistDataList.size();
    }

    // MyViewHolder описывает элемент RecyclerView и содержит ссылки на его элементы.
    public static class ViewHolderPlaylistRecyclerView extends RecyclerView.ViewHolder {
        public TextView textViewNamePlaylist;
        public RecyclerView recyclerView;
        public TextView itemCountChannelsTextView;
        //public Switch switchPlaylistActive;
        public LinearLayout linearLayout;


        public ViewHolderPlaylistRecyclerView(View v) {
            super(v);
            textViewNamePlaylist = v.findViewById(R.id.itemNamePlaylistTextView);
            recyclerView = v.findViewById(R.id.playlist_recycler_view);
            itemCountChannelsTextView = v.findViewById(R.id.itemCountChannelsTextView);
            //switchPlaylistActive = v.findViewById(R.id.switchActivePlaylist);
            linearLayout = v.findViewById(R.id.layout_item_playlist);


        }
    }
}
