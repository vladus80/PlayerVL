package com.example.myapplication;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GroupChannelViewModel extends AndroidViewModel {

    private AppDateBase db;
    private CompositeDisposable compositeDisposable;

    private MutableLiveData<Integer> countChannelsInFavoritLD;

    public GroupChannelViewModel(@NonNull Application application) {
        super(application);

        db  = AppDateBase.getInstance(getApplication());
        countChannelsInFavoritLD = new MutableLiveData<>();
        compositeDisposable = new CompositeDisposable();

    }

    /* Получаем активные каналы*/
    public LiveData<List<Channel>> getchannelsActiveList(){

        return db.channelEntityDAO().getChannelsByActivePlaylistLD(1);

    }


    /* Будем слушать изменения на колво избранных каналов*/
    public Integer getCountChannelsInFavorit() {
        return countChannelsInFavoritLD.getValue();
    }

    /*Получаем колво каналов в избранном избранном*/
    public void getCountChannelsInGroup(){


       Disposable disposable =  Single.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return db.channelEntityDAO().getLikes().size();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer countChanSize) throws Throwable {
                        countChannelsInFavoritLD.setValue(countChanSize);
                    }
                });
        compositeDisposable.add(disposable);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
