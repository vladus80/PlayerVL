package com.example.myapplication.playlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class PlaylistAdapterRecyclerView  extends RecyclerView.Adapter<PlaylistAdapterRecyclerView.ViewHolderPlaylistRecyclerView>  {
    interface OnStateClickListener{
        void onStateClick(PlaylistData PlaylistData, int position) throws ExecutionException, InterruptedException;
    }
    private final List<PlaylistData> mDataset; // список данных, которые будут отображаться в RecyclerView
    private final OnStateClickListener onClickListener;
    private   Integer selectedPosition = RecyclerView.NO_POSITION;


    // Конструктор класса MyAdapter, который получает список данных и сохраняет его в приватной переменной mDataset
    public PlaylistAdapterRecyclerView(List<PlaylistData> myDataset, OnStateClickListener onClickListener) {
        this.mDataset = myDataset;
        this.onClickListener = onClickListener;

    }

    //@SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    public PlaylistAdapterRecyclerView.ViewHolderPlaylistRecyclerView onCreateViewHolder(ViewGroup parent,
                                                                               int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycle_playlist, parent, false);
        //v.setBackgroundColor(R.color.white);

        return new PlaylistAdapterRecyclerView.ViewHolderPlaylistRecyclerView(v);
    }



    @Override
    public void onBindViewHolder(ViewHolderPlaylistRecyclerView holder, @SuppressLint("RecyclerView") int position) {


        // Установка имя канала
        holder.textViewNamePlaylist.setText(mDataset.get(position).getName());
        // Установка Logo канала через библиотек Glade

        holder.imageButtonSettingPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), mDataset.get(position).getPath(), Toast.LENGTH_SHORT).show();
            }
        });

        // Слушатель на click, возвращает объект и номер позиции
        PlaylistData playlistData = mDataset.get(position);
        holder.itemView.setOnClickListener(v -> {

            try {
                onClickListener.onStateClick(playlistData, position);
                senData(holder.itemView.getContext(),PlaylistAddActivity.class, "playlist", playlistData );

            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
            //v.setBackgroundResource(R.drawable.list_item_selector); // установка селектора

            int previousSelectedPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelectedPosition);
            notifyItemChanged(selectedPosition);
            setSelectedItemPosition(holder.getAdapterPosition());

        });


    }

    private void senData(Context context, Class<? extends Activity> activityClass, String key, PlaylistData playlistData){
        Intent intent = new Intent(context, activityClass);
        intent.putExtra(key, playlistData );
        context.startActivity(intent);


    }

    public void setSelectedItemPosition(int position) {
        if (selectedPosition != position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }

    // getItemCount возвращает общее количество элементов в списке данных.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // MyViewHolder описывает элемент RecyclerView и содержит ссылки на его элементы.
    public static class ViewHolderPlaylistRecyclerView extends RecyclerView.ViewHolder {
        public TextView textViewNamePlaylist;
        public RecyclerView recyclerView;
        public TextView textViewCountChannels;
        public ImageButton imageButtonSettingPlaylist;


        public ViewHolderPlaylistRecyclerView(View v) {
            super(v);
            textViewNamePlaylist = v.findViewById(R.id.itemNamePlaylistTextView);
            recyclerView = v.findViewById(R.id.playlist_recycler_view);
            textViewCountChannels = v.findViewById(R.id.itemCountChannelsTextView);
            imageButtonSettingPlaylist = v.findViewById(R.id.settingPlaylistImageBut);

        }


    }
}
