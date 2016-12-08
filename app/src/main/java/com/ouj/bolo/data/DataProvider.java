package com.ouj.bolo.data;

import com.ouj.bolo.data.http.HttpClient;
import com.ouj.bolo.entity.MovieEntity;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/8.
 */

public class DataProvider {

    public static Subscription getTopMovie(Subscriber<MovieEntity> subscriber) {
        return HttpClient.getApi().getTopMovie(0, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }
}
