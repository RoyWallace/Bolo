package com.ouj.bolo;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ouj.bolo.entity.MovieEntity;
import com.ouj.bolo.data.http.ApiService;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    private RecyclerView recyclerView;

    private TextView textView;

    String[] videoUrlList = {"onepunch01.mp4", "onepunch02.mp4", "onepunch03.mp4", "onepunch04.mp4", "onepunch05.mp4", "onepunch05.mp4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.videoRecyclerView);
        textView = (TextView) findViewById(R.id.textView);
        setUpRecyclerView();

        getMovie();
    }

    void setUpRecyclerView() {
        recyclerView.setAdapter(new VideoListAdapter(videoUrlList));
        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.i("etong", "idle");
                    doOnRecyclerViewStopScroll();
                }
            }
        });
        recyclerView.smoothScrollBy(0, -10);
    }

    void doOnRecyclerViewStopScroll() {
        View showChild = getShowChild();
        if (showChild == null)
            return;
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            VideoItemHolder holder = (VideoItemHolder) recyclerView.getChildViewHolder(child);
            if (showChild.equals(child)) {
                holder.play();
            } else {
                holder.stop();
            }
        }
    }

    View getShowChild() {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            VideoItemHolder holder = (VideoItemHolder) recyclerView.getChildViewHolder(child);
            if (holder.textureAlreadyShow()) {
                return child;
            }
        }
        return null;
    }

    private void getMovie(){
        String baseUrl = "https://api.douban.com/v2/movie/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ApiService movieService = retrofit.create(ApiService.class);

        movieService.getTopMovie(0, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MovieEntity>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(MainActivity.this, "Get Top Movie Completed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        textView.setText(e.getMessage());
                    }

                    @Override
                    public void onNext(MovieEntity movieEntity) {
                        textView.setText(movieEntity.title);
                    }
                });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Toast.makeText(this, "播放完", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OneMediaPlayer.getMediaPlayer().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        OneMediaPlayer.getMediaPlayer().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OneMediaPlayer.onDestroy();
    }
}
