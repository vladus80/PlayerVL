package com.example.myapplication;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.myapplication.playlist.PlaylistData;
import com.google.android.exoplayer2.source.MediaSource;

import net.bjoernpetersen.m3u.model.M3uEntry;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.vladus.parser.VLM3uEntity;
import com.vladus.parser.VlM3uParser;


@Entity(
        tableName = "channels",
        foreignKeys = @ForeignKey(
                entity = PlaylistData.class,
                parentColumns = "playlist_id",
                childColumns = "playlist_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = {"playlist_id"})
)
public class Channel implements Serializable {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    private long id;
    @ColumnInfo(name = "name")
    private String nameChannel;
    @ColumnInfo(name = "group")
    private String groupChannel;
    @ColumnInfo(name = "epg_id")
    private String epgId;
    @ColumnInfo(name = "uri")
    private String urlChannel;
    @ColumnInfo(name = "logo")
    private String urlLogo;
    @ColumnInfo(name = "like")
    private int like;
    @ColumnInfo(name = "playlist_id")
    private long playlist_id;
    @ColumnInfo(name = "visible")
    private int visible;
    @ColumnInfo(name = "activated")
    private int activated;

    @Ignore
    private PlaylistData playlist;

    @Ignore
    private M3uEntry m3uEntry;
    @Ignore
    private File filePlaylist;
    @Ignore
    private Context context;
    @Ignore
    private MediaSource mediaSource;
    @Ignore
    private List<Channel>channelList;
    @Ignore
    private VLM3uEntity vlm3uEntity;
    @Ignore
    public final static int LIKE = 0;
    @Ignore
    public final static int DISLIKE = 0;
    @Ignore
    public final static int VISIBLE = 1;
    @Ignore
    public final static int INVISIBLE  = 0;
    @Ignore
    public final static int ACTIVATED  = 1;
    @Ignore
    public final static int NOT_ACTIVATED  = 0;
    @Ignore
    private String playlistName;




    public Channel(){};
    public Channel(long id, String nameChannel, String groupChannel,
                   String epgId, String urlChannel, String urlLogo,
                   int like, int visible, long playlist_id, int activated)
    {
        this.nameChannel = nameChannel;
        this.groupChannel = groupChannel;
        this.playlist_id = playlist_id;
        this.epgId = epgId;
        this.urlChannel = urlChannel;
        this.urlLogo = urlLogo;
        this.visible= visible;
        this.like = like;
        this.id = id;
        this.activated = activated;
        this.playlist = playlist;
    }

    @Ignore
    public Channel(String nameChannel, String groupChannel, String epgId,
                                        String urlChannel, String urlLogo) {
        this.nameChannel = nameChannel;
        this.groupChannel = groupChannel;
        this.epgId = epgId;
        this.urlChannel = urlChannel;
        this.urlLogo = urlLogo;
        this.visible= visible;
    }

    @Ignore
    public Channel(String nameChannel,  String urlChannel) {
        this.nameChannel = nameChannel;
        this.urlChannel = urlChannel;
    }

    @Ignore
    public Channel(VLM3uEntity vlm3uEntity) throws MalformedURLException {

        this.vlm3uEntity = vlm3uEntity;
        this.urlLogo = vlm3uEntity.getLogoChannel();
        this.groupChannel = vlm3uEntity.getGroupChannel() ;
        this.epgId = vlm3uEntity.getEpgChannelId();
        this.urlChannel = vlm3uEntity.getUriChannel();
        this.nameChannel = vlm3uEntity.getNameChannel();
    }

//    public Channel(M3uEntry m3uEntry) {
//        this.m3uEntry = m3uEntry;
//        this.urlLogo = m3uEntry.getMetadata().getLogo();
//        this.groupChannel = m3uEntry.getMetadata().get("group-title");
//        this.epgId = m3uEntry.getMetadata().get("tvg-id");
//        this.urlChannel = String.valueOf(m3uEntry.getLocation().getUrl());
//        this.nameChannel = m3uEntry.getTitle();
//
//    }

//    public Channel(File filePlaylist) throws IOException {
//
//        this.filePlaylist = filePlaylist;
//        this.channelList = new ArrayList<>();
//        File file = new File(String.valueOf(filePlaylist));
//
//        List<M3uEntry> m3uEntries = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            m3uEntries = M3uParser.parse(file.toPath());
//        }
//
//        for (M3uEntry entry :  m3uEntries) {
//            channelList.add(new Channel(entry)) ;
//        }
//
//    }
    @Ignore
    public Channel(File filePlaylist) throws IOException {

        this.filePlaylist = filePlaylist;
        this.channelList = new ArrayList<>();
        File file = new File(String.valueOf(filePlaylist));

        List<VLM3uEntity> vlm3uEntities = null;
        PlaylistData playlistEntity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vlm3uEntities = VlM3uParser.parse(file.toPath().toString());
        }

        for (VLM3uEntity entry :  vlm3uEntities) {
            channelList.add(new Channel(entry)) ;
        }

    }

    @Ignore
    public Channel(String pathFile) throws MalformedURLException {

        this.channelList = new ArrayList<>();
        List<VLM3uEntity> vlm3uEntities;
        vlm3uEntities = VlM3uParser.parse(pathFile);

        for (VLM3uEntity entry :  vlm3uEntities) {
            channelList.add(new Channel(entry)) ;

        }
        System.out.println(channelList);
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public long getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id(long playlist_id) {
        this.playlist_id = playlist_id;
    }


    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }


    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }
    public int getActivated() {
        return activated;
    }

    public void setActivated(int activated) {
        this.activated = activated;
    }


    public void setUrlLogo(String urlLogo) {
        this.urlLogo = urlLogo;
    }

    public PlaylistData getPlaylist() {
        return playlist;
    }

    public void setPlaylist(PlaylistData playlist) {
        this.playlist = playlist;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", nameChannel='" + nameChannel + '\'' +
                ", groupChannel='" + groupChannel + '\'' +
                ", epgId='" + epgId + '\'' +
                ", urlChannel='" + urlChannel + '\'' +
                ", urlLogo='" + urlLogo + '\'' +
                ", like=" + like +
               // ", playlist_id=" + playlist_id +
                ", visible=" + visible +
                ", activated=" + activated +
                '}';
    }
}
