package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivityViewModel extends AndroidViewModel {


    private final AppDateBase db;
    private final MutableLiveData<List<Channel>> channelsLD;
    private final CompositeDisposable compositeDisposable;
    private List<Channel> channels;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        db = AppDateBase.getInstance(application);
        channels = new ArrayList<>();
        channelsLD = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();

    }

    /* В зависимости от name group заполняем  channels и передаем  player*/
    public MutableLiveData<List<Channel>> getChannelsLD(String nameGroupFromIntent) {
        List<Channel> channelArrayList = new ArrayList<>(); // Собираем  каналы с именем плэйлиста
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                if (nameGroupFromIntent.equals("Избранное")) {
                    // Каналы из избранного
                    channels = db.channelEntityDAO().getLikes();
                    for (Channel channel : channels) {
                        // Просто добавляем имя плэйлиста к каналу
                        channel.setPlaylistName(db.playlistDAO().getById(channel.getPlaylist_id()).getName());
                        channelArrayList.add(channel);
                    }
                    channelsLD.postValue(channelArrayList);
                } else {
                    // Активные каналы
                    List<Channel> channelList = db.channelEntityDAO().getChannelsByActivePlaylist(1);
                    channels = channelList.stream()
                            .filter(channel -> channel.getGroupChannel().equals(nameGroupFromIntent))
                            .collect(Collectors.toList());

                    for (Channel channel : channels) {
                        // Просто добавляем имя плэйлиста к каналу
                        channel.setPlaylistName(db.playlistDAO().getById(channel.getPlaylist_id()).getName());
                        channelArrayList.add(channel);
                    }
                    channelsLD.postValue(channelArrayList);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe();

        return channelsLD;
    }

    /* Устанавливает Like на канале */
    public void setLike(Channel channel) {

        Disposable disposable = Completable.fromAction(new Action() {
                    @Override
                    public void run() throws Throwable {

                        if (db.channelEntityDAO().isActive(channel.getId()) == 1) {
                            db.channelEntityDAO().updateLike(channel.getId(), 0);

                        } else {
                            db.channelEntityDAO().updateLike(channel.getId(), 1);

                        }
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        compositeDisposable.add(disposable);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();

    }
}
