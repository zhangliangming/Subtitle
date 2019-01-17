package com.zlm.subtitlelibrary.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * @Description: 字幕视图
 * @author: zhangliangming
 * @date: 2019-01-17 22:17
 **/
public class SubtitleView extends AppCompatTextView {

    public SubtitleView(Context context) {
        super(context);
        init(context);
    }

    public SubtitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SubtitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }
}
