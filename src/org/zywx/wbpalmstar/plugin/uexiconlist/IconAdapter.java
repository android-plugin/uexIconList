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
import org.zywx.wbpalmstar.base.cache.ImageLoadTask;
import org.zywx.wbpalmstar.base.cache.ImageLoadTask$ImageLoadTaskCallback;
import org.zywx.wbpalmstar.base.cache.ImageLoaderManager;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.DensityUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconBean;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.UIConfig;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.OnDataChangeListener;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.ScrollLayout;
import org.zywx.wbpalmstar.plugin.uexiconlist.view.ScrollLayout.SAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("UseSparseArrays")
public class IconAdapter implements SAdapter, ConstantUtils {
    private Context mContext;// 上下文
    private JSONObject widgetJson = null;
    private String defIconUrl = null;
    private ImageLoaderManager imgLoadMgr;
    private static Bitmap bgBitmap = null;
    private LayoutInflater mInflater;
    private LinkedList<IconBean> mIconList;
    private EUExIconList mEUExIconList;
    private ScrollLayout mContainer;

    public IconAdapter(Context context, LinkedList<IconBean> list,
            String defIconUrl, String widgetInfo, EUExIconList mEUExIconList,
            ScrollLayout mContainer) {
        mContext = context;
        this.mEUExIconList = mEUExIconList;
        this.defIconUrl = defIconUrl;
        mIconList = list;
        this.mContainer = mContainer;
        try {
            widgetJson = new JSONObject(widgetInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        imgLoadMgr = ImageLoaderManager.initImageLoaderManager(context);
        if (bgBitmap == null) {
            bgBitmap = IconListUtils.getImage(mContext, defIconUrl, widgetJson);
        }
        this.mInflater = LayoutInflater.from(context);
    }

    public void setItemShapeVisible(View convertView, boolean visible) {
        GradientDrawable itemShape = (GradientDrawable) convertView
                .getBackground();
        int width = (int) (visible ? UIConfig.getIconFrameWidth() * 2
                : UIConfig.ICON_FRAME_INVISIBLE_WIDTH_SCALE);
        itemShape.setStroke(width, UIConfig.getIconFrameColor());
    }

    @Override
    public View getView(int position) {
        View convertView = null;
        if (position < mIconList.size()) {
            final IconBean iconInfo = mIconList.get(position);
            convertView = mInflater.inflate(
                    EUExUtil.getResLayoutID("plugin_iconlist_item"), null);
            final IconViewHolder hoder = new IconViewHolder();
            hoder.ivIcon = (ImageView) convertView.findViewById(
                    EUExUtil.getResIdID("plugin_iconlist_ivIcon"));
            int iconBTPadding = (int) (UIConfig.getGridScaleHight()
                    * UIConfig.ICON_PADDING_HIGHT_SCALE);
            LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) hoder.ivIcon
                    .getLayoutParams();
            lParams.width = (int) UIConfig.getIconScaleWidth();
            lParams.height = (int) UIConfig.getIconScaleHight();
            lParams.bottomMargin = iconBTPadding;
            // lParams.topMargin = iconBTPadding;
            lParams.leftMargin = iconBTPadding;
            lParams.rightMargin = iconBTPadding;

            hoder.ivIcon.setLayoutParams(lParams);
            hoder.tvIconName = (TextView) convertView.findViewById(
                    EUExUtil.getResIdID("plugin_iconlist_tvTitle"));
            LinearLayout.LayoutParams llpParams = (LinearLayout.LayoutParams) hoder.tvIconName
                    .getLayoutParams();
            llpParams.height = (int) UIConfig.getTitleScaleHight();
            hoder.tvIconName.setLayoutParams(llpParams);

            int textSize = DensityUtil.px2sp(mContext,
                    UIConfig.getTitleScaleHight()
                            * UIConfig.TITLE_TEXT_HIGHT_SCALE);
            hoder.tvIconName.setTextSize(textSize);
            hoder.tvIconName.setTextColor(UIConfig.getTileTextColor());
            hoder.ivDelIcon = (ImageView) convertView.findViewById(
                    EUExUtil.getResIdID("plugin_iconlist_del_app_btu"));
            Bitmap bitmap = null;
            try {
                if (iconInfo.getIcon().startsWith("http://")) {
                    hoder.ivIcon.setImageBitmap(bgBitmap);
                    bitmap = imgLoadMgr.getCacheBitmap(iconInfo.getIcon());
                    if (null == bitmap) {
                        imgLoadMgr.asyncLoad(
                                new ImageLoadTask(iconInfo.getIcon()) {
                                    @Override
                                    protected Bitmap doInBackground() {
                                        Bitmap mBitmap = IconListUtils
                                                .downloadNetworkBitmap(
                                                        iconInfo.getIcon());
                                        return mBitmap;
                                    }
                                }.addCallback(
                                        new ImageLoadTask$ImageLoadTaskCallback() {
                                            @Override
                                            public void onImageLoaded(
                                                    ImageLoadTask task,
                                                    Bitmap bitmap) {
                                                if (bitmap != null) {
                                                    hoder.ivIcon.setImageBitmap(
                                                            bitmap);
                                                }
                                            }
                                        }));
                        // Thread.sleep(500);
                    } else {
                        hoder.ivIcon.setImageBitmap(bitmap);
                    }
                } else {
                    bitmap = IconListUtils.loadBitmapByUrl(mContext,
                            iconInfo.getIcon(), defIconUrl, widgetJson);
                    hoder.ivIcon.setImageBitmap(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            hoder.tvIconName.setText(iconInfo.getTitle());
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (!mContainer.isEditting()) {
                        if (!ScrollLayout.isMove) {
                            mEUExIconList.mEuExIconListHandler.send2Callback(
                                    WHAT_ON_ICON_CLICK, iconInfo.getJsonStr());
                        }
                    }
                }
            });
            /** 图片 名字布局 */
            View iconName = (View) convertView.findViewById(
                    EUExUtil.getResIdID("plugin_iconlist_icon_name"));

            RelativeLayout.LayoutParams iconNameParams = (RelativeLayout.LayoutParams) iconName
                    .getLayoutParams();
            iconNameParams.topMargin = (int) (UIConfig.getGridScaleHight()
                    * UIConfig.ICON_NAME_PADDING_HIGHT_SCALE);
            iconName.setLayoutParams(iconNameParams);
            convertView.setTag(iconInfo);
        }
        /** 隐藏icon的边框 */
        setItemShapeVisible(convertView, false);
        return convertView;
    }

    public static class IconViewHolder {
        public TextView tvIconName;
        public ImageView ivIcon;
        public ImageView ivDelIcon;
    }

    @Override
    public int getCount() {
        return mIconList.size();
    }

    @Override
    public void exchange(int oldPosition, int newPositon) {
        IconBean item = mIconList.get(oldPosition);
        mIconList.remove(oldPosition);
        mIconList.add(newPositon, item);
    }

    private OnDataChangeListener dataChangeListener = null;

    public OnDataChangeListener getOnDataChangeListener() {
        return dataChangeListener;
    }

    public void setOnDataChangeListener(
            OnDataChangeListener dataChangeListener) {
        this.dataChangeListener = dataChangeListener;
    }

    public void delete(int position) {
        if (position < getCount()) {
            mIconList.remove(position);
        }
    }

    public boolean add(IconBean item) {
        boolean isAddEnd = true;
        if (mIconList.get(mIconList.size() - 1).getIsCanMove()) {
            mIconList.add(item);
        } else {
            mIconList.add(mIconList.size() - 1, item);
            isAddEnd = false;
        }
        return isAddEnd;
    }

    public IconBean getIconBean(int position) {
        return mIconList.get(position);
    }
}
