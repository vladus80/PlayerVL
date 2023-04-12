package com.example.myapplication.playlist;

import android.app.Application;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.AppDateBase;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PlaylistAddViewModel extends AndroidViewModel {

    private AppDateBase db;

    public PlaylistAddViewModel(@NonNull Application application) {
        super(application);
        db = AppDateBase.getInstance(application);

    }

    public void deletePlaylist(PlaylistData playlist){
        db.playlistDAO().delete(playlist);
    }

    public void switchActivate(CompoundButton compoundButton, boolean isChecked){

        Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {
//                        if (isChecked) {
//                            db.channelEntityDAO().setActive(playlistData.getId(), 1);
//                            db.playlistDAO().setActive(playlistData.getId(), 1);
//                        } else {
//                            db.channelEntityDAO().setActive(playlistData.getId(), 0);
//                            db.playlistDAO().setActive(playlistData.getId(), 0);
//                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

}
