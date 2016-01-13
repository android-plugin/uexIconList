package org.zywx.wbpalmstar.plugin.uexiconlist.view;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndicator extends LinearLayout {

	private LinearLayout.LayoutParams lp;
	private static final int SIZE = 8;
	private static final int MARGIN = 4;
	private boolean isRoll = true;

	public PageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		initViews(context);
	}

	public PageIndicator(Context context) {
		super(context);
		initViews(context);
	}

	public void initViews(Context context) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int size = (int) (dm.density * SIZE);
		int margin = (int) (dm.density * MARGIN);
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
				if (i == index) {
					view.setBackgroundResource(EUExUtil.getResDrawableID("plugin_iconlist_page_focus"));
				} else {
					view.setBackgroundResource(EUExUtil.getResDrawableID("plugin_iconlist_page_normal"));
				}
			}
		}
	}

	public void setRoll(boolean roll) {
		isRoll = roll;
	}

}
