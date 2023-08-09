package com.fileclean.mkmfilerecovery.Model;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DelayScrollRecyclerView extends RecyclerView {

    Context context;

    public DelayScrollRecyclerView(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public DelayScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DelayScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {

        velocityY *= 0.5;
        // velocityX *= 0.3; for Horizontal recycler view. comment velocityY line not require for Horizontal Mode.

        return super.fling(velocityX, velocityY);
    }
}
