package com.example.myapplication;


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    //private  Context context;
    public static File getPlaylistFromRaw(Context mContext) {

        InputStream inputStream = mContext.getResources().openRawResource(R.raw.playlistizi); // создание InputStream
        File file = new File(mContext.getFilesDir(), "playlist_file.m3u8"); // создание объекта File для сохранения файла
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
        return file;
    }



    /*Получаем информацию о медиаданных, метданных*/
   /* public static ListenableFuture<List<String>> getMediaInfoCodec(Context context, MediaItem mediaItem) {

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
    }*/
}


