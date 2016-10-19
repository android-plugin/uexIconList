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

package org.zywx.wbpalmstar.plugin.uexiconlist;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconBean;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListOption;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.LogUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.UIConfig;
import org.zywx.wbpalmstar.widgetone.dataservice.WWidgetData;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.AbsoluteLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/*入口类*/
@SuppressWarnings("deprecation")
public class EUExIconList extends EUExBase implements ConstantUtils {

    /* 服务器端已定义 */
    final static String CB_OPEN = "uexIconList.cbOpen";
    final static String CB_CLOSE = "uexIconList.cbClose";
    final static String ON_ICON_CLICK = "uexIconList.cbClickItem";
    final static String ON_DEL_CLICK = "uexIconList.onDelClick";
    final static String CB_GET_CURRENT_ICON_LIST = "uexIconList.cbGetCurrentIconList";
    final static String ON_LONG_PRESS = "uexIconList.onLongPress";
    final static String CB_ADD_ICON_ITEM = "uexIconList.cbAddIconItem";
    final static String ON_TOUCH_DOWN = "uexIconList.onTouchDown";
    final static String ON_TOUCH_UP = "uexIconList.onTouchUp";
    final static String CB_DEL_ICON_ITEM_COMPLETED = "uexIconList.cbDelIconItemCompleted";

    private boolean isIconListOpened = false;
    public EuExIconListHandler mEuExIconListHandler = new EuExIconListHandler();
    private WWidgetData mWWidgetData = null;
    private IconListActivity mIconListActivity = null;
    public static LocalActivityManager mgr;

    /* 构造方法 */
    public EUExIconList(Context context, EBrowserView view) {
        super(context, view);
        mWWidgetData = view.getCurrentWidget();
    }

    /**
     * setOption 设置uexIconList选项，如：是否跟随网页滚动等。
     */
    public void setOption(String[] params) {
        LogUtils.logDebug(true, "into setOption");
        if (params.length >= 1) {
            IconListUtils.setIconListOption(params[0]);
        }
    }

