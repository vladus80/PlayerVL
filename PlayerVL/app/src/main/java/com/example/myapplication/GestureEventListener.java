package com.example.myapplication;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.google.android.exoplayer2.Player;

public class GestureEventListener extends GestureDetector.SimpleOnGestureListener 
        implements Player.Listener, GestureDetector.OnGestureListener, View.OnTouchListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    private final GestureDetector gestureDetector;
    private final Player player;
    private Context context;


    public GestureEventListener(Context context, Player player) {
        gestureDetector = new GestureDetector(context, this);
        this.player = player;
        this.context  = context;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //return gestureDetector.onTouchEvent(event);
        float startX = 0;
        float startY = 0;
        long startTime = 0;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                startTime = System.currentTimeMillis();
                //Toast.makeText(context, event.toString(), Toast.LENGTH_SHORT).show();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                long endTime = System.currentTimeMillis();
                float dx = endX - startX;
                float dy = endY - startY;
                float absDx = Math.abs(dx);
                float absDy = Math.abs(dy);
                long duration = endTime - startTime;
                if (duration < 1000 && absDx > SWIPE_THRESHOLD && absDy < SWIPE_THRESHOLD && Math.abs(dx) > SWIPE_VELOCITY_THRESHOLD) {
                    if (dx > 0) {
                        // выполнить действие при свайпе вправо
                        Toast.makeText(context, "Свайп вправо", Toast.LENGTH_SHORT).show();
                    } else {
                        // выполнить действие при свайпе влево
                        Toast.makeText(context, "Свайп влево", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                //Toast.makeText(context, event.toString(), Toast.LENGTH_SHORT).show();
                break;
        }

        gestureDetector.onTouchEvent(event);
        return false;
    }




    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        // Обработка одиночного касания экрана
        System.out.println("Произошло касание");
        FFmpegKit.cancel();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float distanceX, float distanceY) {
        // Обработка скролла
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        // Обработка долгого нажатия
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float velocityX, float velocityY) {
        // Обработка жестов вправо/влево
        float diffX = motionEvent1.getX() - motionEvent.getX();
        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffX > 0) {

                player.previous();
                //Toast.makeText(context, "Жест влево", Toast.LENGTH_SHORT).show();
            } else {
                player.next();
                //Toast.makeText(context, "Жест вправо", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // Определение текущего состояния плеера
        switch (playbackState) {
            case Player.STATE_BUFFERING:
                // Отображение индикатора загрузки
                break;
            case Player.STATE_READY:
                // Скрытие индикатора загрузки
                break;
            case Player.STATE_ENDED:
                // Обработка завершения воспроизведения
                break;
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        // Передача событий жестов в GestureDetector
        return gestureDetector.onTouchEvent(event);
    }
}

