package com.example.myapplication;

import android.annotation.SuppressLint;

public interface ItemTouchHelperAdapter {
    //void onBindViewHolder(ChannelAdapterRecyclerView.ItemViewHolder holder, @SuppressLint("RecyclerView") int position);

    void onItemMove(int fromPosition, int toPosition);
}

