package com.example.myapplication;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.playlist.PlaylistAdapterRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements ItemTouchHelperAdapter {
    interface OnStateClickListener{
        void onStateClick(Channel channel, int position) throws ExecutionException, InterruptedException;
    }
    private final List<Channel> mDataset; // список данных, которые будут отображаться в RecyclerView
    private final OnStateClickListener onClickListener;
    private   Integer selectedPosition = RecyclerView.NO_POSITION;


    // Конструктор класса MyAdapter, который получает список данных и сохраняет его в приватной переменной mDataset
    public MyAdapter(List<Channel> myDataset, OnStateClickListener onClickListener) {
        this.mDataset = myDataset;
        this.onClickListener = onClickListener;

    }

    @SuppressLint({"ResourceAsColor", "MissingInflatedId"})
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_recycleview_channel, parent, false);
        //v.setBackgroundColor(R.color.white);

        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MyAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {


        // Установка имя канала
        holder.mTextView.setText(mDataset.get(position).getNameChannel());
        // Установка Logo канала через библиотек Glade
        Glide.with(holder.itemView.getContext())
                .load(mDataset.get(position).getUrlLogo())
                .into(holder.mImageView);
        holder.mTextViewGroup.setText(mDataset.get(position).getGroupChannel());
        //holder.imageViewDivider.setImageResource(R.drawable.list_item_selector);


//        if (selectedPosition == position) {
//            holder.itemView.setBackgroundResource(R.drawable.list_item_selector);
//        } else {
//            holder.itemView.setBackgroundResource(com.google.android.material.R.color.background_floating_material_dark);
//        }

        // Слушатель на click, возвращает объект и номер позиции
        Channel channel = mDataset.get(position);
        holder.itemView.setOnClickListener(v -> {

            try {
                onClickListener.onStateClick(channel, position);
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

    public void setSelectedItemPosition(int position) {
        if (selectedPosition != position) {
            selectedPosition = position;
            notifyDataSetChanged();
        }
    }


    /** метод onItemMove() служит для перемещения строк в cyrcleView*/
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(mDataset, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    public Integer getSelectedPosition() {
        return selectedPosition;
    }

    // getItemCount возвращает общее количество элементов в списке данных.
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // MyViewHolder описывает элемент RecyclerView и содержит ссылки на его элементы.
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mImageView;
        public RecyclerView recyclerView;
        public TextView mTextViewGroup;
        public ImageView imageViewDivider;

        public MyViewHolder(View v) {
            super(v);
            mTextView = v.findViewById(R.id.title_text_view);
            mImageView = v.findViewById(R.id.image_view);
            recyclerView = v.findViewById(R.id.recycler_view);
            mTextViewGroup = v.findViewById(R.id.group_text_view);
            //imageViewDivider = v.findViewById(R.id.imageViewDivifer);

        }


    }
}
