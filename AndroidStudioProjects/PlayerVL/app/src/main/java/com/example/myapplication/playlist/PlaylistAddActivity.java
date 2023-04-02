package com.example.myapplication.playlist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.RoomDatabase;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.AppDateBase;
import com.example.myapplication.R;
import com.example.myapplication.RealPathUtil;

import java.util.List;
import java.util.Set;

public class PlaylistAddActivity extends AppCompatActivity {

    private TextView labelPlaylistPathFile;
    private Button buttonSelectPlaylist;
    private Button buttonSavePlaylist;
    private Button buttonDeletePlaylist;
    private ActivityResultLauncher<Intent> mGetContent;
    private EditText inputPlaylistName;

    private String name = "";
    private String path = "";
    private Context context;
    private AppDateBase db;
    private  PlaylistData playlistData;
    private Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_add);



        inputPlaylistName = findViewById(R.id.inputPlaylistName);
        labelPlaylistPathFile = findViewById(R.id.labelPlaylistPathFile);
        buttonSelectPlaylist = findViewById(R.id.buttonSelectPlaylist);
        buttonSavePlaylist = findViewById(R.id.buttonSavePlaylist);
        buttonDeletePlaylist = findViewById(R.id.buttonDeletePlaylist);



        db = AppDateBase.getInstance(getApplication());
        context = getApplicationContext();
       // onClickSelectFilePlaylist();
       // onClickSavePlaylist();
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
                            //path = PlaylistDownload.getFilePathFromUri(context, uri);
                            //path = uri.getPath();
                            path = RealPathUtil.getRealPath(context, uri);// TODO поменять обработку uri to path
                            labelPlaylistPathFile.setText(path);
                            labelPlaylistPathFile.setVisibility(View.VISIBLE);
                            buttonSavePlaylist.setEnabled(true);
                        }
                    }
                });


        intent = getIntent();
        if (intent != null) {
            playlistData = (PlaylistData) intent.getSerializableExtra("playlist");
            if ( playlistData != null) {

                buttonDeletePlaylist.setVisibility(View.VISIBLE);
                inputPlaylistName.setText(playlistData.getName());
                labelPlaylistPathFile.setVisibility(View.VISIBLE);
                labelPlaylistPathFile.setText(playlistData.getPath());
                buttonSavePlaylist.setEnabled(true);

                Log.d("playlistData", playlistData.toString());

            }else {


            }

        }

        buttonSelectPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*"); // Тип файлов, которые можно выбирать
                intent.addCategory(Intent.CATEGORY_OPENABLE); // Файлы, которые можно открыть
                mGetContent.launch(intent);
            }

        });

        buttonSavePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    updatePlaylist();
                    }else{
                    savePlaylist();
                }

            }
        });

        buttonDeletePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(playlistData !=null){
                    db.PlaylistDAO().delete(playlistData);
                    startActivity(new Intent(getApplicationContext(), PlaylistActivity.class));
                }

            }
        });

    }

    private void savePlaylist(){
        name = String.valueOf(inputPlaylistName.getText());
        boolean isExits = false;
        List<PlaylistData> playlistData = db.PlaylistDAO().getAll();

        for (PlaylistData playlistData1 : playlistData) {
            if (playlistData1.getName().equals(name)) {
                isExits = true;
                break;
            }
        }
        if (isExits == false) {
            PlaylistDownload.downloadPlaylist(db, name, path); // Загружаем плэйлист с каналами
            Intent intent = new Intent(PlaylistAddActivity.this, PlaylistActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(PlaylistAddActivity.this, getText(R.string.warnigPlaylistExist), Toast.LENGTH_SHORT).show();
        }

    }
    private void updatePlaylist(){

        if(playlistData != null){

            db.PlaylistDAO().delete(playlistData);
            PlaylistDownload.downloadPlaylist(db, String.valueOf(inputPlaylistName.getText()) , String.valueOf(labelPlaylistPathFile.getText()));
            startActivity(new Intent(this, PlaylistActivity.class));

        }

    }

    /*Запрашиваем права*/
    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void verifyStoragePermissions(Activity activity) {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.MANAGE_EXTERNAL_STORAGE }, 100);
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, 200);
        }
    }


}