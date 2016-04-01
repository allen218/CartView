package com.example.mac.cartview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * Created by allen on 16/3/30.
 */
public class CartView extends RelativeLayout {
    private Button mCartBtn;

    private TextView detail_addcart_anim_tv, detail_cart_num_tv;

    private View detail_cart_layout;

    private Drawable mCartDrawable;

    private ImageView mCart;

    private Scroller mScroller;

    public CartView(Context context) {
        super(context);
    }

    public CartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(attrs);

        mScroller = new Scroller(getContext());

    }

    private int currentViewX;
    private int currentViewY;

    public void setViewDefaultLocation(int x, int y) {
        currentViewX = x;
        currentViewY = y;
    }

    private int[] mLocations;


    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cart_layout, null);
        mCartBtn = (Button) view.findViewById(R.id.detail_cart_btn);
        detail_addcart_anim_tv = (TextView) view.findViewById(R.id.detail_addcart_anim_tv);
        detail_cart_layout = view.findViewById(R.id.detail_cart_layout);
        detail_cart_num_tv = (TextView) view.findViewById(R.id.detail_cart_num_tv);

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CartView);
        mCartDrawable = typedArray.getDrawable(R.styleable.CartView_cartScr);

        if (mCartDrawable == null) {
            throw new RuntimeException("必须为CartView添加CartScr属性");
        }

        mCart = new ImageView(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(dip2px(getContext(), 30),
                dip2px(getContext(), 30));
        mCart.setLayoutParams(layoutParams);

        mCart.setImageDrawable(mCartDrawable);

        typedArray.recycle();

        addView(view);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) (ev.getRawX() + 0.5);
            mDownY = (int) (ev.getRawY() + 0.5);
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int mDownX;
    private int mDownY;
    private int mDiffX;
    private int mDiffY;
    private int mMoveX;
    private int mMoveY;

    private int mCurrentX;
    private int mCurrentY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mDownX = (int) (event.getRawX() + 0.5);
                mDownY = (int) (event.getRawY() + 0.5);

                break;

            case MotionEvent.ACTION_MOVE:

                mMoveX = (int) (event.getRawX() + 0.5);
                mMoveY = (int) (event.getRawY() + 0.5);

                handleEgdeValue(mMoveX, mMoveY);

                mDiffX = mDownX - mMoveX;
                mDiffY = mDownY - mMoveY;


                moveSelf();

                mDownX = mMoveX;
                mDownY = mMoveY;
                break;

            case MotionEvent.ACTION_UP:
                mCurrentX = (int) (event.getRawX() + 0.5f);
                mCurrentY = (int) (event.getRawY() + 0.5f);
                mLocations = new int[2];
                getLocationOnScreen(mLocations);

                moveSelfToEdge();
                break;
        }
        return false;
    }

    private int mCurrentCartLocationX;

    private void moveSelfToEdge() {
        if (getCurrentRawX() > scrWidth / 2) {
            //滑动到右边
//            ((ViewGroup) getParent()).scrollTo(0, -getCurrentRawY());
            mScroller.startScroll(0, 0, 0, getCurrentRawY());
            mCurrentCartLocationX = scrWidth - getWidth();

        } else {
            //滑动到左边
            mScroller.startScroll(scrWidth - getWidth(), 0, 0, getCurrentRawY());
//            ((ViewGroup) getParent()).scrollTo(scrWidth - getWidth(), -getCurrentRawY());
            mCurrentCartLocationX = 0;
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            ((ViewGroup) getParent()).scrollTo(mScroller.getCurrX(), -getCurrentRawY());
            postInvalidate();
        }
        super.computeScroll();
    }

    private static final int DIFF_LEFT_WIDTH_VALUE = 80;
    private static final int DIFF_RIGHT_WIDTH_VALUE = 150;
    private static final int DIFF_TOP_HEIGHT_VALUE = 450;
    private static final int DIFF_BOTTOM_HEIGHT_VALUE = 150;

    /**
     * 处理边缘购物车滑出问题
     *
     * @param moveX
     * @param moveY
     */
    private int scrWidth;
    private int scrHeight;

    private void handleEgdeValue(int moveX, int moveY) {

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        scrWidth = wm.getDefaultDisplay().getWidth();
        scrHeight = wm.getDefaultDisplay().getHeight();

        if (mMoveX < DIFF_LEFT_WIDTH_VALUE) {
            mMoveX = DIFF_LEFT_WIDTH_VALUE;
        }

        if (mMoveY < DIFF_TOP_HEIGHT_VALUE) {
            mMoveY = DIFF_TOP_HEIGHT_VALUE;
        }

        if (mMoveX > scrWidth - DIFF_RIGHT_WIDTH_VALUE) {
            mMoveX = scrWidth - DIFF_RIGHT_WIDTH_VALUE;
        }

        if (mMoveY > scrHeight - DIFF_BOTTOM_HEIGHT_VALUE) {
            mMoveY = scrHeight - DIFF_BOTTOM_HEIGHT_VALUE;
        }
    }


    private void moveSelf() {

        ((ViewGroup) getParent()).scrollBy(mDiffX, mDiffY);
    }

    public int getCurrentRawX() {

        if (mLocations == null || mLocations.length <= 0) {
            return currentViewX + 20;
        } else
            return mLocations[0] + 20;
    }


    public int getCurrentRawY() {

        if (mLocations == null || mLocations.length <= 0) {
            return currentViewY - 230;
        } else
            return mLocations[1] - 230;

    }

