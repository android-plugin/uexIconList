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

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.UIConfig;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndicator extends LinearLayout {

    private LinearLayout.LayoutParams lp;
    private boolean isRoll = true;

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PageIndicator(Context context) {
        super(context);
    }

    public void initViews(Context context) {
        int size = (int) (UIConfig.getPageIndicatorScaleHight() * 0.6);
        int margin = (int) (UIConfig.getPageIndicatorScaleHight() * 0.3);
        lp = new LinearLayout.LayoutParams(size, size);
        lp.setMargins(margin, margin, margin, margin);
    }

    public void setTotalPageSize(int size) {
        if (size == getChildCount()) {
            return;
        }
        if (size > getChildCount()) {// 需要添加
            while (getChildCount() < size) {
                ImageView imageView = new ImageView(getContext());
                addView(imageView, getChildCount() - 1, lp);
            }
        } else {
            while (getChildCount() > size) {
                removeViewAt(getChildCount() - 1);
            }
        }
    }

    public void addPage() {
        ImageView imageView = new ImageView(getContext());
        addView(imageView, getChildCount() - 1, lp);
    }

    public void removePage() {
        removeViewAt(getChildCount() - 1);
    }

    public int getPageCount() {
        return getChildCount();
    }

    public void setCurrentPage(int index) {
        if (isRoll) {
            for (int i = 0, size = getChildCount(); i < size; i++) {
                View view = getChildAt(i);
                // if (i == index) {
                // view.setBackgroundResource(EUExUtil.getResDrawableID("plugin_iconlist_page_focus"));
                // } else {
                // view.setBackgroundResource(EUExUtil.getResDrawableID("plugin_iconlist_page_normal"));
                // }
                if (i == index) {
                    view.setBackgroundResource(EUExUtil.getResDrawableID(
                            "plugin_iconlist_page_indicator_focus"));
                } else {
                    view.setBackgroundResource(EUExUtil.getResDrawableID(
                            "plugin_iconlist_page_indicator_normal"));
                }
            }
        }
    }

    public void setRoll(boolean roll) {
        isRoll = roll;
    }

}
