package org.zywx.wbpalmstar.plugin.uexiconlist.utils;

import android.graphics.Color;

/**
 * @author wanglei
 *
 */
public class UIConfig {
	private static int x;
	private static int scaleX;
	private static int y;
	private static int scaleY;
	private static int width;
	private static int scaleWidth;
	private static int hight;
	private static int scaleHight;
	private static int iconScaleWidth;
	private static int iconScaleHight;
	private static int titleScaleHight;
	private static int pageIndicatorScaleHight;

	private static int APP_PAGE_LINE = 2;// 行数
	private static int APP_PAGE_ROW = 4;// 列数

	private static int BACKGROUND_COLOR = Color.parseColor(ConstantUtils.DEF_BACKGROUND_COLOR);
	private static int TITLE_TEXT_COLOR = Color.parseColor(ConstantUtils.DEF_TITLE_TEXT_COLOR);

	// public static float ICON_ITME_R_L_SPACE_SCALE = (float) 0.0434; //width
	// public static float MAIN_LAYOUT_BOTTOM_SPACE_SCALE = (float)
	// 0.016;//hight
	// public static float MAIN_LAYOUT_TOP_SPACE_SCALE = (float) 0.040;//hight
	// public static float GRIDVIEW_TOP_SPACE_SCALE = (float) 0.028;//hight
	// public static float TILE_TITLE_HIGHT_SCALE = (float) 0.105;//hight

	public static void setX(int xPos) {
		x = xPos;
	}

	public static int getX() {
		return x;
	}

	public static void setScaleX(int xPos) {
		scaleX = xPos;
	}

	public static int getScaleX() {
		return scaleX;
	}

	public static void setY(int yPos) {
		y = yPos;
	}

	public static int getY() {
		return y;
	}

	public static void setScaleY(int yPos) {
		scaleY = yPos;
	}

	public static int getScaleY() {
		return scaleY;
	}

	public static void setWidth(int w) {
		width = w;
	}

	public static int getWidth() {
		return width;
	}

	public static void setScaleWidth(int w) {
		scaleWidth = w;
	}

	public static int getScaleWidth() {
		return scaleWidth;
	}

	public static void setHight(int h) {
		hight = h;
	}

	public static int getHight() {
		return hight;
	}

	public static void setScaleHight(int h) {
		scaleHight = h;
	}

	public static int getScaleHight() {
		return scaleHight;
	}

	public static void setIconScaleWidth(int w) {
		iconScaleWidth = w;
	}

	public static int getIconScaleWidth() {
		return iconScaleWidth;
	}

	public static void setIconScaleHight(int h) {
		iconScaleHight = h;
	}

	public static int getIconScaleHight() {
		return iconScaleHight;
	}

	public static void setTitleScaleHight(int h) {
		titleScaleHight = h;
	}

	public static int getTitleScaleHight() {
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
		BACKGROUND_COLOR = color;
	}

	public static int getBackgroundColor() {
		return BACKGROUND_COLOR;
	}

	public static void setTileTextColor(int color) {
		TITLE_TEXT_COLOR = color;
	}

	public static int getTileTextColor() {
		return TITLE_TEXT_COLOR;
	}

	public static int getPageIndicatorScaleHight() {
		return pageIndicatorScaleHight;
	}

	public static void setPageIndicatorScaleHight(int pageIndicatorScaleHight) {
		UIConfig.pageIndicatorScaleHight = pageIndicatorScaleHight;
	}
}
