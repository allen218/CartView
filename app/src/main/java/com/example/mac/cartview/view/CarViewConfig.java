package com.example.mac.cartview.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.example.mac.cartview.R;

/**
 * Created by mac on 16/4/1.
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

        TranslateAnimation moveAnimation = new TranslateAnimation(0, 50, 0, 50);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(moveAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(500);
        view.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handleAddOneAlphaAnimation(view, numView);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private int currentNum;

    /**
     * +1消失的动画
     *
     * @param view
     */
    public void handleAddOneAlphaAnimation(final View view, View numView) {

        currentNum++;

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(alphaAnimation);
        if (numView instanceof TextView) {
            ((TextView) numView).setText(currentNum + "");
        }


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
