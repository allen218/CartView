package com.example.mac.cartview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.mac.cartview.R;

/**
 * Created by allen on 16/4/1.
 */
public class CarViewConfig {
    private Context mContext;
    private CartView mCartView;

    public CarViewConfig(Context ctx, CartView cartView) {
        mContext = ctx;
        mCartView = cartView;
    }

    public void handleAddOne(View view, View numView) {
        showAndHideAnimation(view, numView);


    }

    /**
     * 加1的动画
     *
     * @param view
     */
    public void showAndHideAnimation(final View view, final View numView) {
        view.setVisibility(View.VISIBLE);

        ObjectAnimator moveXAnimation = ObjectAnimator.ofFloat(view, "translationX", 0f * getCurrentDensity(),
                8f * getCurrentDensity(), 16f * getCurrentDensity());
        ObjectAnimator moveYAnimation = ObjectAnimator.ofFloat(view, "translationY", 0f * getCurrentDensity(),
                8f * getCurrentDensity(), 16f * getCurrentDensity());
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.9f, 0.8f);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.9f, 0.8f);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.9f, 0.8f, 0.3f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(moveXAnimation, moveYAnimation, scaleXAnimation, scaleYAnimation, alphaAnimation);
        animatorSet.setDuration(500);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (numView instanceof TextView) {
                    ((TextView) numView).setText(currentNum + "");
                }
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * 获得当前屏幕密度
     *
     * @return
     */
    private float getCurrentDensity() {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metric = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metric);
        return metric.density;
    }

    private int currentNum;


    /**
     * 设置购物车的数量
     *
     * @param num
     */

    public void setCurrentNum(int num) {
        currentNum = num;

    }

    private static int ADD_CART_TIME = 900;

    /**
     * @param view                  这里view必须要由使用此控件的Activity传递过来,因为此view是要在Activity上做动画
     * @param scrWidth              屏幕宽度
     * @param mCurrentCartLocationX 当前移动的X轴位置
     * @param ViewWidth             当前CartView的宽度
     * @param scrHeight             屏幕高度
     * @param currentRawY           当前控件Y轴位置
     * @param addOneTv              +1控件
     * @param addOneNumTv           计数控件
     */
    public void handleAddCartAnimation(final View view, int scrWidth, int mCurrentCartLocationX, int ViewWidth,
                                       int scrHeight, int currentRawY, final View addOneTv, final View addOneNumTv) {

        view.setVisibility(View.VISIBLE);
        ObjectAnimator translateXAnimator;
        if (mCurrentCartLocationX != 0) {
            translateXAnimator = ObjectAnimator.ofFloat(view, "translationX", scrWidth - scrWidth / 2, mCurrentCartLocationX - mCurrentCartLocationX / 3,
                    mCurrentCartLocationX - mCurrentCartLocationX / 4,
                    mCurrentCartLocationX - mCurrentCartLocationX / 5,
                    mCurrentCartLocationX);
            translateXAnimator.setInterpolator(new AccelerateInterpolator());
        } else {
            int halfScrWidth = scrWidth / 2 - ViewWidth;
            translateXAnimator = ObjectAnimator.ofFloat(view, "translationX", scrWidth - scrWidth / 2, halfScrWidth - halfScrWidth / 5,
                    halfScrWidth - halfScrWidth / 4,
                    halfScrWidth - halfScrWidth / 3,
                    mCurrentCartLocationX);
            translateXAnimator.setInterpolator(new AccelerateInterpolator());
        }

        ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(view, "translationY", scrHeight - scrHeight / 4, currentRawY - currentRawY / 3,
                currentRawY - currentRawY / 4,
                currentRawY - currentRawY / 5,
                currentRawY);
        translateYAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 0.5f);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 0.5f);

        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0.8f);

        ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 360f, 0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(translateXAnimator).with(translateYAnimator).with(scaleXAnimator).with(scaleYAnimator).with(alphaAnimator).with(rotateAnimator);
        animatorSet.setDuration(ADD_CART_TIME);
        animatorSet.start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //+1动画
                handleAddOne(addOneTv, addOneNumTv);
            }
        }, ADD_CART_TIME - 100);


        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                shakeAnimation(mCartView.getThisView());
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 购物车抖动
     *
     * @param targetView
     */
    private void shakeAnimation(View targetView) {
        TranslateAnimation shakeAnimation = new TranslateAnimation(0, 5, 0, 10);
        shakeAnimation.setInterpolator(mContext, R.anim.shake_cycle);
        shakeAnimation.setDuration(500);
        targetView.startAnimation(shakeAnimation);


    }
}
