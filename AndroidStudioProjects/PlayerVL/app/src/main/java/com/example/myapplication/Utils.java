package com.example.myapplication;


import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MetadataRetriever;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Utils {

    private static  Context context;
    public static File getPlaylistFromRaw(Context mContext) {

        int resId = mContext.getResources().getIdentifier("playlist2", "raw", mContext.getPackageName()); // получение идентификатора ресурса
        InputStream inputStream = mContext.getResources().openRawResource(resId); // создание InputStream
        File file = new File(mContext.getFilesDir(), "playlist2.m3u"); // создание объекта File для сохранения файла
        try {
            FileOutputStream outputStream = new FileOutputStream(file); // создание FileOutputStream
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length); // запись данных в FileOutputStream
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ;
        return file;

    }


    public static void Toast(Context context, String message){

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }


    /*Получаем информацию о медиаданных, метданных*/
    public static ListenableFuture<List<String>> getMediaInfoCodec(Context context, MediaItem mediaItem) {

        Executor executor = Executors.newFixedThreadPool(4);
        SettableFuture<List<String>> future = SettableFuture.create();

        ListenableFuture<TrackGroupArray> trackGroupsFuture = MetadataRetriever.retrieveMetadata( context, mediaItem);

        Futures.addCallback(trackGroupsFuture, new FutureCallback<TrackGroupArray>() {
            @Override
            public void onSuccess(@Nullable TrackGroupArray trackGroups) {
                List<String> infoCodec = new ArrayList<>();
                if (trackGroups != null) {
                    int trackGroupCount = trackGroups.length;
                    for (int i = 0; i < trackGroupCount; i++) {
                        TrackGroup trackGroup = trackGroups.get(i);
                        int trackCount = trackGroup.length;
                        for (int j = 0; j < trackCount; j++) {
                            Format format = trackGroup.getFormat(j);
                            infoCodec.add(String.valueOf(format.codecs ));
                            //System.out.println("FORMAT " + format.width + "x" + format.height);
                        }
                    }
                }
                future.set(infoCodec);
            }

            @Override
            public void onFailure(Throwable t) {
                future.setException(t);
            }
        }, executor);
        return future;
    }
}


