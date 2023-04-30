package com.example.myapplication;

public interface OnClickListenerItem {
    void onClickItem(int position);
    void onClickItem(Channel channel);
    int getPosition(int position);
}

