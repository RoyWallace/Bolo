package com.ouj.bolo;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/11/29.
 */

public class BaseVideoHolder extends RecyclerView.ViewHolder implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener {

    View alphaView;

    TextureView videoView;
    ImageView coverImageView;
    ImageView stateImageView;
    ProgressBar progressBar;
    SeekBar seekBar;
    ImageView toggleFullScreenImageView;
    TextView currentTextView;
    TextView totalTextView;

    String videoUrl;

    Timer timer;
    TimerTask timerTask;

    boolean seekBarTouching;

    RelativeLayout optionLayout;

    public BaseVideoHolder(View itemView) {
        super(itemView);
        videoView = (TextureView) itemView.findViewById(R.id.videoView);
        coverImageView = (ImageView) itemView.findViewById(R.id.coverImageView);
        stateImageView = (ImageView) itemView.findViewById(R.id.stateImageView);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        seekBar = (SeekBar) itemView.findViewById(R.id.seekBar);
        toggleFullScreenImageView = (ImageView) itemView.findViewById(R.id.toggleFullScreenImageView);
        currentTextView = (TextView) itemView.findViewById(R.id.currentTextView);
        totalTextView = (TextView) itemView.findViewById(R.id.totalTextView);
        optionLayout = (RelativeLayout) itemView.findViewById(R.id.optionLayout);
        alphaView = itemView.findViewById(R.id.alphaView);

        stateImageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    Context getContext() {
        return videoView.getContext();
    }

    public void bindData(final String url) {
        this.videoUrl = url;
        videoView.setVisibility(View.INVISIBLE);

        Observable.OnSubscribe<VideoCoverInfo> onSubscribe = new Observable.OnSubscribe<VideoCoverInfo>() {
            @Override
            public void call(Subscriber<? super VideoCoverInfo> subscriber) {
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    AssetFileDescriptor afd = getContext().getAssets().openFd(url);
                    retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    VideoCoverInfo info = new VideoCoverInfo();
                    info.bitmap = retriever.getFrameAtTime();
                    info.duration = Integer.valueOf(duration);
                    subscriber.onNext(info);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Observable.create(onSubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<VideoCoverInfo>() {
                    @Override
                    public void call(VideoCoverInfo info) {
                        if (coverImageView.isShown())
                            coverImageView.setImageBitmap(info.bitmap);
                        currentTextView.setText("00:00");
                        totalTextView.setText(Utils.formatTime(info.duration));
                        seekBar.setMax(info.duration);
                    }
                });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionLayout.setVisibility(optionLayout.isShown() ? View.GONE : View.VISIBLE);
            }
        });
    }

    public void play() {
        if (videoView.isAvailable() && !videoView.isShown()) {
            textureStart(videoView.getSurfaceTexture());
        } else {
            videoView.setSurfaceTextureListener(this);
        }
        videoView.setVisibility(View.VISIBLE);

        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(350);
        stateImageView.startAnimation(animation);
        stateImageView.setVisibility(View.GONE);

//        AlphaAnimation animation1 = new AlphaAnimation(0, 1);
//        animation1.setDuration(350);
//        alphaView.startAnimation(animation1);
        alphaView.setVisibility(View.GONE);

//        timer = new Timer();
//        timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                int current = OneMediaPlayer.getMediaPlayer().getCurrentPosition();
//                if (current < 0) {
//                    return;
//                }
//                int duration = OneMediaPlayer.getMediaPlayer().getDuration();
//                if (!seekBarTouching) {
//                    seekBar.setProgress(current);
//                }
//            }
//        };
//        timer.schedule(timerTask, 0, 100);
    }

    public void stop() {
        coverImageView.setVisibility(View.VISIBLE);
        stateImageView.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.INVISIBLE);

        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(350);
        alphaView.startAnimation(animation);
        alphaView.setVisibility(View.VISIBLE);

        if (timer != null) {
            timer.cancel();
        }
        if (timerTask != null) {
            timerTask.cancel();
        }
    }

    boolean textureAlreadyShow() {
        Rect rect = new Rect();
        videoView.getLocalVisibleRect(rect);
        return rect.top == 0 && rect.bottom == videoView.getHeight();
    }

    void textureStart(final SurfaceTexture texture) {
        Observable.OnSubscribe<String> onSubscribe = new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    AssetFileDescriptor afd = getContext().getAssets().openFd(videoUrl);
                    Surface surface = new Surface(texture);
                    MediaPlayer mediaPlayer = OneMediaPlayer.getMediaPlayer();
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mediaPlayer.setSurface(surface);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setOnPreparedListener(BaseVideoHolder.this);
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Observable.create(onSubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
        textureStart(texture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        AlphaAnimation aa = new AlphaAnimation(1, 0);
        aa.setDuration(500);
        coverImageView.startAnimation(aa);
        coverImageView.setVisibility(View.GONE);
        mp.start();
    }
}
