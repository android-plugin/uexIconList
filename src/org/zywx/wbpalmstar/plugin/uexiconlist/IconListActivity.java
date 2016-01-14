/**
 * 基于 https://github.com/autowanglei/AndroidLauncher 修改
 * 
 * */

package org.zywx.wbpalmstar.plugin.uexiconlist;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconBean;
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

@SuppressLint("HandlerLeak")
public class IconListActivity extends Activity
		implements OnAddOrDeletePage, OnPageChangedListener, OnEditModeListener, ConstantUtils {

	// private static final boolean DEBUG = true;
	public static final String TAG = "IconListActivity";
	// 滑动控件的容器Container
	private ScrollLayout mContainer;

	// Container的Adapter
	private IconAdapter mIconsAdapter;
	// Container中滑动控件列表
	private PageIndicator pageIndicator;
	private LinkedList<IconBean> mIconList;
	private String widgetInfo = null;
	private String defIconUrl = null;
	private EUExIconList mEUExIconList;
	private View mainView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainView = LayoutInflater.from(this).inflate(EUExUtil.getResLayoutID("plugin_iconlist_main_layout"), null);
		setContentView(mainView);
		Intent intent = getIntent();
		String itemInfo = intent.getStringExtra(ITEM_INFO);
		widgetInfo = intent.getStringExtra(WIDGET_INFO);
		mContainer = (ScrollLayout) findViewById(EUExUtil.getResIdID("icon_list_scrolllayout"));
		pageIndicator = (PageIndicator) findViewById(EUExUtil.getResIdID("icon_list_page_indictor"));
		defIconUrl = IconListUtils.getDefaultIconUrl(itemInfo);
		mIconList = IconListUtils.parseIconBeanList(itemInfo);
	}

	public void initView(EUExIconList mEUExIconList) {
		this.mEUExIconList = mEUExIconList;
		int viewGroupRLPadding = (int) (UIConfig.getScaleHight() * VIEWGROUP_RL_PADDING_HIGHT_SCALE);
		int viewGroupBottomPadding = (int) (UIConfig.getScaleHight() * VIEWGROUP_BOTTOM_PADDING_HIGHT_SCALE);
		UIConfig.setPageIndicatorScaleHight((int) (UIConfig.getScaleHight() * PAGE_INDICATOR_HIGHT_SCALE));
		/** gridView的高度=（总高度-页码指示器高度 - viewGroupTopPadding - topPadding）/ 行数 */
		float gridViewHight = (UIConfig.getScaleHight() - UIConfig.getPageIndicatorScaleHight()
				- viewGroupBottomPadding) / UIConfig.getLine();
		UIConfig.setTitleScaleHight((int) (gridViewHight * TITLE_HIGHT_SCALE));
		/**
		 * icon 高度 = gridViewHight * ICON_HIGHT_SCALE - iconTopPadding -
		 * iconBottomPadding
		 */
		UIConfig.setIconScaleHight(
				(int) (gridViewHight * ICON_HIGHT_SCALE - UIConfig.getScaleHight() * ICON_PADDING_HIGHT_SCALE * 2));
		UIConfig.setIconScaleWidth(UIConfig.getScaleWidth() / UIConfig.getRow());
		mContainer.setBackgroundColor(UIConfig.getBackgroundColor());
		pageIndicator.setBackgroundColor(UIConfig.getBackgroundColor());
		mainView.setBackgroundColor(UIConfig.getBackgroundColor());

		RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) mContainer.getLayoutParams();
		rlParams.leftMargin = viewGroupRLPadding;
		rlParams.rightMargin = viewGroupRLPadding;
		rlParams.bottomMargin = viewGroupBottomPadding;
		mContainer.setLayoutParams(rlParams);
		RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) pageIndicator.getLayoutParams();
		rlp.height = UIConfig.getPageIndicatorScaleHight();
		pageIndicator.setLayoutParams(rlp);
		// 初始化Container的Adapter
		mIconsAdapter = new IconAdapter(this, mIconList, defIconUrl, widgetInfo, mEUExIconList, mContainer);
		// 设置Container添加删除Item的回调
		mContainer.setOnAddPage(this);
		// 设置Container页面换转的回调，比如自第一页滑动第二页
		mContainer.setOnPageChangedListener(this);
		// 设置Container编辑模式的回调，长按进入修改模式
		mContainer.setOnEditModeListener(this);
		// 设置Adapter
		mContainer.setSaAdapter(mIconsAdapter);
		// 动态设置Container每页的列数为2行
		mContainer.setColCount(UIConfig.getRow());
		// 动态设置Container每页的行数为4行
		mContainer.setRowCount(UIConfig.getLine());
		// 调用refreView绘制所有的Item
		mContainer.refreView();
		mContainer.setEuexIconList(mEUExIconList);

		int pageSize = UIConfig.getLine() * UIConfig.getRow();
		int pageCount = (int) Math.ceil((float) mIconList.size() / (float) pageSize);
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
					mContainer.clearAllChild();
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
			resetFrameHandler.sendEmptyMessageDelayed(WHAT_RESET_FRAME_COMPLETED, RESET_FRAME_DEALY);
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
		mEUExIconList.mEuExIconListHandler.send2Callback(WHAT_CB_ADD_ICON_ITEM, json.toString());
	}

	@Override
	public void onEdit() {
		Log.e("test", "onEdit");
	}

	@Override
	public void onPage2Other(int former, int current) {
		pageIndicator.setCurrentPage(current);
		Log.e("test", "former-->" + former + "  current-->" + current);
	}

	public void onAddOrDeletePage(int page, boolean isAdd) {
		Log.e("test", "page-->" + page + "  isAdd-->" + isAdd);
	}

}
