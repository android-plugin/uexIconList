/*
 *  Copyright (C) 2015 The AppCan Open Source Project.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.

 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.zywx.wbpalmstar.plugin.uexiconlist.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.EUExIconList;
import org.zywx.wbpalmstar.plugin.uexiconlist.IconAdapter;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.DensityUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconBean;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListOption;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.UIConfig;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Scroller;

@SuppressLint("UseSparseArrays")
public class ScrollLayout extends ViewGroup
        implements OnDataChangeListener, ConstantUtils {

    private boolean isIconShake = true;
    /** 实现图标是否抖动可配置 */
    // 容器的Adapter
    private IconAdapter mAdapter;
    // 每个Item图片宽度的半长，用于松下手指时的动画
    private int halfBitmapWidth;
    // 同上
    private int halfBitmapHeight;

    // 动态设置行数
    private int rowCount = 1;
    // 动态设置列数
    private int colCount = 1;
    // 每一页的Item总数
    private int itemPerPage = 1;
    // item的宽度
    private int childWidth = 0;
    // item的高度
    private int childHeight = 0;

    // 手机屏幕宽度
    private int screenWidth = 0;
    // 手机屏幕高度
    private int screenHeight = 0;

    // 总Item数
    private int totalItem = 0;
    // 总页数
    private int totalPage = 0;
    // 当前屏数
    private int mCurScreen;
    // 默认屏数为0，即第一屏
    private int mDefaultScreen = 0;

    // 上次位移滑动到的X坐标位置
    private float mLastMotionX;
    // 上次位移滑动到的Y坐标位置
    private float mLastMotionY;

    // 拖动点的X坐标（加上当前屏数 * screenWidth）
    private int dragPointX;
    // 拖动点的Y坐标
    private int dragPointY;
    // X坐标偏移量
    private int dragOffsetX;
    // Y坐标偏移量
    private int dragOffsetY;

    // 拖拽点的位置编号，每个Item对应一个位置编号，自增
    private int dragPosition = -1;

    // 临时交换位置的编号
    private int temChangPosition = -1;

    // window管理器，负责随手势显示拖拽View
    private WindowManager windowManager;
    private WindowManager.LayoutParams windowParams;

    // 拖拽Item的子View
    private ImageView dragImageView;
    // 拖拽View对应的位图
    private Bitmap dragBitmap;

    // 页面滚动的Scroll管理器
    private Scroller mScroller;

    // 三种滑动状态，默认为静止状态
    private int Mode_Free = 0; // 静止状态
    private int Mode_Drag = 1; // 当前页面下，拖动状态
    private int Mode_Scroll = 2; // 跨页面滚动状态
    private int Mode = Mode_Free;

    // 手势落下的X坐标
    private int startX = 0;

    // 编辑状态标识
    private boolean isEditting = false;

    private Context mContext;
    public static boolean isMove = false;

    // 系列动画执行完成标识的集合
    private HashMap<Integer, Boolean> animationMap = new HashMap<Integer, Boolean>();

    // 用来判断滑动到哪一个item的位置
    private Rect itemRect;

    // 页面滑动的监听
    private OnPageChangedListener pageChangedListener;
    // 删除或增加页面的监听
    private OnAddOrDeletePage onAddPage;
    /**
     * DragGridView的item长按响应的时间， 默认是1000毫秒，也可以自行设置
     */
    private long dragResponseMS = 1000;
    // /** * 震动器 */
    // private Vibrator mVibrator;
    // Container编辑模式的监听
    private OnEditModeListener onEditModeListener;
    private EUExIconList mEuExIconList;
    private PageIndicator mPageIndicator;
    private boolean circul = false;
    /** 循环翻页 */
    private boolean resetFrame = false;
    private boolean isDelItem = false;

    /** 反射规避江苏烟草闪屏 */
    private Method addViewInner;
    private Method removeViewInternal;

    /** 画网格线使用 */
    private Paint framePaint = null;
    private LinkedList<FrameCoordinate> frameCoorList = new LinkedList<FrameCoordinate>();
    private int mCount = 0;
    private final static float[] DEGREE = { 1.8f, -2.0f, 2.0f, -1.5f, 1.5f };
    private Runnable mLongPressRunnable = new Runnable() {
        public void run() {
            if (!isEditting) {
                View view = mAdapter.getView(dragPosition);
                IconBean iconBean = (IconBean) view.getTag();
                if ((view != null) && iconBean.getIsCanDel()) {
                    mEuExIconList.mEuExIconListHandler
                            .send2Callback(WHAT_ON_LONG_PRESS, null);
                    showEdit(true);
                    if (isIconShake) {
                        shakeAnimation();
                    }
                }
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WHAT_RESTART_SHAKE_DELAY:
                shakeAnimation();
                if (isDelItem) {
                    isDelItem = false;
                }
                break;
            default:
                break;
            }
        }
    };

    public ScrollLayout(Context context) {
        super(context);
        init(context);
    }

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    // 初始化成员变量，同时设置OnClick监听
    private void init(Context context) {
        this.mContext = context;
        this.mScroller = new Scroller(context);
        this.mCurScreen = mDefaultScreen;
        if (mAdapter != null) {
            refreView();
        }
        windowManager = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        if (IconListOption.isInvalidateChild()) {
            addViewInner = IconListUtils.getReflectionMethod(
                    "android.view.ViewGroup", "addViewInner",
                    new Class<?>[] { View.class, int.class, LayoutParams.class,
                            boolean.class });
            removeViewInternal = IconListUtils.getReflectionMethod(
                    "android.view.ViewGroup", "removeViewInternal",
                    new Class<?>[] { int.class, View.class });
        }
    }

    public void initFramePaint() {
        if (UIConfig.isShowIconFrame()) {
            framePaint = new Paint();// 创建一个画笔
            framePaint.setStrokeWidth(UIConfig.getIconFrameWidth());
            framePaint.setColor(UIConfig.getIconFrameColor());
        }
    }

    // 添加一个item
    public void addItemView(IconBean item) {
        boolean isAddEnd = mAdapter.add(item);
        int viewItemSize = mAdapter.getCount();
        int addPos = 0;
        if (isAddEnd) {
            addPos = viewItemSize - 1;
        } else {
            addPos = viewItemSize - 2;
        }
        this.addView(getView(addPos), addPos);
        showEdit(isEditting);
        if (isEditting) {
            shakeAnimation(getChildAt(addPos));
        }
        requestLayout();
        mCurScreen = addPos / itemPerPage;
        refreshPageindictor(totalPage, mCurScreen);
        snapToScreen(mCurScreen);
    }

    private void refreshPageindictor(int pagSize, int curPage) {
        mPageIndicator.setTotalPageSize(pagSize);
        mPageIndicator.setCurrentPage(curPage);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
        child.setClickable(true);
        if (child.getVisibility() != View.VISIBLE) {
            child.setVisibility(View.VISIBLE);
        }
        if (IconListOption.isInvalidateChild() && addViewInner != null) {
            requestLayout();
            invalidate();
            try {
                addViewInner.invoke(this, child, index, params, false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            super.addView(child, index, params);
        }
        int pages = (int) Math.ceil(getChildCount() * 1.0 / itemPerPage);
        if (pages > totalPage) {
            if (this.onAddPage != null) {
                onAddPage.onAddOrDeletePage(totalPage, true);
            }
            totalPage = pages;
        }
    }

    public void preResetFrame() {
        resetFrame = true;
        clearChildrenAnimation();
    }

    public void resetScrollLayout() {
        removeAllViews();
        requestLayout();
        // invalidate();
        frameCoorList.clear();
        totalPage = 0;
    }

    public void resetFrameCompleted() {
        resetFrame = false;
        if (isEditting) {
            showEdit(true);
            if (isIconShake) {
                shakeAnimation();
            }
        }
    }

    // 绘制Container所有item
    public void refreView() {
        removeAllViews();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            this.addView(getView(i));
        }
        totalPage = (int) Math.ceil(getChildCount() * 1.0 / itemPerPage);
        requestLayout();
    }

    @Override
    public void removeView(View view) {
        removeViewAt(indexOfChild(view));
        // if(IconListOption.isInvalidateChild())
        // {
        //
        // }
        // else
        // {
        // super.removeView(view);
        // }
        // int pages = (int) Math.ceil(getChildCount() * 1.0 / itemPerPage);
        // if (pages < totalPage) {
        // if (this.onAddPage != null)
        // {
        // onAddPage.onAddOrDeletePage(totalPage, false);
        // }
        // totalPage = pages;
        // }
    }

    @Override
    public void removeViewAt(int index) {
        if (IconListOption.isInvalidateChild()) {
            try {
                if (removeViewInternal != null) {
                    removeViewInternal.invoke(this, index, getChildAt(index));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            requestLayout();
            invalidate();
        } else {
            super.removeViewAt(index);
        }
        int pages = (int) Math.ceil(getChildCount() * 1.0 / itemPerPage);
        if (pages < totalPage) {
            totalPage = pages;
            if (this.onAddPage != null) {
                onAddPage.onAddOrDeletePage(totalPage, false);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /** 禁止父控件拦截手势事件 */
        getParent().requestDisallowInterceptTouchEvent(true);
        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();
        int thresholdX = DensityUtil.dip2px(mContext, 8);
        int thresholdY = DensityUtil.dip2px(mContext, 8);
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            isMove = false;
            startX = (int) x;
            if (mScroller.isFinished()) {
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                temChangPosition = dragPosition = pointToPosition((int) x,
                        (int) y);
                postDelayed(mLongPressRunnable, dragResponseMS);
                dragOffsetX = (int) (ev.getRawX() - x);
                dragOffsetY = (int) (ev.getRawY() - y);

                mLastMotionX = x;
                mLastMotionY = y;
                startX = (int) x;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            // if (enableBounce) {
            // disableBounce();
            // }
            int deltaX = (int) (mLastMotionX - x);
            if ((Math.abs(deltaX) > thresholdX)
                    || (Math.abs((int) (mLastMotionY - y)) > thresholdY)) {
                removeCallbacks(mLongPressRunnable);
            }
            if (IsCanMove(deltaX) && Math.abs(deltaX) > thresholdX
                    && Mode != Mode_Drag) {
                mLastMotionX = x;
                scrollBy(deltaX, 0);
                Mode = Mode_Scroll;
                isMove = true;
            }
            if (Mode == Mode_Drag) {
                int pos = pointToPosition((int) x, (int) y);
                IconBean iconBean = null;
                if (-1 != pos) {
                    View view = getChildAt(pos);
                    if (view != null) {
                        iconBean = (IconBean) view.getTag();
                        if (iconBean.getIsCanMove()) {
                            onDrag((int) x, (int) y);
                        }
                    } else {
                        onDrag((int) x, (int) y);
                    }
                } else {
                    onDrag((int) x, (int) y);
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            float distance = ev.getRawX() - startX;
            if (!circul) {
                if (distance > screenWidth / 6 && mCurScreen > 0
                        && Mode != Mode_Drag) {
                    snapToScreen(mCurScreen - 1);
                } else if (distance < -screenWidth / 6
                        && mCurScreen < totalPage - 1 && Mode != Mode_Drag) {
                    snapToScreen(mCurScreen + 1);
                } else if (Mode != Mode_Drag) {
                    snapToDestination();
                }
            } else {
                if (distance > screenWidth / 6 && mCurScreen >= 0
                        && Mode != Mode_Drag) {
                    mCurScreen = (0 == mCurScreen) ? totalPage : mCurScreen;
                    snapToScreen(mCurScreen - 1);
                } else if (distance < -screenWidth / 6
                        && mCurScreen <= totalPage - 1 && Mode != Mode_Drag) {
                    mCurScreen = ((totalPage - 1) == mCurScreen) ? -1
                            : mCurScreen;
                    snapToScreen(mCurScreen + 1);
                } else if (Mode != Mode_Drag) {
                    snapToDestination();
                }
            }
            if (Mode == Mode_Drag) {
                stopDrag();
            }
            if (dragImageView != null) {
                animationMap.clear();
                showDropAnimation((int) x, (int) y);
            }
            startX = 0;
            removeCallbacks(mLongPressRunnable);
            // if (!enableBounce) {
            // enableBounce();
            // }
            break;
        case MotionEvent.ACTION_CANCEL:
            removeCallbacks(mLongPressRunnable);
            // if (!enableBounce) {
            // enableBounce();
            // }
            break;
        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    // private void enableBounce() {
    // mEuExIconList.mEuExIconListHandler.send2Callback(WHAT_ON_TOUCH_UP,
    // null);
    // enableBounce = true;
    // }
    //
    // private void disableBounce() {
    // mEuExIconList.mEuExIconListHandler.send2Callback(WHAT_ON_TOUCH_DOWN,
    // null);
    // enableBounce = false;
    // }

    // 开始拖动
    private void startDrag(Bitmap bm, int x, int y, View itemView) {
        dragPointX = x - itemView.getLeft() + mCurScreen * screenWidth;
        dragPointY = y - itemView.getTop();
        windowParams = new WindowManager.LayoutParams();

        windowParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowParams.x = x - dragPointX + dragOffsetX;
        windowParams.y = y - dragPointY + dragOffsetY;
        windowParams.height = LayoutParams.WRAP_CONTENT;
        windowParams.width = LayoutParams.WRAP_CONTENT;
        windowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        windowParams.format = PixelFormat.TRANSLUCENT;
        windowParams.windowAnimations = 0;
        windowParams.alpha = 0.8f;

        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(bm);
        dragBitmap = bm;
        windowManager.addView(iv, windowParams);
        dragImageView = iv;
        Mode = Mode_Drag;

        halfBitmapWidth = bm.getWidth() / 2;
        halfBitmapHeight = bm.getHeight() / 2;
    }

    // 停止拖动
    private void stopDrag() {
        if (Mode == Mode_Drag) {
            View view = getChildAt(dragPosition);
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
                if (isIconShake) {
                    mHandler.sendEmptyMessageDelayed(WHAT_RESTART_SHAKE_DELAY,
                            RESET_FRAME_DEALY);
                }
                if (UIConfig.isShowIconFrame()) {
                    mAdapter.setItemShapeVisible(view, false);
                }
            }
            Mode = Mode_Free;
            mContext.sendBroadcast(new Intent("com.stg.menu_move"));
        }
    }

    // 使用Map集合记录，防止动画执行混乱
    private class NotifyDataSetListener implements AnimationListener {
        private int movedPosition;

        public NotifyDataSetListener(int primaryPosition) {
            this.movedPosition = primaryPosition;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (animationMap.containsKey(movedPosition)) {
                // remove from map when end
                animationMap.remove(movedPosition);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // put into map when start
            animationMap.put(movedPosition, true);
        }
    }

    // 返回滑动的位移动画，比较复杂，有兴趣的可以看看
    private Animation animationPositionToPosition(int oldP, int newP,
            boolean isCrossScreen, boolean isForward) {
        PointF oldPF = positionToPoint2(oldP);
        PointF newPF = positionToPoint2(newP);

        TranslateAnimation animation = null;

        // when moving forward across pages,the first item of the new page moves
        // backward
        if (oldP != 0 && (oldP + 1) % itemPerPage == 0 && isForward) {
            animation = new TranslateAnimation(screenWidth - oldPF.x, 0,
                    DensityUtil.dip2px(mContext, 25) - screenHeight, 0);
            animation.setDuration(800);
        }
        // when moving backward across pages,the last item of the new page moves
        // forward
        else if (oldP != 0 && oldP % itemPerPage == 0 && isCrossScreen
                && !isForward) {
            animation = new TranslateAnimation(newPF.x - screenWidth, 0,
                    screenHeight - DensityUtil.dip2px(mContext, 25), 0);
            animation.setDuration(800);
        }
        // regular animation between two neighbor items
        else {
            animation = new TranslateAnimation(newPF.x - oldPF.x, 0,
                    newPF.y - oldPF.y, 0);
            animation.setDuration(500);
        }
        animation.setFillAfter(true);
        animation.setAnimationListener(new NotifyDataSetListener(oldP));

        return animation;
    }

    // 滑动合法性的判断，防止滑动到空白区域
    private boolean IsCanMove(int deltaX) {
        if (!circul) {
            if (getScrollX() <= 0 && deltaX < 0) {
                return false;
            }
            if (getScrollX() >= (totalPage - 1) * screenWidth && deltaX > 0) {
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    // 判断滑动的一系列动画是否有冲突
    private boolean isMovingFastConflict(int moveNum) {
        int itemsMoveNum = Math.abs(moveNum);
        int temp = dragPosition;
        for (int i = 0; i < itemsMoveNum; i++) {
            int holdPosition = moveNum > 0 ? temp + 1 : temp - 1;
            if (animationMap.containsKey(holdPosition)) {
                return true;
            }
            temp = holdPosition;
        }
        return false;
    }

    // 执行位置动画
    private void movePostionAnimation(int oldP, int newP) {
        int moveNum = newP - oldP;
        boolean isCrossScreen = false;
        boolean isForward = false;
        if (moveNum != 0 && !isMovingFastConflict(moveNum)) {
            int absMoveNum = Math.abs(moveNum);
            for (int i = Math.min(oldP, newP) + 1; i <= Math.max(oldP,
                    newP); i++) {
                if (i % 8 == 0) {
                    isCrossScreen = true;
                }
            }
            if (isCrossScreen) {
                isForward = moveNum < 0 ? false : true;
            }
            for (int i = 0; i < absMoveNum; i++) {
                int holdPosition = (moveNum > 0) ? oldP + 1 : oldP - 1;
                View view = getChildAt(holdPosition);
                if (view != null) {
                    view.startAnimation(animationPositionToPosition(oldP,
                            holdPosition, isCrossScreen, isForward));
                }
                oldP = holdPosition;
            }
        }
    }

    public int getChildIndex(View view) {
        if (view != null && view.getParent() instanceof ScrollLayout) {
            final int childCount = ((ScrollLayout) view.getParent())
                    .getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (view == ((ScrollLayout) view.getParent()).getChildAt(i)) {
                    return i;
                }
            }
        }
        return -1;
    }

    // 获取特定position下的item View
    private View getView(final int position) {
        View view = null;
        if (mAdapter != null) {
            view = mAdapter.getView(position);
            view.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClick(v);
                }
            });
        }
        return view;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public int getColCount() {
        return colCount;
    }

    public void setCurrentPage(int page) {
        mCurScreen = page;
        snapToScreen(page);
    }

    public int getCurrentPage() {
        return mCurScreen;
    }

    public OnAddOrDeletePage getOnCaculatePage() {
        return onAddPage;
    }

    public OnEditModeListener getOnEditModeListener() {
        return onEditModeListener;
    }

    public OnPageChangedListener getOnPageChangedListener() {
        return pageChangedListener;
    }

    public int getRowCount() {
        return rowCount;
    }

    public IconAdapter getSaAdapter() {
        return mAdapter;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public int getTotalPage() {
        return totalPage;
    }

    @Override
    public void ondataChange() {
        refreView();
    }

    @Override
    public void addView(View child, int index) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = generateDefaultLayoutParams();
            if (params == null) {
                throw new IllegalArgumentException(
                        "generateDefaultLayoutParams() cannot return null");
            }
        }
        addView(child, index, params);
    }

    // 根据手势绘制不断变化位置的dragView
    private void onDrag(int x, int y) {
        if (dragImageView != null) {
            windowParams.alpha = 0.8f;
            windowParams.x = x - dragPointX + dragOffsetX;
            windowParams.y = y - dragPointY + dragOffsetY;
            windowManager.updateViewLayout(dragImageView, windowParams);
        }
        int tempPosition = pointToPosition(x, y);
        if (tempPosition != -1) {
            dragPosition = tempPosition;
        }
        View view = getChildAt(temChangPosition);
        if (view == null) {
            stopDrag();
            return;
        }
        if (isIconShake) {
            view.clearAnimation();
        }
        if (!IconListOption.isInvalidateChild()) {
            view.setVisibility(View.INVISIBLE);
        }
        if (temChangPosition != dragPosition) {
            View dragView = getChildAt(temChangPosition);
            if (isIconShake) {
                clearChildrenAnimation();
            }

            movePostionAnimation(temChangPosition, dragPosition);
            removeViewAt(temChangPosition);
            addView(dragView, dragPosition);
            getChildAt(dragPosition).setVisibility(View.INVISIBLE);
            this.getSaAdapter().exchange(temChangPosition, dragPosition);
            temChangPosition = dragPosition;
        }

        if (x > getRight() - DensityUtil.dip2px(mContext, 25)
                && mCurScreen < totalPage - 1 && mScroller.isFinished()
                && x - startX > 10) {
            snapToScreen(mCurScreen + 1, false);
        } else if (x - getLeft() < DensityUtil.dip2px(mContext, 35)
                && mCurScreen > 0 && mScroller.isFinished()
                && x - startX < -10) {
            snapToScreen(mCurScreen - 1, false);
        }
    }

    public boolean onItemLongClick(View v) {
        if (mScroller.isFinished() && isEditting) {
            v.destroyDrawingCache();
            v.setDrawingCacheEnabled(true);
            if (onEditModeListener != null) {
                onEditModeListener.onEdit();
            }
            if (UIConfig.isShowIconFrame()) {
                mAdapter.setItemShapeVisible(v, true);
            }
            Bitmap bm = Bitmap.createBitmap(v.getDrawingCache());
            IconBean iconBean = (IconBean) v.getTag();
            if (iconBean.getIsCanMove()) {
                if (isIconShake) {
                    v.clearAnimation();
                }
                v.setVisibility(View.GONE);
                startDrag(bm, (int) (mLastMotionX), (int) (mLastMotionY), v);
            }
            // mVibrator.vibrate(50); //震动一下
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (UIConfig.isShowIconFrame()) {
            for (FrameCoordinate frame : frameCoorList) {
                canvas.drawLine(frame.startX, frame.startY, frame.stopX,
                        frame.stopY, framePaint);
            }
        }
    }

    public void saveFrame(int page) {
        int startPos = page * itemPerPage;
        int endPos = startPos + itemPerPage - 1;
        for (int i = startPos; i <= endPos; i++) {
            FrameCoordinate frameCoor = null;
            int row = i / colCount % rowCount;
            int col = i % colCount;
            float left = page * UIConfig.getScaleWidth()
                    + col * UIConfig.getIconScaleWidth();
            float top = row * UIConfig.getGridScaleHight();
            /** 画竖框 */
            if ((0 == row) && ((col > 0) && (col < colCount))) {
                frameCoor = new FrameCoordinate(left, top, left,
                        top + rowCount * UIConfig.getGridScaleHight());
                frameCoorList.add(frameCoor);
                // LogUtils.logDebug(LogUtils.DEBUG, "竖线row = " + row
                // + " col = " + col + " frameCoor:" + frameCoor.toString());
            }
            /** 画横框 */
            if ((0 == col) && (row != 0)) {
                frameCoor = new FrameCoordinate(left, top,
                        left + UIConfig.getScaleWidth(), top);
                frameCoorList.add(frameCoor);
                // LogUtils.logDebug(LogUtils.DEBUG, "横线row = " + row
                // + " col = " + col + " frameCoor:" + frameCoor.toString());
            }
        }
        /** 每页最后加横框 */
        FrameCoordinate lastHorLine = frameCoorList.getLast();
        FrameCoordinate frameCoor = null;
        float lastHorLineYOffset = UIConfig.getIconFrameWidth() / 2;
        /** lastHorLine 为横线 */
        if (rowCount != 1) {
            frameCoor = new FrameCoordinate(lastHorLine.startX,
                    lastHorLine.startY + (UIConfig.getGridScaleHight()
                            - lastHorLineYOffset),
                    lastHorLine.stopX,
                    lastHorLine.stopY + (UIConfig.getGridScaleHight()
                            - lastHorLineYOffset));
        }
        /** lastHorLine 为竖线 */
        else {
            frameCoor = new FrameCoordinate(page * UIConfig.getScaleWidth(),
                    lastHorLine.startY + (UIConfig.getGridScaleHight()
                            - lastHorLineYOffset),
                    (page + 1) * UIConfig.getScaleWidth(),
                    lastHorLine.startY + (UIConfig.getGridScaleHight()
                            - lastHorLineYOffset));
        }
        // frameCoor.startY += UIConfig.getGridScaleHight() - frameWidth;
        // frameCoor.stopY += UIConfig.getGridScaleHight() - frameWidth;
        // LogUtils.logDebug(LogUtils.DEBUG, "横线 frameCoor:" +
        // frameCoor.toString());
        frameCoorList.add(frameCoor);
    }

    class FrameCoordinate {
        private float startX;
        private float startY;
        private float stopX;
        private float stopY;

        public FrameCoordinate(float startX, float startY, float stopX,
                float stopY) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

        public String toString() {
            return ("starx = " + startX + " stary = " + startY + " stopx = "
                    + stopX + " stopy = " + stopY);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != View.GONE) {
                childWidth = childView.getMeasuredWidth();
                childHeight = childView.getMeasuredHeight();
                int page = i / itemPerPage;

                int row = i / colCount % rowCount;
                int col = i % colCount;
                int left = page * screenWidth + col * childWidth;
                int top = row * childHeight;
                childView.layout(left, top, left + childWidth,
                        top + childView.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = MeasureSpec.getSize(widthMeasureSpec);
        MeasureSpec.getMode(widthMeasureSpec);

        final int height = MeasureSpec.getSize(heightMeasureSpec);
        MeasureSpec.getMode(heightMeasureSpec);

        screenWidth = width;
        screenHeight = height;
        int childWidth = width / colCount;
        int childHeight = height / rowCount;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childWidthSpec = getChildMeasureSpec(MeasureSpec
                    .makeMeasureSpec(childWidth, MeasureSpec.EXACTLY), 20,
                    childWidth);
            int childHeightSpec = getChildMeasureSpec(MeasureSpec
                    .makeMeasureSpec(childHeight, MeasureSpec.EXACTLY), 20,
                    childHeight);
            child.measure(childWidthSpec, childHeightSpec);
        }
        scrollTo(mCurScreen * width, 0);
    }

    // 根据坐标，判断当前item所属的位置，即编号
    public int pointToPosition(int x, int y) {
        int locX = x + mCurScreen * getWidth();

        if (itemRect == null) {
            itemRect = new Rect();
        }
        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);
            child.getHitRect(itemRect);
            if (itemRect.contains(locX, y)) {
                return i;
            }
        }
        return -1;
    }

    private PointF positionToPoint2(int position) {
        PointF point = new PointF();

        int row = position / colCount % rowCount;
        int col = position % colCount;
        int left = col * childWidth;
        int top = row * childHeight;

        point.x = left;
        point.y = top;
        return point;

    }

    public void setColCount(int colCount) {
        this.colCount = colCount;
        this.itemPerPage = this.colCount * this.rowCount;
    }

    public boolean isEditting() {
        return isEditting;
    }

    public void setOnAddPage(OnAddOrDeletePage onAddPage) {
        this.onAddPage = onAddPage;
    }

    public void setOnEditModeListener(OnEditModeListener onEditModeListener) {
        this.onEditModeListener = onEditModeListener;
    }

    public void setOnPageChangedListener(
            OnPageChangedListener pageChangedListener) {
        this.pageChangedListener = pageChangedListener;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
        this.itemPerPage = this.colCount * this.rowCount;
    }

    public void setSaAdapter(IconAdapter saAdapter) {
        this.mAdapter = saAdapter;
        this.mAdapter.setOnDataChangeListener(this);
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    // 执行松手动画
    private void showDropAnimation(int x, int y) {
        ViewGroup moveView = (ViewGroup) getChildAt(dragPosition);
        TranslateAnimation animation = new TranslateAnimation(
                x - halfBitmapWidth - moveView.getLeft(), 0,
                y - halfBitmapHeight - moveView.getTop(), 0);
        animation.setFillAfter(false);
        animation.setDuration(300);
        moveView.setAnimation(animation);
        windowManager.removeView(dragImageView);
        dragImageView = null;

        if (dragBitmap != null) {
            dragBitmap = null;
        }

        clearChildrenAnimation();
    }

    private void clearChildrenAnimation() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).clearAnimation();
        }
    }

    private void shakeAnimation() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            shakeAnimation(getChildAt(i));
        }
    }

    public void showEdit(boolean isEdit) {
        isEditting = isEdit;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            ImageView iv = (ImageView) child.findViewById(
                    EUExUtil.getResIdID("plugin_iconlist_del_app_btu"));
            IconBean iconBean = (IconBean) child.getTag();
            iv.setTag(iconBean);
            boolean canDel = isEdit && iconBean.getIsCanDel();
            iv.setVisibility(canDel ? View.VISIBLE : View.GONE);
            if (canDel) {
                iv.setOnClickListener(new DelItemClick());
            }
        }
    }

    // 滚屏
    private void snapToDestination() {
        final int screenWidth = getWidth();
        final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
        if (destScreen >= 0 && destScreen < totalPage) {
            snapToScreen(destScreen);
        }
    }

    private void snapToScreen(int whichScreen) {
        snapToScreen(whichScreen, true);
    }

    @Override
    public void invalidate() {
        if (IconListOption.isInvalidateChild()) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View childView = getChildAt(i);
                if (childView.getVisibility() != View.GONE) {
                    childView.invalidate();
                }
            }
        } else {
            super.invalidate();
        }
    }

    private void snapToScreen(int whichScreen, boolean isFast) {
        // get the valid layout page
        if (!circul) {
            whichScreen = Math.max(0,
                    Math.min(whichScreen, getChildCount() - 1));
        } else {
            whichScreen = Math.max((-1),
                    Math.min(whichScreen, getChildCount()));
        }

        if (getScrollX() != (whichScreen * getWidth())) {
            final int delta = whichScreen * getWidth() - getScrollX();
            if (pageChangedListener != null) {
                pageChangedListener.onPage2Other(mCurScreen, whichScreen);
            }

            if (!isFast) {
                mScroller.startScroll(getScrollX(), 0, delta, 0, 800);
            } else {
                mScroller.startScroll(getScrollX(), 0, delta, 0, 500);
            }
            mCurScreen = whichScreen;
            invalidate(); // Redraw the layout
            requestLayout();
        }
    }

    /**
     * 删除按钮的功能处理
     *
     */
    private View delView = null;

    private final class DelItemClick implements OnClickListener {

        public DelItemClick() {
        }

        @Override
        public void onClick(View v) {
            delView = v;
            IconBean iconBean = (IconBean) v.getTag();
            mEuExIconList.mEuExIconListHandler.send2Callback(WHAT_ON_DEL_CLICK,
                    iconBean.getJsonStr());
        }
    }

    public void delItem(int pos) {
        ViewGroup viewGroup = (ViewGroup) delView.getParent();
        if (viewGroup != null) {
            if (mCurScreen < totalPage - 1) {

            }
            if (isIconShake) {
                isDelItem = true;
                clearChildrenAnimation();
            }
            movePostionAnimation(pos, getChildCount() - 1);
            removeView(viewGroup);
            mAdapter.delete(pos);
            showEdit(true);
            if (isIconShake) {
                mHandler.sendEmptyMessageDelayed(WHAT_RESTART_SHAKE_DELAY,
                        START_RESHAKE_DELAY);
            }

            // 如果删除后少了一屏，则移动到前一屏，并进行页面刷新
            if (getChildCount() % itemPerPage == 0) {
                if (mCurScreen == totalPage) {
                    snapToScreen(totalPage - 1);
                }
                refreshPageindictor(totalPage, mCurScreen);
            }
        }
    }

    /**
     * 以下三种情况下icon不抖动： resetFrame、delItem、moveItem
     */
    private boolean isShaking() {
        return (!resetFrame && !isDelItem && (Mode != Mode_Drag));
    }

    private void shakeAnimation(final View v) {
        int c = mCount++ % 5;
        float rotate = DEGREE[c];
        final RotateAnimation mra = new RotateAnimation(rotate, -rotate,
                UIConfig.getIconScaleWidth() / 2,
                UIConfig.getIconScaleHight() / 2);
        final RotateAnimation mrb = new RotateAnimation(-rotate, rotate,
                UIConfig.getIconScaleWidth() / 2,
                UIConfig.getIconScaleHight() / 2);

        mra.setDuration(ANIMATION_DURATION);
        mrb.setDuration(ANIMATION_DURATION);

        mra.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (isEditting) {
                    if (isShaking()) {
                        mra.reset();
                        v.startAnimation(mrb);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });

        mrb.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                if (isEditting) {
                    if (isShaking()) {
                        mrb.reset();
                        v.startAnimation(mra);
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        v.startAnimation(mra);
    }

    public void setEuexIconList(EUExIconList mEuExIconList) {
        this.mEuExIconList = mEuExIconList;
    }

    public void setPageIndicator(PageIndicator mPageIndicator) {
        this.mPageIndicator = mPageIndicator;
    }

    public interface SAdapter {
        void exchange(int oldPosition, int newPositon);

        int getCount();

        View getView(int position);
    }

    public interface OnAddOrDeletePage {
        void onAddOrDeletePage(int page, boolean isAdd);
    }

    public interface OnEditModeListener {
        void onEdit();
    }

    public interface OnPageChangedListener {
        void onPage2Other(int former, int current);
    }

}
