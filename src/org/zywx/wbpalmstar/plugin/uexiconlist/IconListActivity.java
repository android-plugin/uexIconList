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
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconBean;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListOption;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.UIConfig;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.PageIndicator;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.ScrollLayout;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.ScrollLayout.OnAddOrDeletePage;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.ScrollLayout.OnEditModeListener;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.ScrollLayout.OnPageChangedListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * 基于 https://github.com/autowanglei/AndroidLauncher 修改
 * 
 */
@SuppressLint("HandlerLeak")
public class IconListActivity extends Activity implements OnAddOrDeletePage,
        OnPageChangedListener, OnEditModeListener, ConstantUtils {

    public final static String TAG = "IconListActivity";
    // 滑动控件的容器Container
    private ScrollLayout mContainer;

    private PageIndicator pageIndicator;
    private LinkedList<IconBean> mIconList;
    private String widgetInfo = null;
    private String defIconUrl = null;
    private EUExIconList mEUExIconList;
    private View mainView = null;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = LayoutInflater.from(this).inflate(
                EUExUtil.getResLayoutID("plugin_iconlist_main_layout"), null);
        if (IconListOption.isInvalidateChild()) {
            mainView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        setContentView(mainView);
        Intent intent = getIntent();
        String itemInfo = intent.getStringExtra(ITEM_INFO);
        widgetInfo = intent.getStringExtra(WIDGET_INFO);
        mContainer = (ScrollLayout) findViewById(
                EUExUtil.getResIdID("icon_list_scrolllayout"));
        pageIndicator = (PageIndicator) findViewById(
                EUExUtil.getResIdID("icon_list_page_indictor"));
        defIconUrl = IconListUtils.getDefaultIconUrl(itemInfo);
        /** 刷新时，会重置mIconList */
        mIconList = IconListUtils.parseIconBeanList(itemInfo);
    }

    public void initView(EUExIconList mEUExIconList) {
        this.mEUExIconList = mEUExIconList;
        /** 大约计算gridItem的高度 */
        float gridViewHight = UIConfig.getScaleHight() / UIConfig.getLine();
        int viewGroupBottomPadding = (int) (UIConfig.getGridScaleHight()
                * UIConfig.VIEWGROUP_BOTTOM_PADDING_HIGHT_SCALE);
        UIConfig.setPageIndicatorScaleHight(
                (int) (gridViewHight * UIConfig.PAGE_INDICATOR_HIGHT_SCALE));
        /**
         * 精确计算gridItem的高度 gridItem的高度=（总高度-页码指示器高度 - viewGroupTopPadding -
         * topPadding）/ 行数
         */
        gridViewHight = (UIConfig.getScaleHight()
                - UIConfig.getPageIndicatorScaleHight()
                - viewGroupBottomPadding) / UIConfig.getLine();
        UIConfig.setGridScaleHight(gridViewHight);
        UIConfig.setTitleScaleHight(
                (int) (gridViewHight * UIConfig.TITLE_HIGHT_SCALE));
        /**
         * icon 高度 = gridItemHight * ICON_HIGHT_SCALE - iconTopPadding -
         * iconBottomPadding
         */
        UIConfig.setIconScaleHight(
                (int) (gridViewHight * UIConfig.ICON_HIGHT_SCALE
                        - UIConfig.getGridScaleHight()
                                * UIConfig.ICON_PADDING_HIGHT_SCALE));
        UIConfig.setIconScaleWidth(
                UIConfig.getScaleWidth() / UIConfig.getRow());
        if (UIConfig.isShowIconFrame()) {
            UIConfig.setIconFrameWidth(UIConfig.getIconScaleHight()
                    * UIConfig.ICON_FRAME_VISIBLE_WIDTH_SCALE);
        }
        mContainer.initFramePaint();
        // 动态设置Container每页的列数为2行
        mContainer.setColCount(UIConfig.getRow());
        // 动态设置Container每页的行数为4行
        mContainer.setRowCount(UIConfig.getLine());
        mContainer.setBackgroundColor(UIConfig.getBackgroundColor());
        pageIndicator.initViews(getApplicationContext());
        pageIndicator.setBackgroundColor(UIConfig.getBackgroundColor());
        mainView.setBackgroundColor(UIConfig.getBackgroundColor());

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) pageIndicator
                .getLayoutParams();
        rlp.height = (int) UIConfig.getPageIndicatorScaleHight();
        pageIndicator.setLayoutParams(rlp);
        // 初始化Container的Adapter
        IconAdapter mIconsAdapter = new IconAdapter(getApplicationContext(),
                mIconList, defIconUrl, widgetInfo, mEUExIconList, mContainer);
        // 设置Container添加删除Item的回调
        mContainer.setOnAddPage(this);
        // 设置Container页面换转的回调，比如自第一页滑动第二页
        mContainer.setOnPageChangedListener(this);
        // 设置Container编辑模式的回调，长按进入修改模式
        mContainer.setOnEditModeListener(this);
        // 设置Adapter
        mContainer.setSaAdapter(mIconsAdapter);
        // 调用refreView绘制所有的Item
        mContainer.refreView();
        mContainer.setEuexIconList(mEUExIconList);

        int pageSize = UIConfig.getLine() * UIConfig.getRow();
        int pageCount = (int) Math
                .ceil((float) mIconList.size() / (float) pageSize);
        pageIndicator.setTotalPageSize(pageCount);
        pageIndicator.setCurrentPage(0);
        mContainer.setPageIndicator(pageIndicator);
    }

    /**
     * 网页只刷新IconList UI，去掉删除按钮
     * 
     */
    public void refreshIconListUI() {
        if (mContainer != null) {
            mContainer.showEdit(false);
        }
    }

    private Handler resetFrameHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case WHAT_RESET_FRAME_COMPLETED:
                if (mContainer != null) {
                    mContainer.resetScrollLayout();
                    if (pageIndicator != null) {
                        pageIndicator.removeAllViews();
                    }
                    initView(mEUExIconList);
                    mContainer.resetFrameCompleted();
                }
                break;
            default:
                break;
            }
        }
    };

    public void refreshIconList() {
        if (mContainer != null) {
            mContainer.preResetFrame();
            resetFrameHandler.sendEmptyMessageDelayed(
                    WHAT_RESET_FRAME_COMPLETED, RESET_FRAME_DEALY);
        }
    }

    /**
     * 重新加载IconList 包括数据和UI
     * 
     * @param mIconList
     */
    public void reloadIconList(LinkedList<IconBean> mIconList) {
        this.mIconList = mIconList;
        refreshIconListUI();
        refreshIconList();
    }

    public String getCurrentIconList() {
        return IconListUtils.getJsonStrFromIconList(mIconList);
    }

    /**
     * 删除IconItem
     * 
     */
    public void delIconItem(IconBean icon) {
        int pos = IconListUtils.indexOfIconBeans(icon, mIconList);
        if ((pos >= 0) && (pos < mIconList.size())) {
            mContainer.delItem(pos);
        }
    }

    /**
     * add IconItem ps：若最后一个可移动，添加到最后，否则添加到倒数第二个
     * 
     */
    public void addIconItem(IconBean icon) {
        JSONObject json = new JSONObject();
        try {
            if (mContainer != null) {
                if (!IconListUtils.isIconExist(icon, mIconList)) {
                    mContainer.addItemView(icon);
                    json.put(JK_STATUE, ERROR_MSG_OK);
                } else {
                    json.put(JK_STATUE, ERROR_MSG_ERROR);
                    json.put(JK_INFO, ICON_IS_EXIST);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mEUExIconList.mEuExIconListHandler.send2Callback(WHAT_CB_ADD_ICON_ITEM,
                json.toString());
    }

    @Override
    public void onEdit() {
        Log.e("test", "onEdit");
    }

    @Override
    public void onPage2Other(int former, int current) {
        pageIndicator.setCurrentPage(current);
    }

    public void onAddOrDeletePage(int page, boolean isAdd) {
        if (isAdd && UIConfig.isShowIconFrame() && mContainer != null) {
            mContainer.saveFrame(page);
        }
    }

}
