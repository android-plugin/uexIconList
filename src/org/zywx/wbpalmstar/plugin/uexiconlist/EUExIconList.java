package org.zywx.wbpalmstar.plugin.uexiconlist;

import java.util.LinkedList;

import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import org.zywx.wbpalmstar.engine.universalex.EUExCallback;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.ConstantUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconBean;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.IconListUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.LogUtils;
import org.zywx.wbpalmstar.plugin.uexiconlist.utils.UIConfig;
import org.zywx.wbpalmstar.widgetone.dataservice.WWidgetData;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/*入口类*/
@SuppressLint("ShowToast")
public class EUExIconList extends EUExBase {
	private static boolean DEBUG = true;

	/* 服务器端已定义 */
	static final String CB_OPEN = "uexIconList.cbOpen";
	static final String CB_CLOSE = "uexIconList.cbClose";
	static final String ON_ICON_CLICK = "uexIconList.cbClickItem";
	static final String ON_DEL_CLICK = "uexIconList.onDelClick";
	static final String CB_GET_CURRENT_ICON_LIST = "uexIconList.cbGetCurrentIconList";
	static final String ON_LONG_PRESS = "uexIconList.onLongPress";
	static final String CB_ADD_ICON_ITEM = "uexIconList.cbAddIconItem";
	static final String ON_TOUCH_DOWN = "uexIconList.onTouchDown";
	static final String ON_TOUCH_UP = "uexIconList.onTouchUp";
	static final String CB_DEL_ICON_ITEM_COMPLETED = "uexIconList.cbDelIconItemCompleted";