    /**
     * 
     * 打开图标列表接口
     * 
     */
    public void open(String[] params) {
        LogUtils.logDebug(true, "into open");
        JSONObject cbJson = new JSONObject();
        try {
            if (params.length >= 2) {
                IconListUtils.setUIConfig(params[OPEN_UI_CONFIG],
                        IconListUtils.getWebScale(mBrwView));
                UIConfig.setScale();
                String result = openIconList(params[OPEN_DATA_CONFIG]);
                if ("".equals(result)) {
                    cbJson.put(JK_STATUE, ERROR_MSG_OK);
                } else {
                    cbJson.put(JK_STATUE, ERROR_MSG_ERROR);
                    cbJson.put(JK_INFO, result);
                }
            } else {
                cbJson.put(JK_STATUE, ERROR_MSG_ERROR);
                cbJson.put(JK_INFO, ERROR_MSG_PARM_ERROR);
                LogUtils.logError("open parm error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mEuExIconListHandler.send2Callback(WHAT_CB_OPEN, cbJson.toString());
    }

    /**
     * 动态改变IconList view大小 可改变行列值
     * 
     */
    public void resetFrame(String[] params) {
        LogUtils.logDebug(true, "into resetFrame");
        if (params.length >= 1) {
            IconListUtils.setUIConfig(params[0],
                    IconListUtils.getWebScale(mBrwView));
            resetFrame();
        }
    }

    /**
     * 动态改变IconList view大小 可改变行列
     * 
     */
    private void resetFrame() {
        ((Activity) mContext).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mgr == null) {
                    mgr = new LocalActivityManager((Activity) mContext, false);
                    mgr.dispatchCreate(null);
                }
                Activity activity = mgr.getActivity(IconListActivity.TAG);
                if (activity != null) {
                    ViewGroup subView = (ViewGroup) activity.getWindow()
                            .getDecorView();
                    if (IconListOption.isFollowWebRoll()) {
                        AbsoluteLayout.LayoutParams lParams = (LayoutParams) subView
                                .getLayoutParams();
                        lParams.width = (int) UIConfig.getScaleWidth();
                        lParams.height = (int) UIConfig.getScaleHight();
                        lParams.x = (int) UIConfig.getScaleX();
                        lParams.y = (int) UIConfig.getScaleY();
//                        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
//                                (int) UIConfig.getScaleWidth(),
//                                (int) UIConfig.getScaleHight(),
//                                (int) UIConfig.getScaleX(),
//                                (int) UIConfig.getScaleY());
                        subView.setLayoutParams(lParams);
                    } else {
                        FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) subView
                                .getLayoutParams();
                        lParams.width = (int) UIConfig.getScaleWidth();
                        lParams.height = (int) UIConfig.getScaleHight();
                        lParams.leftMargin = (int) UIConfig.getScaleX();
                        lParams.topMargin = (int) UIConfig.getScaleY();
                        // FrameLayout.LayoutParams lp = new
                        // FrameLayout.LayoutParams(
                        // (int) UIConfig.getScaleWidth(),
                        // (int) UIConfig.getScaleHight());
                        // lp.leftMargin = (int) UIConfig.getScaleX();
                        // lp.topMargin = (int) UIConfig.getScaleY();
                        subView.setLayoutParams(lParams);
                    }
                    subView.invalidate();
                    mEuExIconListHandler.send2Callback(WHAT_RESET_FRAME, null);
                }
            }
        });
    }

    /**
     * @param jsonData
     * @return
     */
    private String openIconList(final String jsonData) {
        String errorMsg = "";
        if (!isIconListOpened) {
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Rect outRect = new Rect();
                    ((Activity) mContext).getWindow().getDecorView()
                            .getWindowVisibleDisplayFrame(outRect);
                    try {
                        JSONObject json = new JSONObject();
                        json.put(JK_WIDGET_PATH, mWWidgetData.getWidgetPath());
                        json.put(JK_WIDGET_TYPE, mWWidgetData.m_wgtType);
                        Intent intent = new Intent(mContext,
                                IconListActivity.class);
                        intent.putExtra(WIDGET_INFO, json.toString());
                        intent.putExtra(ITEM_INFO, jsonData);
                        if (mgr == null) {
                            mgr = new LocalActivityManager((Activity) mContext,
                                    false);
                            mgr.dispatchCreate(null);
                        }
                        Window window = mgr.startActivity(IconListActivity.TAG,
                                intent);
                        View marketDecorView = window.getDecorView();
                        if (IconListOption.isFollowWebRoll()) {
                            AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(
                                    (int) UIConfig.getWidth(),
                                    (int) UIConfig.getHight(),
                                    (int) UIConfig.getX(),
                                    (int) UIConfig.getY());
                            ViewGroup viewGroup = (ViewGroup) marketDecorView
                                    .getParent();
                            if (viewGroup != null) {
                                viewGroup.removeView(marketDecorView);
                            }
                            addViewToWebView(marketDecorView, lp,
                                    IconListActivity.TAG);
                        } else {
                            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                                    (int) UIConfig.getWidth(),
                                    (int) UIConfig.getHight());
                            lp.leftMargin = (int) UIConfig.getX();
                            lp.topMargin = (int) UIConfig.getY();
                            addViewToCurrentWindow(marketDecorView, lp);
                        }
                        isIconListOpened = true;
                        mIconListActivity = (IconListActivity) mgr
                                .getActivity(IconListActivity.TAG);
                        mIconListActivity.initView(EUExIconList.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }// end run()
            });
        } // end if
        else {
            errorMsg = ERROR_MSG_ALREADY_OPEN;
        }
        return errorMsg;
    }

    /**
     * 添加IconItem接口
     * 
     * @ps：若最后一个可移动，添加到最后，否则添加到倒数第二个,页面滚动到图标添加的页
     * 
     */
    public void addIconItem(String[] parm) {
        LogUtils.logDebug(true, "into addIconItem");
        if (parm.length > 0) {
            if (mIconListActivity != null) {
                mEuExIconListHandler.send2Callback(WHAT_ADD_ICON_ITEM, parm[0]);
            }
        } else {
            LogUtils.logError("delIconItem parm error.");
        }
    }

    /**
     * 删除IconItem接口
     * 
     */
    public void delIconItem(String[] parm) {
        LogUtils.logDebug(true, "into delIconItem");
        if (parm.length > 0) {
            if (mIconListActivity != null) {
                mEuExIconListHandler.send2Callback(WHAT_DEL_ICON_ITEM, parm[0]);
            }
        } else {
            LogUtils.logError("delIconItem parm error.");
        }
    }

    public void getCurrentIconList(String[] parm) {
        LogUtils.logDebug(true, "into getCurrentIconList");

        String jsonStr = "";
        if (mIconListActivity != null) {
            jsonStr = mIconListActivity.getCurrentIconList();
        }

        mEuExIconListHandler.send2Callback(WHAT_CB_GET_CURRENT_ICON_LIST,
                jsonStr);

    }

    public void refreshIconList(String[] parm) {
        LogUtils.logDebug(true, "into refreshIconLis, len = " + parm.length);
        if (1 == parm.length) /** 传数据刷新 */
        {
            if (mIconListActivity != null) {
                mEuExIconListHandler.send2Callback(WHAT_REFRESH_ICON_LIST,
                        parm[0]);
            }
        } else if (0 == parm.length) /** 只刷新UI */
        {
            mEuExIconListHandler.send2Callback(WHAT_REFRESH_ICON_LIST_UI, null);
        } else {
            LogUtils.logError("refreshIconList parm error.");
        }
    }

    public void close(String[] parm) {
        LogUtils.logDebug(true, "into close");
        if (isIconListOpened) {
            isIconListOpened = false;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mgr == null) {
                        mgr = new LocalActivityManager((Activity) mContext,
                                false);
                        mgr.dispatchCreate(null);
                    }
                    View decorView = mIconListActivity.getWindow()
                            .getDecorView();
                    decorView.setVisibility(View.GONE);
                    if (IconListOption.isFollowWebRoll()) {
                        removeViewFromWebView(IconListActivity.TAG);
                    } else {
                        removeViewFromCurrentWindow(decorView);
                    }
                    mgr.destroyActivity(IconListActivity.TAG, true);
                    mIconListActivity = null;
                }
            });
        }
        mEuExIconListHandler.send2Callback(WHAT_CB_CLOSE, null);
    }

    // clean something
    @Override
    protected boolean clean() {
        return true;
    }

    public class EuExIconListHandler extends Handler {

        public void send2Callback(int what, String result) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = result;
            this.sendMessage(msg);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WHAT_CB_OPEN:
                jsCallback(CB_OPEN, 0, EUExCallback.F_C_JSON,
                        msg.obj.toString());
                break;
            case WHAT_CB_CLOSE:
                jsCallback(CB_CLOSE, 0, EUExCallback.F_C_TEXT, null);
                break;
            case WHAT_ON_ICON_CLICK:
                jsCallback(ON_ICON_CLICK, 0, EUExCallback.F_C_JSON,
                        msg.obj.toString());
                break;
            case WHAT_ON_DEL_CLICK:
                jsCallback(ON_DEL_CLICK, 0, EUExCallback.F_C_JSON,
                        msg.obj.toString());
                break;
            case WHAT_CB_GET_CURRENT_ICON_LIST:
                jsCallback(CB_GET_CURRENT_ICON_LIST, 0, EUExCallback.F_C_JSON,
                        msg.obj.toString());
                break;
            case WHAT_ON_LONG_PRESS:
                jsCallback(ON_LONG_PRESS, 0, EUExCallback.F_C_TEXT, null);
                break;
            case WHAT_CB_ADD_ICON_ITEM:
                jsCallback(CB_ADD_ICON_ITEM, 0, EUExCallback.F_C_JSON,
                        msg.obj.toString());
                break;
            case WHAT_ON_TOUCH_DOWN:
                jsCallback(ON_TOUCH_DOWN, 0, EUExCallback.F_C_TEXT, null);
                break;
            case WHAT_ON_TOUCH_UP:
                jsCallback(ON_TOUCH_UP, 0, EUExCallback.F_C_TEXT, null);
                break;
            case WHAT_REFRESH_ICON_LIST:
                LinkedList<IconBean> iconList = IconListUtils
                        .parseIconBeanList(msg.obj.toString());
                if (mIconListActivity != null) {
                    mIconListActivity.reloadIconList(iconList);
                }
                break;
            case WHAT_REFRESH_ICON_LIST_UI:
                if (mIconListActivity != null) {
                    mIconListActivity.refreshIconListUI();
                }
                break;
            case WHAT_RESET_FRAME:
                if (mIconListActivity != null) {
                    mIconListActivity.refreshIconList();
                }
                break;
            case WHAT_DEL_ICON_ITEM:
                try {
                    if (mIconListActivity != null) {
                        mIconListActivity
                                .delIconItem(IconListUtils.parseIconBean(
                                        new JSONObject(msg.obj.toString())));
                        jsCallback(CB_DEL_ICON_ITEM_COMPLETED, 0,
                                EUExCallback.F_C_TEXT, null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case WHAT_ADD_ICON_ITEM:
                try {
                    if (mIconListActivity != null) {
                        mIconListActivity
                                .addIconItem(IconListUtils.parseIconBean(
                                        new JSONObject(msg.obj.toString())));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
