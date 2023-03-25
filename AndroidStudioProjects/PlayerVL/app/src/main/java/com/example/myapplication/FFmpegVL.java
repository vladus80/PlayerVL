package com.example.myapplication;

import android.os.Environment;
import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFprobeKit;
import com.arthenica.ffmpegkit.MediaInformationSession;
import com.arthenica.ffmpegkit.ReturnCode;
import com.arthenica.ffmpegkit.SessionState;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* Записываем видео с помощью FFmpeg*/

public class FFmpegVL {
    public FFmpegVL() {


        /*

    List<Channel> channelList = new ArrayList<>();

    String URL_ = channelList.get(10).getUrlChannel().toString();
    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    String downloadPath = downloadDir.getAbsolutePath();
    String outputPath = downloadPath + "/output.mp4";
    String[] command = {"-i", URL_, "-c", "copy", outputPath};


    FFmpegKit.executeWithArgumentsAsync(command, session -> {
        SessionState state = session.getState();
        ReturnCode returnCode = session.getReturnCode();
        // CALLED WHEN SESSION IS EXECUTED

        Log.d("FFmpegKit", String.format("FFmpeg process exited with state %s and rc %s.%s", state, returnCode, session.getFailStackTrace()));
    }, log -> {

        // CALLED WHEN SESSION PRINTS LOGS

        Log.d("FFmpegKitLog", String.valueOf(log.getMessage()));

    }, statistics -> {

        // CALLED WHEN SESSION GENERATES STATISTICS
        Log.d("FFmpegKitFPS", String.valueOf(statistics.getVideoFps()));

    });

            FFmpegKit.cancel();

    MediaInformationSession mediaInformation = FFprobeKit.getMediaInformation(URL_);
    Log.d("FFmpegKitInfo", mediaInformation.getMediaInformation().getFilename());*/




    }


}
