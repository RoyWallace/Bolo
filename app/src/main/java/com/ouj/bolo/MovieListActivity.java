package com.ouj.bolo;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouj.bolo.base.BaseActivity;
import com.ouj.bolo.data.DataProvider;
import com.ouj.bolo.entity.MovieEntity;
import com.ouj.bolo.data.http.HttpClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieListActivity extends BaseActivity {

    @Bind(R.id.movieRecyclerView)
    RecyclerView movieRecyclerView;

    private ArrayList<MovieEntity.Subject> subjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        ButterKnife.bind(this);

        movieRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        MovieListAdapter adapter = new MovieListAdapter();
        movieRecyclerView.setAdapter(adapter);

        getMovie();
    }

    private void getMovie() {

        Subscription s = DataProvider.getTopMovie(new Subscriber<MovieEntity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(MovieEntity movieEntity) {
                subjects.addAll(movieEntity.subjects);
                movieRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        addSubscription(s);
    }

    protected class MovieViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.avatarImageView)
        ImageView avatarImageView;
        @Bind(R.id.titleTextView)
        TextView titleTextView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class MovieListAdapter extends RecyclerView.Adapter<MovieViewHolder> {

        @Override
        public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View contentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
            return new MovieViewHolder(contentView);
        }

        @Override
        public void onBindViewHolder(MovieViewHolder holder, int position) {
            MovieEntity.Subject subject = subjects.get(position);
            Picasso.with(MovieListActivity.this).load(subject.images.medium).into(holder.avatarImageView);
            holder.titleTextView.setText(subject.title);
        }

        @Override
        public int getItemCount() {
            return subjects.size();
        }
    }
}