/*    public void setAddOneAnimation(final Activity act, final long second) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                handleAddOne(act, second);
            }
        }, second - 100);

    }*/

    private Activity mAct;

    private void handleAddOne() {
//        mAct = act;
//        act.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                showAndHideAnimation(second);
//            }
//        });
        showAndHideAnimation();


    }

    /**
     * 购物车抖动
     * @param targetView
     */
    private void shakeAnimation(View targetView) {
        TranslateAnimation shakeAnimation = new TranslateAnimation(0, 5, 0, 10);
        shakeAnimation.setInterpolator(getContext(), R.anim.shake_cycle);
        shakeAnimation.setDuration(500);
        targetView.startAnimation(shakeAnimation);


    }

    private void showAndHideAnimation() {
        detail_addcart_anim_tv.setVisibility(VISIBLE);

        TranslateAnimation moveAnimation = new TranslateAnimation(0, 50, 0, 50);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.8f, 1.0f, 0.8f);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(moveAnimation);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setDuration(500);
        detail_addcart_anim_tv.startAnimation(animationSet);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handleAddOneAlphaAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    private int currentNum;

    private void handleAddOneAlphaAnimation() {

        currentNum++;

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(500);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                detail_addcart_anim_tv.setVisibility(GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        detail_addcart_anim_tv.startAnimation(alphaAnimation);
        detail_cart_num_tv.setText(currentNum + "");


    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private static int ADD_CART_TIME = 900;

    public void handleAddCartAnimation(final View view) {

        view.setVisibility(VISIBLE);
       /* TranslateAnimation translateAnimation = new TranslateAnimation(0, getCurrentRawX(),
                0, getCurrentRawY());
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                mAct.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        view.setVisibility(GONE);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        translateAnimation.setDuration(2000);
        translateAnimation.setFillAfter(true);
        view.startAnimation(translateAnimation);*/
        ObjectAnimator translateXAnimator;
        if (mCurrentCartLocationX != 0) {
            translateXAnimator = ObjectAnimator.ofFloat(view, "translationX", scrWidth - scrWidth / 2, mCurrentCartLocationX - mCurrentCartLocationX / 3,
                    mCurrentCartLocationX - mCurrentCartLocationX / 4,
                    mCurrentCartLocationX - mCurrentCartLocationX / 5,
                    mCurrentCartLocationX);
            translateXAnimator.setInterpolator(new AccelerateInterpolator());
        } else {
            int halfScrWidth = scrWidth / 2 - getWidth();
            translateXAnimator = ObjectAnimator.ofFloat(view, "translationX", scrWidth - scrWidth / 2, halfScrWidth - halfScrWidth / 5,
                    halfScrWidth - halfScrWidth / 4,
                    halfScrWidth - halfScrWidth / 3,
                    mCurrentCartLocationX);
            translateXAnimator.setInterpolator(new AccelerateInterpolator());
        }

        ObjectAnimator translateYAnimator = ObjectAnimator.ofFloat(view, "translationY", scrHeight - scrHeight / 4, getCurrentRawY() - getCurrentRawY() / 3,
                getCurrentRawY() - getCurrentRawY() / 4,
                getCurrentRawY() - getCurrentRawY() / 5,
                getCurrentRawY());
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
                handleAddOne();
            }
        }, ADD_CART_TIME - 100);



        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                shakeAnimation(CartView.this);
                view.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    public void startAddCartAnimation(Activity act, View view) {
        mAct = act;
        handleAddCartAnimation(view);
    }

    private void handleAddCartAnimation1(final View view) {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, getCurrentRawX() - 300, 0, getCurrentRawY() + 300);
        translateAnimation.setDuration(200);
        translateAnimation.setFillAfter(true);
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handleAddCartAnimation(view);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(translateAnimation);
    }

}
