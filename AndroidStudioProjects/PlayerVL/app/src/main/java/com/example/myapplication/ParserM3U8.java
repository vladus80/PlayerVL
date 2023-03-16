package com.example.myapplication;

import io.lindstrom.m3u8.model.MasterPlaylist;
import io.lindstrom.m3u8.model.MediaPlaylist;
import io.lindstrom.m3u8.parser.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import io.lindstrom.m3u8.model.*;

public class ParserM3U8 {
    private String m3u8Url;


    public ParserM3U8(String m3u8Url) throws IOException {
        this.m3u8Url = m3u8Url;
    }

    public void getList() throws IOException {
        // Получаем содержимое плейлиста M3U8 по URL

        MasterPlaylistParser parser = new MasterPlaylistParser();

// Parse playlist
        MasterPlaylist playlist = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            playlist = parser.readPlaylist(Paths.get(this.m3u8Url));
        }

// Update playlist version
        MasterPlaylist updated = MasterPlaylist.builder()
                .from(playlist)
                .version(2)
                .build();

// Write playlist to standard out
        System.out.println(parser.writePlaylistAsString(updated));
    }
}

