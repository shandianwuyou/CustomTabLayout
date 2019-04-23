package com.my.customtablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建者 ：赵鹏   时间：2019/4/6
 */
public class CustomTablayout extends HorizontalScrollView {

    private float mSelectedTextSize = dpToPx(20);
    private float mUnSelectedTextSize = dpToPx(15);
    private float mInterval = dpToPx(60);

    private int mSelectedTextColor = Color.parseColor("#333333");
    private int mUnSelectedTextColor = Color.parseColor("#666666");

    private int mCurSelectedIndex = -1;

    private long mAniTime = 300l;
    private TextView mCurSelectedView;
    private TextView mLastSelectedView;
    private SlidingTabStrip mStrip;

    private List<String> mTabTextList = new ArrayList<>();
    private List<TextView> mViewList = new ArrayList<>();

    public CustomTablayout(Context context) {
        this(context, null);
    }

    public CustomTablayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTablayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mStrip = new SlidingTabStrip(getContext());
        RelativeLayout.LayoutParams stripLp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        stripLp.bottomMargin = (int) dpToPx(16);
        addView(mStrip, stripLp);
    }

    public void setTabTextList(List<String> list){
        resetData();
        mTabTextList.addAll(list);
        fillTab();

    }

    private void resetData(){
        mCurSelectedIndex = -1;
        for (TextView view : mViewList) {
            if(view.getParent() != null){
                mStrip.removeView(view);
            }
        }
        mTabTextList.clear();
        mViewList.clear();
    }

    private void fillTab() {
        for(int i = 0; i < mTabTextList.size(); i++){
            final TextView textView = new TextView(getContext());
            textView.setText(mTabTextList.get(i));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnSelectedTextSize);
            textView.setTextColor(mUnSelectedTextColor);
            textView.setGravity(Gravity.CENTER);
            textView .setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            textView.setTag(i);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedPos((Integer) textView.getTag());
                    if(mListener != null){
                        mListener.onClickItem((Integer) textView.getTag());
                    }
                }
            });

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) mInterval, LinearLayout.LayoutParams.MATCH_PARENT);
//            lp.leftMargin = (int) (mInterval * i);

            mStrip.addView(textView, lp);
            mViewList.add(textView);
        }
    }

    public void setSelectedPos(int pos){
        if(mCurSelectedIndex != pos){
            mCurSelectedView = mViewList.get(pos);
            showSizeIncreaseAni();
            mCurSelectedView.setTextColor(mSelectedTextColor);
            mCurSelectedView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            if(mCurSelectedIndex != -1){
                mLastSelectedView = mViewList.get(mCurSelectedIndex);
                showSizeDecreaseAni();
                mLastSelectedView.setTextColor(mUnSelectedTextColor);
                mLastSelectedView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            mStrip.scrollToPos((int) (mInterval * pos));

            mCurSelectedIndex = pos;
        }
    }

    private void showSizeIncreaseAni(){
        ValueAnimator sizeIncreaseAni = new ValueAnimator();
        sizeIncreaseAni.setFloatValues(mUnSelectedTextSize, mSelectedTextSize);
        sizeIncreaseAni.setInterpolator(new FastOutSlowInInterpolator());
        sizeIncreaseAni.setDuration(mAniTime);
        sizeIncreaseAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurSelectedView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) animation.getAnimatedValue());
            }
        });
        sizeIncreaseAni.start();
    }

    private void showSizeDecreaseAni(){
        ValueAnimator sizeDecreaseAni = new ValueAnimator();
        sizeDecreaseAni.setFloatValues(mSelectedTextSize, mUnSelectedTextSize);
        sizeDecreaseAni.setInterpolator(new FastOutSlowInInterpolator());
        sizeDecreaseAni.setDuration(mAniTime);
        sizeDecreaseAni.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLastSelectedView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) animation.getAnimatedValue());
            }
        });
        sizeDecreaseAni.start();
    }


    private float dpToPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    private class SlidingTabStrip extends LinearLayout {

        private Paint mStripPaint;
        private int leftMargin = -1;
        private Bitmap mStripBp;
        private long mAniTime = 400l;
        private int mStripHeight;
        private int mStripWidth;

        public SlidingTabStrip(Context context) {
            super(context);
            setWillNotDraw(false);
            mStripPaint = new Paint();
            mStripBp = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_sliding_strip);
            mStripHeight = mStripBp.getHeight();
            mStripWidth = mStripBp.getWidth();
        }

        public void scrollToPos(int toDistance){
            ValueAnimator va = new ValueAnimator();
            va.setIntValues(leftMargin, (int) (toDistance + (mInterval- mStripWidth) / 2));
            va.setInterpolator(new FastOutSlowInInterpolator());
            va.setDuration(mAniTime);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    updateLocation((int) animation.getAnimatedValue());
                }
            });
            va.start();
        }

        private void updateLocation(int location){
            leftMargin = location;
            ViewCompat.postInvalidateOnAnimation(this);
        }

        @Override
        public void draw(Canvas canvas) {
            if(leftMargin != -1){
                canvas.drawBitmap(mStripBp, leftMargin, getHeight() - mStripHeight, mStripPaint);
            }
            super.draw(canvas);
        }
    }

    public interface ItemClickListener{
        void onClickItem(int postion);
    }

    private ItemClickListener mListener;
    public void setClickListener(ItemClickListener listener){
        this.mListener = listener;
    }

}
