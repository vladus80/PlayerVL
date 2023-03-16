package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class MyViewHolder extends RecyclerView.ViewHolder {
    public ImageView imageView;
    public TextView textView;
    public MyViewHolder(View view) {
        super(view);
        imageView = view.findViewById(R.id.image_view);
        textView = view.findViewById(R.id.title_text_view);
    }
}