	/** 是否跟随网页滚动 */
	private boolean isFollowWebRoll = true;
	private boolean isIconListOpened = false;
	public EuExIconListHandler mEuExIconListHandler = new EuExIconListHandler();
	private WWidgetData mWWidgetData = null;
	private IconListActivity mIconListActivity = null;
	private JSONObject optionJson = new JSONObject();

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
		LogUtils.logDebug(true, LogUtils.getLineInfo() + "into setOption");
		if (params.length >= 1) {
			try {
				optionJson = new JSONObject(params[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			isFollowWebRoll = optionJson.optBoolean(ConstantUtils.JK_FOLLOW_WEB_ROLL, true)
					&& IconListUtils.isMethodExist(ConstantUtils.EUEXBASE_CALSS, ConstantUtils.FOLLOW_WEB_ROLL_METHOD);
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
				IconListUtils.parseUIConfig(params[ConstantUtils.OPEN_UI_CONFIG], getWebViewScale());
				String result = openIconList(params[ConstantUtils.OPEN_DATA_CONFIG]);
				if ("".equals(result)) {
					cbJson.put(ConstantUtils.JK_STATUE, ConstantUtils.ERROR_MSG_OK);
				} else {
					cbJson.put(ConstantUtils.JK_STATUE, ConstantUtils.ERROR_MSG_ERROR);
					cbJson.put(ConstantUtils.JK_INFO, result);
				}
			} else {
				cbJson.put(ConstantUtils.JK_STATUE, ConstantUtils.ERROR_MSG_ERROR);
				cbJson.put(ConstantUtils.JK_INFO, ConstantUtils.ERROR_MSG_PARM_ERROR);
				LogUtils.logError("open parm error");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_CB_OPEN, cbJson.toString());
	}

	/**
	 * 动态改变IconList view大小 可改变行列值
	 * 
	 */
	public void resetFrame(String[] params) {
		LogUtils.logDebug(true, "into resetFrame");
		if (params.length >= 1) {
			IconListUtils.parseUIConfig(params[0], getWebViewScale());
			resetFrame();
		}
	}

	private float getWebViewScale() {
		float nowScale = 1.0f;
		int versionA = Build.VERSION.SDK_INT;

		if (versionA <= 18) {
			nowScale = mBrwView.getScale();
		}
		return nowScale;
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
					ViewGroup subView = (ViewGroup) activity.getWindow().getDecorView();
					if (isFollowWebRoll) {
						AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(UIConfig.getScaleWidth(),
								UIConfig.getScaleHight(), UIConfig.getScaleX(), UIConfig.getScaleY());
						subView.setLayoutParams(lp);
					} else {
						FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(UIConfig.getScaleWidth(),
								UIConfig.getScaleHight());
						lp.leftMargin = UIConfig.getScaleX();
						lp.topMargin = UIConfig.getScaleY();
						subView.setLayoutParams(lp);
					}
					subView.invalidate();
					mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_RESET_FRAME, null);
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
					((Activity) mContext).getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
					try {
						JSONObject json = new JSONObject();
						json.put(ConstantUtils.JK_WIDGET_PATH, mWWidgetData.getWidgetPath());
						json.put(ConstantUtils.JK_WIDGET_TYPE, mWWidgetData.m_wgtType);
						Intent intent = new Intent(mContext, IconListActivity.class);
						intent.putExtra(ConstantUtils.WIDGET_INFO, json.toString());
						intent.putExtra(ConstantUtils.ITEM_INFO, jsonData);
						if (mgr == null) {
							mgr = new LocalActivityManager((Activity) mContext, false);
							mgr.dispatchCreate(null);
						}
						Window window = mgr.startActivity(IconListActivity.TAG, intent);
						View marketDecorView = window.getDecorView();
						if (isFollowWebRoll) {
							AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(UIConfig.getWidth(),
									UIConfig.getHight(), UIConfig.getX(), UIConfig.getY());
							ViewGroup viewGroup = (ViewGroup) marketDecorView.getParent();
							if (viewGroup != null) {
								viewGroup.removeView(marketDecorView);
							}
							/** 此方法引擎中对xywh已做处理，所以lp的xywh不需要乘scale */
							addViewToWebView(marketDecorView, lp, IconListActivity.TAG);
						} else {
							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(UIConfig.getWidth(),
									UIConfig.getHight());
							lp.leftMargin = UIConfig.getX();
							lp.topMargin = UIConfig.getY();
							/** 此方法引擎中对xywh已做处理，所以lp的xywh不需要乘scale */
							addViewToCurrentWindow(marketDecorView, lp);
						}
						isIconListOpened = true;
						mIconListActivity = (IconListActivity) mgr.getActivity(IconListActivity.TAG);
						mIconListActivity.initView(EUExIconList.this);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}// end run()
			});
		} // end if
		else {
			errorMsg = ConstantUtils.ERROR_MSG_ALREADY_OPEN;
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
				mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_ADD_ICON_ITEM, parm[0]);
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
				mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_DEL_ICON_ITEM, parm[0]);
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

		mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_CB_GET_CURRENT_ICON_LIST, jsonStr);

	}

	public void refreshIconList(String[] parm) {
		LogUtils.logDebug(true, "into refreshIconLis, len = " + parm.length);
		if (1 == parm.length) /** 传数据刷新 */
		{
			if (mIconListActivity != null) {
				mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_REFRESH_ICON_LIST, parm[0]);
			}
		} else if (0 == parm.length) /** 只刷新UI */
		{
			mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_REFRESH_ICON_LIST_UI, null);
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
						mgr = new LocalActivityManager((Activity) mContext, false);
						mgr.dispatchCreate(null);
					}
					View decorView = mIconListActivity.getWindow().getDecorView();
					decorView.setVisibility(View.GONE);
					if (isFollowWebRoll) {
						removeViewFromWebView(IconListActivity.TAG);
					} else {
						removeViewFromCurrentWindow(decorView);
					}
					mgr.destroyActivity(IconListActivity.TAG, true);
					mIconListActivity = null;
				}
			});
		}
		mEuExIconListHandler.send2Callback(ConstantUtils.WHAT_CB_CLOSE, null);
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
			case ConstantUtils.WHAT_CB_OPEN:
				jsCallback(CB_OPEN, 0, EUExCallback.F_C_JSON, msg.obj.toString());
				break;
			case ConstantUtils.WHAT_CB_CLOSE:
				jsCallback(CB_CLOSE, 0, EUExCallback.F_C_TEXT, null);
				break;
			case ConstantUtils.WHAT_ON_ICON_CLICK:
				jsCallback(ON_ICON_CLICK, 0, EUExCallback.F_C_JSON, msg.obj.toString());
				break;
			case ConstantUtils.WHAT_ON_DEL_CLICK:
				jsCallback(ON_DEL_CLICK, 0, EUExCallback.F_C_JSON, msg.obj.toString());
				break;
			case ConstantUtils.WHAT_CB_GET_CURRENT_ICON_LIST:
				jsCallback(CB_GET_CURRENT_ICON_LIST, 0, EUExCallback.F_C_JSON, msg.obj.toString());
				break;
			case ConstantUtils.WHAT_ON_LONG_PRESS:
				jsCallback(ON_LONG_PRESS, 0, EUExCallback.F_C_TEXT, null);
				break;
			case ConstantUtils.WHAT_CB_ADD_ICON_ITEM:
				jsCallback(CB_ADD_ICON_ITEM, 0, EUExCallback.F_C_JSON, msg.obj.toString());
				break;
			case ConstantUtils.WHAT_ON_TOUCH_DOWN:
				jsCallback(ON_TOUCH_DOWN, 0, EUExCallback.F_C_TEXT, null);
				break;
			case ConstantUtils.WHAT_ON_TOUCH_UP:
				jsCallback(ON_TOUCH_UP, 0, EUExCallback.F_C_TEXT, null);
				break;
			case ConstantUtils.WHAT_REFRESH_ICON_LIST:
				LinkedList<IconBean> iconList = IconListUtils.parseIconBeanList(msg.obj.toString());
				mIconListActivity.reloadIconList(iconList);
				break;
			case ConstantUtils.WHAT_REFRESH_ICON_LIST_UI:
				mIconListActivity.refreshIconListUI();
				break;
			case ConstantUtils.WHAT_RESET_FRAME:
				mIconListActivity.refreshIconList();
				break;
			case ConstantUtils.WHAT_DEL_ICON_ITEM:
				try {
					mIconListActivity.delIconItem(IconListUtils.parseIconBean(new JSONObject(msg.obj.toString())));
					jsCallback(CB_DEL_ICON_ITEM_COMPLETED, 0, EUExCallback.F_C_TEXT, null);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			case ConstantUtils.WHAT_ADD_ICON_ITEM:
				try {
					mIconListActivity.addIconItem(IconListUtils.parseIconBean(new JSONObject(msg.obj.toString())));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
