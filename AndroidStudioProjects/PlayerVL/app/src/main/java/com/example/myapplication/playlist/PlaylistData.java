package com.example.myapplication.playlist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;


@Entity(tableName = "playlist")
public class PlaylistData implements Serializable {

    @ColumnInfo(name = "playlist_id")
    @PrimaryKey(autoGenerate = true)
    long id;
    @ColumnInfo(name = "playlist_name")
    String name;
    String provider;
    String path;
    int active;




    public PlaylistData(long id, String name, String provider, String path, int active) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.path = path;
        this.active = active;
    }

    public PlaylistData() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }


    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                ", path='" + path + '\'' +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaylistData)) return false;
        PlaylistData that = (PlaylistData) o;
        return getId() == that.getId() && getActive() == that.getActive() && getName().equals(that.getName()) && Objects.equals(getProvider(), that.getProvider()) && getPath().equals(that.getPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getProvider(), getPath(), getActive());
    }


}
