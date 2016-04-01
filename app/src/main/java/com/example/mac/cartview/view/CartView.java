package com.example.mac.cartview.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.example.mac.cartview.R;

/**
 * Created by allen on 16/3/30.
 */
public class CartView extends RelativeLayout {
    private Button mCartBtn;

    private TextView detail_addcart_anim_tv, detail_cart_num_tv;

    private Scroller mScroller;

    private CarViewConfig mConfig;

    public CartView(Context context) {
        super(context);
    }

    public CartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();

        mScroller = new Scroller(getContext());

        mConfig = new CarViewConfig(getContext(), this);

    }

    private int currentViewX;
    private int currentViewY;

    public void setViewDefaultLocation(int x, int y) {
        currentViewX = x;
        currentViewY = y;
    }

    private int[] mLocations;


    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cart_layout, null);
        mCartBtn = (Button) view.findViewById(R.id.detail_cart_btn);
        detail_addcart_anim_tv = (TextView) view.findViewById(R.id.detail_addcart_anim_tv);
        detail_cart_num_tv = (TextView) view.findViewById(R.id.detail_cart_num_tv);

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


    private Activity mAct;


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 这里view必须要由使用此控件的Activity传递过来,因为此view是要在Activity上做动画
     *
     * @param view
     */
    public void handleAddCartAnimation(final View view) {

        mConfig.handleAddCartAnimation(view, scrWidth, mCurrentCartLocationX, getWidth(),
                scrHeight, getCurrentRawY(), detail_addcart_anim_tv, detail_cart_num_tv);
    }

    public void startAddCartAnimation(Activity act, View view) {
        mAct = act;
        handleAddCartAnimation(view);
    }

    public View getThisView() {
        return CartView.this;
    }

}
