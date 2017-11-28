package org.hobart.facetrans.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class AnimationUtils {

    public static ViewGroup createAnimLayout(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
//        animLayout.setId(Integer.MAX_VALUE);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    public static void setAddTaskAnimation(Activity activity, View startView, View targetView, final AddTaskAnimationListener listener) {
        ViewGroup animMaskLayout = createAnimLayout(activity);
        final ImageView imageView = new ImageView(activity);
        animMaskLayout.addView(imageView);

        int[] startLocArray = new int[2];
        int[] endLocArray = new int[2];
        startView.getLocationInWindow(startLocArray);
        targetView.getLocationInWindow(endLocArray);

        ViewGroup.LayoutParams startViewLayoutParams = startView.getLayoutParams();
        ViewGroup.LayoutParams targetViewLayoutParams = targetView.getLayoutParams();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                startViewLayoutParams.width,
                startViewLayoutParams.height);
        lp.leftMargin = startLocArray[0];
        lp.topMargin = startLocArray[1];
        imageView.setLayoutParams(lp);

        if (startView != null && (startView instanceof ImageView)) {
            ImageView iv = (ImageView) startView;
            imageView.setImageDrawable(iv.getDrawable() == null ? null : iv.getDrawable());
        }

        int xOffset = endLocArray[0] - startLocArray[0] + targetViewLayoutParams.width / 2;
        int yOffset = endLocArray[1] - startLocArray[1] + targetViewLayoutParams.height / 2;
        TranslateAnimation translateAnimationX = new TranslateAnimation(0,
                xOffset, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, yOffset);
        translateAnimationY.setInterpolator(new LinearInterpolator());
        translateAnimationY.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);

        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.2f, 1.0f, 0.2f);
        scaleAnimation.setInterpolator(new LinearInterpolator());
        scaleAnimation.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(scaleAnimation);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(800);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (listener != null) {
                    listener.onAnimationStart(animation);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imageView.setVisibility(View.GONE);
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(set);
    }

    public interface AddTaskAnimationListener {

        void onAnimationStart(Animation animation);

        void onAnimationEnd(Animation animation);
    }


}
