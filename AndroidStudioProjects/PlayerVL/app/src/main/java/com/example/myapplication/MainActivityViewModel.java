package com.example.myapplication;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivityViewModel extends AndroidViewModel {

    private final AppDateBase db;
    private final MutableLiveData<List<Channel>> channelsLD;
    private final CompositeDisposable compositeDisposable;
    private final MutableLiveData<String> groupNameLD;
    private List<Channel> channels;


    public MainActivityViewModel(@NonNull Application application) {
        super(application);

        db = AppDateBase.getInstance(application);
        channels = new ArrayList<>();
        channelsLD = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();
        groupNameLD = new MutableLiveData<>();



    }

    /* Подписываемся на этот метод для получения channels*/
    public LiveData<List<Channel>> getChannelsLD() {
        return channelsLD;
    }

    /* В методе фильтруем channels (избранное или нет) и отдаем в LiveData channelsLD*/
    public void setChannelsLD(String grName) {

        //String grName = groupNameLD.getValue();
        Log.d("nameGroupViewModel-55", grName);
        List<Channel> channelArrayList = new ArrayList<>(); // Собираем  каналы с именем плэйлиста
        Disposable disposable = Completable.fromAction(() -> {
            if (grName.equals("Избранное")) {
                // Каналы из избранного
                channels = db.channelEntityDAO().getLikes();
                for (Channel channel : channels) {
                    // Просто добавляем имя плэйлиста к каналу
                    channel.setPlaylistName(db.playlistDAO().getById(channel.getPlaylist_id()).getName());
                    channelArrayList.add(channel);
                }
            } else {
                // Активные каналы
                List<Channel> channelList = db.channelEntityDAO().getChannelsByActivePlaylist(1);
                channels = channelList.stream()
                        .filter(channel -> channel.getGroupChannel().equals(grName))
                        .collect(Collectors.toList());

                for (Channel channel : channels) {
                    // Просто добавляем имя плэйлиста к каналу
                    channel.setPlaylistName(db.playlistDAO().getById(channel.getPlaylist_id()).getName());
                    channelArrayList.add(channel);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> channelsLD.setValue(channelArrayList));

        compositeDisposable.add(disposable);

    }

    /* Подписываемся на этот метод для получения groups (заполняем спинер)*/
    public LiveData<List<String>> getGroupsLD() {
        return db.channelEntityDAO().getGroupsActive();
    }


    /* В активити через этот метод устанавливаем значение */
    public void setGroupName(String groupName) {
        groupNameLD.setValue(groupName); // Меняем значение в groupNameLD в LiveDta
        setChannelsLD(groupName); // Через этот метод возвращаем значения channels в LiveData channelsLD ;

    }

    /* Устанавливает Like на канале */
    public void setLike(Channel channel) {

        Disposable disposable = Completable.fromAction(() -> {
            if (db.channelEntityDAO().isActive(channel.getId()) == 1) {
                db.channelEntityDAO().updateLike(channel.getId(), 0);
            } else {
                db.channelEntityDAO().updateLike(channel.getId(), 1);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        compositeDisposable.add(disposable);
    }

    /* Устанавливает активность на канале */
    public void setActivated(Channel channel){

        Disposable disposable = Completable.fromAction(new Action() {
            @Override
            public void run() throws Throwable {
                db.channelEntityDAO().setActivatedClear();
                db.channelEntityDAO().setActivated(channel.getId(), 1);
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
