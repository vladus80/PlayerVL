package com.example.myapplication;

import android.annotation.SuppressLint;

public interface ItemTouchHelperAdapter {
    void onBindViewHolder(MyAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position);

    void onItemMove(int fromPosition, int toPosition);
}

