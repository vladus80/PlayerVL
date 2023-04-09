package com.example.myapplication.playlist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.R;
import com.example.myapplication.RealPathUtil;

import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class PlaylistAddActivity extends AppCompatActivity {

    private TextView labelPlaylistPathFile;
    private Button buttonSelectPlaylist;
    private Button buttonSavePlaylist;
    private Button buttonDeletePlaylist;
    private ActivityResultLauncher<Intent> mGetContent;
    private EditText inputPlaylistName;
    private Switch switchActivatePlaylist;
    private String path = "";
    private Context context;
    private AppDateBase db;
    private PlaylistData playlistData;
    private Intent intent;
    private Boolean result;

    /*Запрашиваем права*/
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void verifyStoragePermissions(Activity activity) {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                PERMISSIONS_STORAGE = new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE};
            }
        }

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_add);

        inputPlaylistName = findViewById(R.id.inputPlaylistName);
        labelPlaylistPathFile = findViewById(R.id.labelPlaylistPathFile);
        buttonSelectPlaylist = findViewById(R.id.buttonSelectPlaylist);
        buttonSavePlaylist = findViewById(R.id.buttonSavePlaylist);
        buttonDeletePlaylist = findViewById(R.id.buttonDeletePlaylist);
        switchActivatePlaylist = findViewById(R.id.switchActivatePlaylist);

        db = AppDateBase.getInstance(getApplication());
        context = getApplicationContext();

        // Запрашиваем  права на доступ к файлам
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            verifyStoragePermissions(this);
        }

        /*Регистрируем для получения результата запущенной активити*/
        mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            path = RealPathUtil.getRealPath(context, uri);// TODO поменять обработку uri to path
                            labelPlaylistPathFile.setText(path);
                            labelPlaylistPathFile.setVisibility(View.VISIBLE);
                            buttonSavePlaylist.setEnabled(true);
                        }
                    }
                });

        /* В зависимости от того если прилетает  интент, то значит обновляем плэйлист,
        * а если нет, то значит сохраняем плэйлист */
        intent = getIntent();
        if (intent != null) {
            playlistData = (PlaylistData) intent.getSerializableExtra("playlist");
            if (playlistData != null) {

                buttonDeletePlaylist.setVisibility(View.VISIBLE);
                inputPlaylistName.setText(playlistData.getName());
                labelPlaylistPathFile.setVisibility(View.VISIBLE);
                labelPlaylistPathFile.setText(playlistData.getPath());
                buttonSavePlaylist.setEnabled(true);
                switchActivatePlaylist.setVisibility(View.VISIBLE);
                buttonSelectPlaylist.setText(R.string.updatePlaylist);

                if (playlistData.getActive() == 0) {

                    switchActivatePlaylist.setChecked(false);
                } else {
                    switchActivatePlaylist.setChecked(true);
                }
            }
        }

        /* Переключение Switch (активен-не активен плэлист)*/
        switchActivatePlaylist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {
                        if (isChecked) {
                            db.channelEntityDAO().setActive(playlistData.getId(), 1);
                            db.playlistDAO().setActive(playlistData.getId(), 1);
                        } else {
                            db.channelEntityDAO().setActive(playlistData.getId(), 0);
                            db.playlistDAO().setActive(playlistData.getId(), 0);
                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
            }
        });

        /* Открываем системное активити выбора файлов */
        buttonSelectPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Тип файлов, которые можно выбирать
                intent.addCategory(Intent.CATEGORY_OPENABLE); // Файлы, которые можно открыть
                mGetContent.launch(intent);
            }
        });

        /* Сохраняем или обновляем плэйлист */
        buttonSavePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    updatePlaylist();
                } else {
                    savePlaylist();
                }
            }
        });

        /* Удаляем плэйлист */
        buttonDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {

                Observable.fromCallable(() -> {
                            db.playlistDAO().delete(playlistData);
                            return playlistData;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<PlaylistData>() {
                            @Override
                            public void accept(PlaylistData playlistData) throws Throwable {
                                startActivity(new Intent(getApplicationContext(), PlaylistActivity.class));
                                Toast.makeText(getApplicationContext(), "Плэйлист " + playlistData.getName() + " удален", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
            }
        });
    }

    /* Метод определяет сохранение и загрузку плэйлиста*/
    @SuppressLint("CheckResult")
    private void savePlaylist() {
        String namePlaylist2 = String.valueOf(inputPlaylistName.getText());

        if(getExitsName(namePlaylist2)){
            Toast.makeText(PlaylistAddActivity.this, getText(R.string.warnigPlaylistExist), Toast.LENGTH_SHORT).show();
            //Log.d("getExitsName", String.valueOf(getExitsName(namePlaylist2)));

        }else{

            Intent intent = new Intent(PlaylistAddActivity.this, PlaylistActivity.class);
            startActivity(intent);
            Observable.fromCallable(() -> {
                        if (db.playlistDAO().getCountPlaylistByName(namePlaylist2) < 1) {
                            PlaylistDownload.downloadPlaylist(db, namePlaylist2, path, 1); // Загружаем плэйлист с каналами
                            result = false;
                        } else {
                            result = true;
                        }
                        return result;
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean result) throws Throwable {

                            if (result == false) {
//                                Intent intent = new Intent(PlaylistAddActivity.this, PlaylistActivity.class);
//                                startActivity(intent);
//                                finish();

                            } else {
                           Toast.makeText(PlaylistAddActivity.this,
                                   getText(R.string.warnigPlaylistExist), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            finish();
        }

    }

    /*Метод обновляет плэйлист*/
    @SuppressLint("CheckResult")
    private void updatePlaylist() {
        if (playlistData != null) {

            Observable.fromCallable(new Callable<Boolean>() {  //Создаем Observable
                    @Override
                    public Boolean call() throws Exception {

                        if (switchActivatePlaylist.isChecked()) {
                            db.playlistDAO().delete(playlistData);
                            PlaylistDownload.downloadPlaylist(db, String.valueOf(inputPlaylistName.getText()),
                                    String.valueOf(labelPlaylistPathFile.getText()), 1);
                        } else {
                            db.playlistDAO().delete(playlistData);
                            PlaylistDownload.downloadPlaylist(db, String.valueOf(inputPlaylistName.getText()),
                                    String.valueOf(labelPlaylistPathFile.getText()), 0);
                        }
                        return false;
                    }
                })
                .subscribeOn(Schedulers.io())   // Подписываемся на ввод-вывод
                .observeOn(AndroidSchedulers.mainThread())  // Передаем результат в главный поток
                .subscribe(result -> {  // Подписываемся и обрабатываем результат
                    finish();
                }, throwable -> {
                    // обработка ошибок
                });
            //Не будем дожидаться выполнения оперции к базе и перейдем на активити списка плэйлистов
            startActivity(new Intent(PlaylistAddActivity.this, PlaylistActivity.class));
        }
    }

    /* Переопределяем кнопку Back */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PlaylistAddActivity.this, PlaylistActivity.class));
    }
     /* Заправшиваем права*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 8000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение было предоставлено
                Log.d("checkSelfPermission", "Разрешение предоставлено");

            } else {
                // Разрешение не было предоставлено
                Log.d("checkSelfPermission", "Разрешение  не предоставлено");
            }
        }
    }


    private Boolean getExitsName(String name){

        Handler handler = new Handler(Looper.getMainLooper());

        final Boolean[] res = {false};
        new Thread(new Runnable() {
            @Override
            public void run() {

                int c = db.playlistDAO().getCountPlaylistByName(name);
                Log.d("getExitsName", String.valueOf(c));


                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        if (c < 1) {

                            res[0] = false;
                        } else {
                            res[0] = true;

                        }

                    }
                });
            }

        }).start();
        //Thread.sleep(100);
        return res[0];
    }

}