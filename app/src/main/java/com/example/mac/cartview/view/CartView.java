package com.example.mac.cartview.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.widget.Toast;

import com.example.mac.cartview.R;

/**
 * Created by allen on 16/3/30.
 */
public class CartView extends RelativeLayout implements View.OnClickListener {
    private Button mCartBtn;

    private TextView detail_addcart_anim_tv, detail_cart_num_tv;

    private Scroller mScroller;

    private CarViewConfig mConfig;

    private static final int LOCATION_CENTER = 1;
    private static final int LOCATION_TOP = 0;
    private static final int LOCATION_BOTTOM = 2;

    public CartView(Context context) {
        super(context);
    }

    public CartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(attrs);

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


    private void initView(AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cart_layout, null);
        mCartBtn = (Button) view.findViewById(R.id.detail_cart_btn);
        detail_addcart_anim_tv = (TextView) view.findViewById(R.id.detail_addcart_anim_tv);
        detail_cart_num_tv = (TextView) view.findViewById(R.id.detail_cart_num_tv);
        getResourceFromXML(attrs);

        addView(view);
    }


    private void getResourceFromXML(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CartView);
        currentViewLocation = typedArray.getInt(R.styleable.CartView_YLocation, -1);

        if (currentViewLocation == -1) {
            currentViewEnumLocation = typedArray.getInt(R.styleable.CartView_YEnumLocation, -1);
            if (currentViewEnumLocation == -1) {

                throw new RuntimeException("请在xml里配置CartView_YLocation或者CartView_YEnumLocation属性");
            }
        }

        typedArray.recycle();
    }

    /**
     * 处理xml参数为enum时,所对应的值
     */
    private void handleEnumLocation() {
        switch (currentViewEnumLocation) {
            case LOCATION_TOP:
                currentViewLocation = 0;
                break;

            case LOCATION_CENTER:
                currentViewLocation = scrHeight / 2;
                break;

            case LOCATION_BOTTOM:

                currentViewLocation = scrHeight;
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mDownX = (int) (ev.getRawX() + 0.5);
            mDownY = (int) (ev.getRawY() + 0.5);

            mRawDownY = mDownY;
            mRawDownX = mDownX;
        }
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            mMoveX = (int) (ev.getRawX() + 0.5f);
            mMoveY = (int) (ev.getRawY() + 0.5f);
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private int mDownX;
    private int mRawDownX;
    private int mDownY;
    private int mRawDownY;
    private int mDiffX;
    private int mDiffY;
    private int mMoveX;
    private int mMoveY;

    private int mUpDiffX;
    private int mUpDiffY;

    private int mCurrentX;
    private int mCurrentY;
    private static final int MOVE_DIFFY = 100;
    private static final int DiffClickOrTouch = 30;

    private int currentViewLocation;

    private int currentViewEnumLocation;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:

                mMoveX = (int) (event.getRawX() + 0.5);
                mMoveY = (int) (event.getRawY() + 0.5);

                mDiffX = mDownX - mMoveX;
                mDiffY = mDownY - mMoveY;


                handleTopEdge();

                mDownX = mMoveX;
                mDownY = mMoveY;
                break;

            case MotionEvent.ACTION_UP:
                mCurrentX = (int) (event.getRawX() + 0.5f);
                mCurrentY = (int) (event.getRawY() + 0.5f);
                mLocations = new int[2];
                getLocationOnScreen(mLocations);

                handleEgdeValue(mMoveX, mMoveY);

                mUpDiffX = Math.abs(mCurrentX - mRawDownX);
                mUpDiffY = Math.abs(mCurrentY - mRawDownY);

                moveSelfToEdge();

                if (mUpDiffX > DiffClickOrTouch ||
                        mUpDiffY > DiffClickOrTouch) {
                    //判断是否是滑动
                    return true;
                }
                //点击事件处理
                if (mListener != null) {
                    mListener.onclick(this);
                }
                break;
        }
        return false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        scrWidth = wm.getDefaultDisplay().getWidth();
        scrHeight = wm.getDefaultDisplay().getHeight();
        switch (currentViewEnumLocation) {
            case LOCATION_TOP:
                break;

            case LOCATION_CENTER:
                mCurrentCartLocationX = scrWidth - getWidth() - DIFF_BOTTOM_HEIGHT_VALUE;
                break;

            case LOCATION_BOTTOM:
                break;
        }
    }

    /**
     * 处理顶部边界问题
     */
    private void handleTopEdge() {

//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        scrWidth = wm.getDefaultDisplay().getWidth();
//        scrHeight = wm.getDefaultDisplay().getHeight();

        handleEnumLocation();

        if (mMoveY < 200) {
            moveEdge();
        } else
            moveSelf();
    }

    private void moveEdge() {
        ((ViewGroup) getParent()).scrollTo(scrWidth - mMoveX - getWidth(), 0 + currentViewLocation - getHeight());
    }

    private int mCurrentCartLocationX;


    private void moveSelfToEdge() {
        if (mUpDiffX > DiffClickOrTouch || mUpDiffY > DiffClickOrTouch) {
            if (getCurrentRawX() > scrWidth / 2) {
                //滑动到右边
//            ((ViewGroup) getParent()).scrollTo(0, -getCurrentRawY());
//            if (getCurrentRawY() > scrHeight - getHeight() - MOVE_DIFFY) {
//
//                mScroller.startScroll(getCurrentRawX() - scrWidth + getWidth(), getCurrentRawY() - CURRENT_VIEW_DIFFY - currentViewLocation,
//                        scrWidth - getCurrentRawX() - getWidth(), scrHeight - getCurrentRawY() - CURRENT_VIEW_DIFFY);
//
//            } else
                //

                if (getCurrentRawY() + CURRENT_VIEW_DIFFY < getHeight()) {

                    mScroller.startScroll(getCurrentRawX() - scrWidth + getWidth(), 0 - currentViewLocation + getHeight(), scrWidth - getCurrentRawX() - getWidth(), 0);

                } else {
                    mScroller.startScroll(getCurrentRawX() - scrWidth + getWidth(), getCurrentRawY() - currentViewLocation + getHeight(),
                            scrWidth - getCurrentRawX() - getWidth(), 0);
                }


                mCurrentCartLocationX = scrWidth - getWidth();

            } else {
                //滑动到左边
            /*if (getCurrentRawY() > scrHeight - getHeight() - MOVE_DIFFY) {

                mScroller.startScroll(-scrWidth + getCurrentRawX() + getWidth(), getCurrentRawY() -
                                CURRENT_VIEW_DIFFY - currentViewLocation + getHeight(),
                        -getCurrentRawX(), scrHeight - getCurrentRawY() - CURRENT_VIEW_DIFFY);

            } else*/
                if (getCurrentRawY() + CURRENT_VIEW_DIFFY < getHeight()) {

                    mScroller.startScroll(-scrWidth + getCurrentRawX() + getWidth(),
                            0 - currentViewLocation + getHeight(), -getCurrentRawX(), 0);
                } else {

                    mScroller.startScroll(-scrWidth + getCurrentRawX() + getWidth(),
                            getCurrentRawY() - currentViewLocation + getHeight(), -getCurrentRawX(), 0);
                }
                mCurrentCartLocationX = 0;
            }
        }

        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            ((ViewGroup) getParent()).scrollTo(-mScroller.getCurrX(), -mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    private int DIFF_LEFT_WIDTH_VALUE = getWidth();
    private int DIFF_RIGHT_WIDTH_VALUE = getCurrentScrWidth() - getWidth();
    private int DIFF_TOP_HEIGHT_VALUE = getHeight();
    private int DIFF_BOTTOM_HEIGHT_VALUE = 300;

    /**
     * 处理边缘购物车滑出问题
     *
     * @param moveX
     * @param moveY
     */
    private int scrWidth;
    private int scrHeight;

    private int getCurrentScrWidth() {
        return scrWidth;
    }

    private int getCurrentScrHeight() {
        return scrHeight;
    }


    private void handleEgdeValue(int moveX, int moveY) {

        if (moveX < DIFF_LEFT_WIDTH_VALUE) {
            mMoveX = DIFF_LEFT_WIDTH_VALUE;
        }

        if (moveY < DIFF_TOP_HEIGHT_VALUE) {
            mMoveY = DIFF_TOP_HEIGHT_VALUE;
        }

        if (moveX > scrWidth - getWidth()) {
            mMoveX = scrWidth - getWidth();
        }

        if (moveY > scrHeight - getHeight()) {
            mMoveY = scrHeight - getHeight();
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

    private static final int CURRENT_VIEW_DIFFY = 200;

    public int getCurrentRawY() {

        if (mLocations == null || mLocations.length <= 0) {
            return currentViewY - CURRENT_VIEW_DIFFY;
        } else
            return mLocations[1] - CURRENT_VIEW_DIFFY;

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

    @Override
    public void onClick(View v) {
        Toast.makeText(mAct, "onclick", Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置购物车的数量
     *
     * @param num
     */
    private boolean isFirstSet = true;//是否为第一次设置,因为如果是第一次的话,需要手动设置到numView上

    public void setCurrentNum(int num) {
        mConfig.setCurrentNum(num);
        if (isFirstSet) {
            detail_cart_num_tv.setText(num + "");
            isFirstSet = false;
        }
    }

    private ClickListener mListener;

    public void setClickListener(ClickListener listener) {
        mListener = listener;
    }
}
