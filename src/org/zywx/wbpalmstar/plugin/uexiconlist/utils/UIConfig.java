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

package org.zywx.wbpalmstar.plugin.uexiconlist.utils;

import android.graphics.Color;

/**
 * @author wanglei
 *
 */
public class UIConfig implements ConstantUtils {
    private static float x;
    private static float scaleX;
    private static float y;
    private static float scaleY;
    private static float width;
    private static float scaleWidth;
    private static float hight;
    private static float scaleHight;
    private static float iconScaleWidth;
    private static float iconScaleHight;
    private static float titleScaleHight;
    /** iconScaleHight + titleScaleHight */
    private static float gridScaleHight;
    private static float pageIndicatorScaleHight;
    private static float iconFrameWidth;
    private static boolean isShowIconFrame = false;

    private static int APP_PAGE_LINE = 2;// 行数
    private static int APP_PAGE_ROW = 4;// 列数

    private static int backgroundColor = Color.parseColor(DEF_BACKGROUND_COLOR);
    private static int titleTextColor = Color.parseColor(DEF_TITLE_TEXT_COLOR);
    private static int iconFrameColor = Color.parseColor(DEF_ICON_FRAME_COLOR);

    /** *图标高度相对于gridItem高度的占比 */
    public static float ICON_HIGHT_SCALE = (float) 0.7;
    public final static float ICON_HIGHT_NO_FRAME_SCALE = (float) 0.7;
    public final static float ICON_HIGHT_FRAME_SCALE = (float) 0.45;
    /** *icon_name top padding相对于gridItem高度的占比 */
    public static float ICON_NAME_PADDING_HIGHT_SCALE = (float) 0.08;
    public final static float ICON_NAME_PADDING_HIGHT_NO_FRAME_SCALE = (float) 0.08;
    public final static float ICON_NAME_PADDING_HIGHT_FRAME_SCALE = (float) 0.18;
    /** *图标名字高度相对于gridItem高度的占比 */
    public final static float TITLE_HIGHT_SCALE = (float) 0.2;
    /** *标题字体大小高度相对于标题高度的占比 */
    public final static float TITLE_TEXT_HIGHT_SCALE = (float) 0.65;

    /** *页码指示器高度相对于gridItem高度的占比 */
    public final static float PAGE_INDICATOR_HIGHT_SCALE = (float) 0.1;
    // /** *viewGroup top padding相对于总高度的占比 */
    // public static float VIEWGROUP_TOP_PADDING_HIGHT_SCALE = (float) 0.00;
    /** *viewGroup bottom padding相对于gridItem高度的占比 */
    public final static float VIEWGROUP_BOTTOM_PADDING_HIGHT_SCALE = (float) 0.02;
    /** *viewGroup right left padding相对于gridItem高度的占比 */
    public final static float VIEWGROUP_RL_PADDING_HIGHT_SCALE = (float) 0;
    /** *icon bottom padding相对于gridItem高度的占比 */
    public final static float ICON_PADDING_HIGHT_SCALE = (float) 0.1;
    /** *icon frame WIDTH相对于Icon的占比 */
    public final static float ICON_FRAME_VISIBLE_WIDTH_SCALE = (float) 0.02;
    public final static float ICON_FRAME_INVISIBLE_WIDTH_SCALE = 0;

    public static void setX(float xPos) {
        x = xPos;
    }

    public static float getX() {
        return x;
    }

    public static void setScaleX(float xPos) {
        scaleX = xPos;
    }

    public static float getScaleX() {
        return scaleX;
    }

    public static void setY(float yPos) {
        y = yPos;
    }

    public static float getY() {
        return y;
    }

    public static void setScaleY(float yPos) {
        scaleY = yPos;
    }

    public static float getScaleY() {
        return scaleY;
    }

    public static void setWidth(float w) {
        width = w;
    }

    public static float getWidth() {
        return width;
    }

    public static void setScaleWidth(float w) {
        scaleWidth = w;
    }

    public static float getScaleWidth() {
        return scaleWidth;
    }

    public static void setHight(float h) {
        hight = h;
    }

    public static float getHight() {
        return hight;
    }

    public static void setScaleHight(float h) {
        scaleHight = h;
    }

    public static float getScaleHight() {
        return scaleHight;
    }

    public static void setIconScaleWidth(float w) {
        iconScaleWidth = w;
    }

    public static float getIconScaleWidth() {
        return iconScaleWidth;
    }

    public static void setIconScaleHight(float h) {
        iconScaleHight = h;
    }

    public static float getIconScaleHight() {
        return iconScaleHight;
    }

    public static void setGridScaleHight(float h) {
        gridScaleHight = h;
    }

    public static float getGridScaleHight() {
        return gridScaleHight;
    }

    public static void setTitleScaleHight(float h) {
        titleScaleHight = h;
    }

    public static float getTitleScaleHight() {
        return titleScaleHight;
    }

    public static void setLine(int line) {
        APP_PAGE_LINE = line;
    }

    public static int getLine() {
        return APP_PAGE_LINE;
    }

    public static void setRow(int row) {
        APP_PAGE_ROW = row;
    }

    public static int getRow() {
        return APP_PAGE_ROW;
    }

    public static void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public static int getBackgroundColor() {
        return backgroundColor;
    }

    public static void setTileTextColor(int color) {
        titleTextColor = color;
    }

    public static int getTileTextColor() {
        return titleTextColor;
    }

    public static void setIsShowIconFrame(boolean showIconFrame) {
        isShowIconFrame = showIconFrame;
    }

    public static boolean isShowIconFrame() {
        return isShowIconFrame;
    }

    public static void setIconFrameColor(int color) {
        iconFrameColor = color;
    }

    public static int getIconFrameColor() {
        return iconFrameColor;
    }

    public static float getIconFrameWidth() {
        return iconFrameWidth;
    }

    public static void setIconFrameWidth(float iconFrameWidth) {
        UIConfig.iconFrameWidth = iconFrameWidth;
    }

    public static float getPageIndicatorScaleHight() {
        return pageIndicatorScaleHight;
    }

    public static void setPageIndicatorScaleHight(
            float pageIndicatorScaleHight) {
        UIConfig.pageIndicatorScaleHight = pageIndicatorScaleHight;
    }

    public static void setScale() {
        if (!isShowIconFrame) {
            ICON_HIGHT_SCALE = ICON_HIGHT_NO_FRAME_SCALE;
            ICON_NAME_PADDING_HIGHT_SCALE = ICON_NAME_PADDING_HIGHT_NO_FRAME_SCALE;
        } else {
            ICON_HIGHT_SCALE = ICON_HIGHT_FRAME_SCALE;
            ICON_NAME_PADDING_HIGHT_SCALE = ICON_NAME_PADDING_HIGHT_FRAME_SCALE;
        }
    }
}
