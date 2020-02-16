package com.xvzan.simplemoneytracker.ui.home;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xvzan.simplemoneytracker.R;

import java.text.DateFormat;
import java.util.Date;

public class FastScroller extends LinearLayout {
    private static final int HANDLE_ANIMATION_DURATION = 100;
    private static final int HANDLE_HIDE_DELAY = 1200;

    private boolean firstScroll = true;

    private ImageView ib_handle;
    private TextView tv_bubble;
    private TextView tv_bubble_right;
    private RecyclerView recyclerView;
    private int height;
    private int halfNumber;
    private AnimatorSet currentAnimator = null;
    private final HandleHider handleHider = new HandleHider();

    interface BubbleTextGetter {
        Date getDateToShowInBubble(int pos);
    }

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            if (firstScroll) {
                firstScroll = false;
            }
            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING)
                ib_handle.setVisibility(VISIBLE);
            if (!ib_handle.isSelected())
                updateHandlePosition();
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            getHandler().removeCallbacks(handleHider);
            switch (newState) {
                case RecyclerView.SCROLL_STATE_SETTLING:
                    if (firstScroll) {
                        firstScroll = false;
                        ib_handle.setVisibility(INVISIBLE);
                    }
                    break;
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (ib_handle.getVisibility() == VISIBLE)
                        getHandler().postDelayed(handleHider, HANDLE_HIDE_DELAY * 2);
            }
        }
    };

    public FastScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialise(context);
    }

    public FastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialise(context);
    }

    private void initialise(Context context) {
        setOrientation(HORIZONTAL);
        setClipChildren(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fastscroller, this);
        ib_handle = findViewById(R.id.fastscroller_handle);
        ib_handle.setVisibility(INVISIBLE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        ib_handle.setSelected(false);
        tv_bubble.setVisibility(INVISIBLE);
        tv_bubble_right.setVisibility(INVISIBLE);
        super.onSizeChanged(w, h, oldw, oldh);
        height = h;
        halfNumber = height / recyclerView.getChildAt(((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition()).getHeight() / 2;
        updateHandlePosition();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (ib_handle.getVisibility() == INVISIBLE)
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getY() >= ib_handle.getY() + ib_handle.getHeight() || event.getY() <= ib_handle.getY()) {
                    return super.onTouchEvent(event);
                }
                ib_handle.setSelected(true);
                setRecyclerViewPosition(event.getY());
                getHandler().removeCallbacks(handleHider);
                tv_bubble.setVisibility(VISIBLE);
                tv_bubble_right.setVisibility(VISIBLE);
                return true;
            case MotionEvent.ACTION_MOVE:
                setHandlePosition(event.getY());
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }
                if (ib_handle.getVisibility() == INVISIBLE) {
                    showHandle();
                }
                setRecyclerViewPosition(event.getY());
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                ib_handle.setSelected(false);
                tv_bubble.setVisibility(INVISIBLE);
                tv_bubble_right.setVisibility(INVISIBLE);
                getHandler().postDelayed(handleHider, HANDLE_HIDE_DELAY);
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void setRecyclerView(final RecyclerView recyclerView, final TextView bubblel, final TextView bubbler) {
        tv_bubble = bubblel;
        tv_bubble_right = bubbler;
        tv_bubble.setVisibility(INVISIBLE);
        tv_bubble_right.setVisibility(INVISIBLE);
        ib_handle.setVisibility(INVISIBLE);
        if (this.recyclerView != recyclerView) {
            if (this.recyclerView != null)
                this.recyclerView.removeOnScrollListener(onScrollListener);
            this.recyclerView = recyclerView;
            if (this.recyclerView == null)
                return;
            recyclerView.addOnScrollListener(onScrollListener);
        }
    }

    private void setRecyclerViewPosition(float y) {
        if (recyclerView != null) {
            final int itemCount = recyclerView.getAdapter().getItemCount();
            float proportion;
            if (ib_handle.getY() == 0)
                proportion = 0f;
            else if (ib_handle.getY() + ib_handle.getHeight() >= height)
                proportion = 1f;
            else
                proportion = y / (float) (height - ib_handle.getHeight());
            int targetPos = getValueInRange(0, itemCount - 1, (int) (proportion * (float) itemCount));
            if (ib_handle.isSelected()) {
                final String bubbleText = DateFormat.getDateInstance().format(((BubbleTextGetter) recyclerView.getAdapter()).getDateToShowInBubble(targetPos));
                if (tv_bubble != null) {
                    tv_bubble.setText(bubbleText);
                    tv_bubble_right.setText(bubbleText);
                }
            }
            if (recyclerView.getAdapter().getItemCount() <= height) {
                final int offTargetPos = getValueBetween(halfNumber, itemCount - halfNumber, proportion);
                ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(offTargetPos, height / 2);
            } else
                recyclerView.scrollToPosition(targetPos);
        }
    }

    private int getValueBetween(int min, int max, float k) {
        if (k <= 0)
            return min;
        if (k >= 1)
            return max;
        return (int) ((max - min) * k) + min;
    }

    private int getValueInRange(int min, int max, int value) {
        int minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    private void updateHandlePosition() {
        final int verticalScrollOffset = recyclerView.computeVerticalScrollOffset();
        final int verticalScrollRange = recyclerView.computeVerticalScrollRange();
        float proportion = (float) verticalScrollOffset / (float) (verticalScrollRange - height);
        setHandlePosition(height * proportion);
    }

    private void setHandlePosition(float y) {
        final int handleHeight = ib_handle.getHeight();
        ib_handle.setY(getValueInRange(0, height - handleHeight, (int) (y - handleHeight / 2)));
        tv_bubble.setY(ib_handle.getY());
        tv_bubble_right.setY(ib_handle.getY());
    }

    private class HandleHider implements Runnable {
        @Override
        public void run() {
            ib_handle.setVisibility(INVISIBLE);
        }
    }

    private void showHandle() {
        AnimatorSet animatorSet = new AnimatorSet();
        ib_handle.setPivotX(ib_handle.getWidth());
        ib_handle.setPivotY(ib_handle.getHeight());
        ib_handle.setVisibility(VISIBLE);
        Animator growerX = ObjectAnimator.ofFloat(ib_handle, SCALE_X, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator growerY = ObjectAnimator.ofFloat(ib_handle, SCALE_Y, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        Animator alpha = ObjectAnimator.ofFloat(ib_handle, ALPHA, 0f, 1f).setDuration(HANDLE_ANIMATION_DURATION);
        animatorSet.playTogether(growerX, growerY, alpha);
        animatorSet.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(onScrollListener);
            recyclerView = null;
        }
    }
}