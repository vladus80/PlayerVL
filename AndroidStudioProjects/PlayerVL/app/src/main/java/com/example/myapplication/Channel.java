package com.example.myapplication;

import android.content.Context;

import com.google.android.exoplayer2.source.MediaSource;

import net.bjoernpetersen.m3u.M3uParser;
import net.bjoernpetersen.m3u.model.M3uEntry;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.vladus.parcer.VLM3uEntity;
import com.vladus.parcer.VlM3uParcer;


public class Channel {
    private String nameChannel;
    private String groupChannel;
    private String epgId;
    private String urlChannel;
    private String urlLogo;
    private M3uEntry m3uEntry;
    private File filePlaylist;
    private Context context;
    private MediaSource mediaSource;
    private List<Channel>channelList;
    private VLM3uEntity vlm3uEntity;

    public Channel(String nameChannel, String groupChannel, String epgId, String urlChannel, String urlLogo) {
        this.nameChannel = nameChannel;
        this.groupChannel = groupChannel;
        this.epgId = epgId;
        this.urlChannel = urlChannel;
        this.urlLogo = urlLogo;
    }

    public Channel(String nameChannel,  String urlChannel) {
        this.nameChannel = nameChannel;
        this.urlChannel = urlChannel;
    }

    public Channel(VLM3uEntity vlm3uEntity) throws MalformedURLException {

        this.vlm3uEntity = vlm3uEntity;
        this.urlLogo = vlm3uEntity.getLogoChannel();
        this.groupChannel = vlm3uEntity.getGroupChannel() ;
        this.epgId = vlm3uEntity.getEpgChannelId();
        //this.urlChannel = new vlm3uEntity.getUriChannel();
        this.nameChannel = vlm3uEntity.getNameChannel();
    }

    public Channel(M3uEntry m3uEntry) {
        this.m3uEntry = m3uEntry;
        this.urlLogo = m3uEntry.getMetadata().getLogo();
        this.groupChannel = m3uEntry.getMetadata().get("group-title");
        this.epgId = m3uEntry.getMetadata().get("tvg-id");
        this.urlChannel = String.valueOf(m3uEntry.getLocation().getUrl());
        this.nameChannel = m3uEntry.getTitle();

    }

    public Channel(File filePlaylist) throws IOException {

        this.filePlaylist = filePlaylist;
        this.channelList = new ArrayList<>();
        File file = new File(String.valueOf(filePlaylist));

        List<M3uEntry> m3uEntries = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            m3uEntries = M3uParser.parse(file.toPath());
        }

        for (M3uEntry entry :  m3uEntries) {
            channelList.add(new Channel(entry)) ;
        }

    }


    public Channel(String pathFile) throws MalformedURLException {

        this.channelList = new ArrayList<>();
        List<VLM3uEntity> vlm3uEntities;
        vlm3uEntities = VlM3uParcer.parce(pathFile);

        for (VLM3uEntity entry :  vlm3uEntities) {
            channelList.add(new Channel(entry)) ;
        }

    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public String getNameChannel() {
        return nameChannel;
    }

    public String getUrlLogo() {
        return urlLogo;
    }

    public void setNameChannel(String nameChannel) {
        this.nameChannel = nameChannel;
    }

    public String getGroupChannel() {
        return groupChannel;
    }

    public void setGroupChannel(String groupChannel) {
        this.groupChannel = groupChannel;
    }

    public String getEpgId() {
        return epgId;
    }

    public void setEpgId(String epgId) {
        this.epgId = epgId;
    }

    public String getUrlChannel() {
        return urlChannel;
    }

    public void setUrlChannel(String urlChannel) {
        this.urlChannel = urlChannel;
    }

    public M3uEntry getM3uEntry() {
        return m3uEntry;
    }

    public void setM3uEntry(M3uEntry m3uEntry) {
        this.m3uEntry = m3uEntry;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "nameChannel='" + nameChannel + '\'' +
                ", groupChannel='" + groupChannel + '\'' +
                ", epgId='" + epgId + '\'' +
                ", logo='" + urlLogo + '\'' +
                ", urlChannel=" + urlChannel + "}\r\n";
    }
}
