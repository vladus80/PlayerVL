package com.example.myapplication;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import net.bjoernpetersen.m3u.M3uParser;
import net.bjoernpetersen.m3u.model.M3uEntry;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Channel {
    private String nameChannel;
    private String groupChannel;
    private String epgId;
    private URL urlChannel;
    private String urlLogo;
    private M3uEntry m3uEntry;
    private File filePlaylist;
    private Context context;
    private MediaSource mediaSource;
    private List<Channel>channelList;

    public Channel(String nameChannel, String groupChannel, String epgId, URL urlChannel, String urlLogo) {
        this.nameChannel = nameChannel;
        this.groupChannel = groupChannel;
        this.epgId = epgId;
        this.urlChannel = urlChannel;
        this.urlLogo = urlLogo;
    }

    public Channel(String nameChannel,  URL urlChannel) {
        this.nameChannel = nameChannel;
        this.urlChannel = urlChannel;
    }

    public Channel(M3uEntry m3uEntry) {
        this.m3uEntry = m3uEntry;
        this.urlLogo = m3uEntry.getMetadata().getLogo();
        this.groupChannel = m3uEntry.getMetadata().get("group-title");
        this.epgId = m3uEntry.getMetadata().get("tvg-id");
        this.urlChannel = m3uEntry.getLocation().getUrl();
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

    public URL getUrlChannel() {
        return urlChannel;
    }

    public void setUrlChannel(URL urlChannel) {
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
