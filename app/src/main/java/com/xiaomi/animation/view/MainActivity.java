package com.xiaomi.animation.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaomi.animation.Info.IconInfo;
import com.xiaomi.animation.PropertyAnimator.AnimationUtils;
import com.xiaomi.animation.R;
import com.xiaomi.animation.Util;
import com.xiaomi.animation.transition.MIUILauncherTransition;
import com.xiaomi.animation.transition.test;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final DecelerateInterpolator sDecelerateInterpolator = new DecelerateInterpolator();
    private FrameLayout mRootView;
    private GridView mGirdView;
    private RelativeLayout mPopView;
    private MIUILauncherTransition mMIUILauncherTransition;
    private boolean isTransition = false;
    private IconInfo mIconInfo;
    private Context mContext;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        mMIUILauncherTransition = new MIUILauncherTransition();
        getWindow().setReenterTransition(mMIUILauncherTransition);
        super.onCreate(savedInstanceState);
        mContext = this;
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        mGirdView = (GridView) findViewById(R.id.grid_view);
        mRootView = (FrameLayout) findViewById(R.id.parent_panel);
        GridAdapter gridAdapter = new GridAdapter(Util.foundIconList(this),this);
        mGirdView.setAdapter(gridAdapter);
        mGirdView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        IconInfo iconInfo = (IconInfo) view.getTag();
        mIconInfo = iconInfo;
        iconInfo.setRectF(Util.getDrawTopRect((BubbleTextView) view));
        if (isTransition)
            startActivity(iconInfo);
        else
            startView(iconInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mPopView != null) {
            AnimatorSet animatorSet = startViewExitAnimation(mIconInfo);
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mRootView.removeView(mPopView);
                    mPopView = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }else {
            super.onBackPressed();
        }
    }

    private void startActivity(IconInfo iconInfo) {
        Intent intent = new Intent(MainActivity.this, IconActivity.class);
        intent.putExtra("Icon_Info",iconInfo);
        mMIUILauncherTransition.setIntent(intent);
        startActivityForResult(intent,1,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void startView(IconInfo iconInfo) {
        mPopView = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.activity_icon,null,false);
        mRootView.addView(mPopView);
        startViewEnterAnimation(iconInfo);
    }

    private void startViewEnterAnimation(IconInfo iconInfo) {
        float scaleX = (float) iconInfo.getWidth() / (float) mGirdView.getWidth();
        float scaleY = (float) iconInfo.getHeight() / (float) mGirdView.getHeight();
        float startX = iconInfo.getRectF().centerX() - iconInfo.getWidth() / 2;
        float startY = iconInfo.getRectF().centerY() - iconInfo.getHeight() / 2 ;
        AnimatorSet animatorSet = AnimationUtils.createAnimatorSet();
        mPopView.setPivotX(0);
        mPopView.setPivotY(0);
        ObjectAnimator alphaAnim = AnimationUtils.ofFloat(mPopView, "alpha", 0f, 1f);
        ObjectAnimator xAnim = AnimationUtils.ofFloat(mPopView, "x",startX, 0f);
        ObjectAnimator yAnim = AnimationUtils.ofFloat(mPopView, "y",startY, 0f);
        ObjectAnimator scaleXAnim = AnimationUtils.ofFloat(mPopView, "scaleX", scaleX, 1f);
        ObjectAnimator scaleYAnim = AnimationUtils.ofFloat(mPopView, "scaleY", scaleY,1f);

        mGirdView.setPivotX(iconInfo.getRectF().centerX());
        mGirdView.setPivotY(iconInfo.getRectF().centerY());
        ObjectAnimator alphaGridViewAnim = AnimationUtils.ofFloat(mGirdView, "alpha", 1f, 0f);
        ObjectAnimator scaleGridViewXAnim = AnimationUtils.ofFloat(mGirdView, "scaleX", 1f, 2f);
        ObjectAnimator scaleGridViewYAnim = AnimationUtils.ofFloat(mGirdView, "scaleY",1f, 2f);

        animatorSet.setDuration(800);
        animatorSet.setInterpolator(sDecelerateInterpolator);
        animatorSet.playTogether(alphaAnim, xAnim, yAnim, scaleXAnim, scaleYAnim,
                alphaGridViewAnim, scaleGridViewXAnim, scaleGridViewYAnim);
        animatorSet.start();
    }

    private AnimatorSet startViewExitAnimation(final IconInfo iconInfo) {
        mTextView = createThumbnailView();
        float scaleX = (float) iconInfo.getWidth() / (float) mGirdView.getWidth();
        float scaleY = (float) iconInfo.getHeight() / (float) mGirdView.getHeight();
        float endX = iconInfo.getRectF().centerX() - iconInfo.getWidth() / 2;
        float endY = iconInfo.getRectF().centerY() - iconInfo.getHeight() / 2 ;
        AnimatorSet animatorSet = AnimationUtils.createAnimatorSet();
        mPopView.setPivotX(0);
        mPopView.setPivotY(0);
        ObjectAnimator alphaAnim = AnimationUtils.ofFloat(mTextView, "alpha", 0f, 1f);
        ObjectAnimator xAnim = AnimationUtils.ofFloat(mPopView, "x",0f, endX);
        ObjectAnimator yAnim = AnimationUtils.ofFloat(mPopView, "y",0f, endY);
        ObjectAnimator scaleXAnim = AnimationUtils.ofFloat(mPopView, "scaleX", 1f, scaleX);
        ObjectAnimator scaleYAnim = AnimationUtils.ofFloat(mPopView, "scaleY", 1f,scaleY);
        scaleXAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mTextView != null) {
                    mRootView.removeView(mTextView);
                    mTextView = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        scaleXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int[] position = new int[2];
                mPopView.getLocationInWindow(position);
                float k = 1 / ((1- 0.65f) * animation.getDuration());
                float b =  1- animation.getDuration() * k;
                if (animation.getDuration() * 0.65f < (float)animation.getCurrentPlayTime()) {
                    ViewGroup.LayoutParams layoutParams = mTextView.getLayoutParams();
                    Drawable drawable = mContext.getResources().getDrawable(mIconInfo.getDrawableId());
                    int thumbnailWidth = (int) (mPopView.getScaleX() * mPopView.getWidth());
                    int thumbnailHeight = (int) (mPopView.getScaleY() * mPopView.getHeight());
                    drawable.setBounds(0,0,thumbnailWidth,thumbnailHeight);
                    //alpha = kx + b;
                    float alpha = k * animation.getCurrentPlayTime() + b;
                    mTextView.setAlpha(alpha);
                    mTextView.setCompoundDrawables(null,drawable,null,null);
                    layoutParams.width = thumbnailWidth < mIconInfo.getWidth() ?  mIconInfo.getWidth() : thumbnailWidth;
                    layoutParams.height = thumbnailHeight < mIconInfo.getHeight() ?  mIconInfo.getHeight() : thumbnailHeight;
                    mTextView.setLayoutParams(layoutParams);
                    mTextView.setX(position[0]);
                    mTextView.setY(position[1] - getStatusBarHeight(mContext));
                }
            }
        });


        mGirdView.setPivotX(iconInfo.getRectF().centerX());
        mGirdView.setPivotY(iconInfo.getRectF().centerY());
        ObjectAnimator alphaGridViewAnim = AnimationUtils.ofFloat(mGirdView, "alpha", 0f, 1f);
        ObjectAnimator scaleGridViewXAnim = AnimationUtils.ofFloat(mGirdView, "scaleX", 2f, 1f);
        ObjectAnimator scaleGridViewYAnim = AnimationUtils.ofFloat(mGirdView, "scaleY",2f, 1f);


        animatorSet.setDuration(800);
        animatorSet.setInterpolator(sDecelerateInterpolator);
        animatorSet.playTogether(alphaAnim, xAnim, yAnim, scaleXAnim, scaleYAnim,
                alphaGridViewAnim, scaleGridViewXAnim, scaleGridViewYAnim);

        animatorSet.start();
        return  animatorSet;
    }

    private TextView createThumbnailView() {
        TextView textView = new TextView(mContext);
        mRootView.addView(textView);
        textView.setX(0);
        textView.setY(0);
        textView.setBackgroundColor(Color.TRANSPARENT);
        return textView;
    }

    private int getStatusBarHeight(Context context) {
        /**
         * 获取状态栏高度——方法2
         * */
        int statusBarHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  statusBarHeight;
    }
}

