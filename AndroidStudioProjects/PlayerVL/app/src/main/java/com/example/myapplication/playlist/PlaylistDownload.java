package com.example.myapplication.playlist;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.documentfile.provider.DocumentFile;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.Channel;
import com.vladus.parser.VLM3uEntity;
import com.vladus.parser.VlM3uParser;

import java.util.ArrayList;
import java.util.List;

public class PlaylistDownload {


    public static void downloadPlaylist(AppDateBase db,String namePlaylist, String filePlaylist){

        List<Channel> channels = new ArrayList<>();
        long playlistId = db.PlaylistDAO().insert(new PlaylistData(0, namePlaylist, "", filePlaylist));
        List<VLM3uEntity> vlm3uEntities =  VlM3uParser.parse(filePlaylist);
        for (VLM3uEntity vlm3u: vlm3uEntities) {
            channels.add(new Channel(0, vlm3u.getNameChannel(),   //имя канала
                                                            vlm3u.getGroupChannel(), // группа
                                                            vlm3u.getEpgChannelId(), // епг
                                                            vlm3u.getUriChannel(),   // url канала
                                                            vlm3u.getLogoChannel(),  // url логотипа
                                                            Channel.LIKE,            // нравится
                                                            Channel.VISIBLE,         // видимый
                                                            playlistId ));           // id плэйлиста

        }
        db.channelEntityDAO().insertAll(channels);
        System.out.println(channels);

    }

    public static String getFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        if (uri != null) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
                if (documentFile != null && documentFile.exists()) {
                    filePath = documentFile.getUri().getPath();
                }
            } else {
                String[] projection = { MediaStore.Images.Media.DATA };
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(column_index);
                    cursor.close();
                }
            }
        }
        return filePath.split(":")[1];
    }



}
