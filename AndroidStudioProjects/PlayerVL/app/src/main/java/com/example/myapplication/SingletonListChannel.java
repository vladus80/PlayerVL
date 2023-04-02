package com.example.myapplication;

import android.hardware.lights.LightsManager;

import java.util.ArrayList;
import java.util.List;

public class SingletonListChannel {


        private static SingletonListChannel instance = null;
        private static List<Channel> channelList = null;

        private SingletonListChannel() {}
        public static SingletonListChannel getInstance() {
            if (instance == null) {
                instance = new SingletonListChannel();
            }
            return instance;
        }


    public List<Channel> getChannelList() {
        return new ArrayList<>(channelList);
    }
    public void setChannelList(List<Channel> channelList) {
            this.channelList = channelList;
    }
}
