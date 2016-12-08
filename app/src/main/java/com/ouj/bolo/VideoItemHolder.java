package com.ouj.bolo;

import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/26.
 */

public class VideoItemHolder extends BaseVideoHolder {

    TextView nameTextView;
    TextView contentTextView;

    public VideoItemHolder(View itemView) {
        super(itemView);
        nameTextView = (TextView) itemView.findViewById(R.id.nameTextView);
        contentTextView = (TextView) itemView.findViewById(R.id.contentTextView);
    }
}
