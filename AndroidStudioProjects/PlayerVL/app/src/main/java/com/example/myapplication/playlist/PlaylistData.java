package com.example.myapplication.playlist;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;


@Entity(tableName = "playlist")
public class PlaylistData implements Serializable {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    long id;
    String name;
    String provider;
    String path;


    public PlaylistData(long id, String name, String provider, String path) {
        this.id = id;
        this.name = name;
        this.provider = provider;
        this.path = path;
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

    @Override
    public String toString() {
        return "Playlist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistData that = (PlaylistData) o;
        return id == that.id && name.equals(that.name) && provider.equals(that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, provider);
    }
}
