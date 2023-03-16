package com.example.myapplication;


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class  Common {



    public  Common() {

    }

    public static File getPlaylistFromRaw(Context mContext) {

        int resId = mContext.getResources().getIdentifier("playlist", "raw", mContext.getPackageName()); // получение идентификатора ресурса
        InputStream inputStream = mContext.getResources().openRawResource(resId); // создание InputStream
        File file = new File(mContext.getFilesDir(), "playlist.m3u"); // создание объекта File для сохранения файла
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
        };
        return file;

    }
}

