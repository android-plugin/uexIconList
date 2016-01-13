package org.zywx.wbpalmstar.plugin.uexiconlist.utils;

public interface ConstantUtils {
	public final static String EUEXBASE_CALSS = "org.zywx.wbpalmstar.engine.universalex.EUExBase";
	public final static String FOLLOW_WEB_ROLL_METHOD = "addViewToWebView";

	public static final int OPEN_UI_CONFIG = 0;
	public static final int OPEN_DATA_CONFIG = 1;

	public final static String JK_FOLLOW_WEB_ROLL = "is_follow_web_roll";
	public final static String JK_UI_X = "x";
	public final static String JK_UI_Y = "y";
	public final static String JK_UI_W = "w";
	public final static String JK_UI_H = "h";
	public final static String JK_UI_LINE = "line";
	public final static String JK_UI_ROW = "row";
	public final static String JK_BACKGROUND_COLOR = "backgroundColor";
	public final static String JK_TITLE_TEXT_COLOR = "titleTextColor";
	public final static String JK_DEF_ICON_URL = "placeholderImg";
	public final static String JK_LIST_ITEM = "listItem";
	public final static String JK_ICON_URL = "image";
	public final static String JK_TITLE = "title";
	public final static String JK_ICON_ID = "iconId";

	/** Icon 唯一标示 */
	public final static String JK_ICON_CAN_DEL = "isCanDel";
	public final static String JK_ICON_CAN_MOVE = "isCanMove";
	public final static String JK_WIDGET_PATH = "widget_path";
	public final static String JK_WIDGET_TYPE = "widget_type";

	public final static String JK_STATUE = "status";
	public final static String JK_INFO = "info";
	public static final String ERROR_MSG_OK = "ok";
	public static final String ERROR_MSG_ERROR = "fail";
	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public static final String ERROR_MSG_PARM_ERROR = "parm_error";
	public static final String ERROR_MSG_ALREADY_OPEN = "already_open";
	public static final String ITEM_INFO = "item_info_intent";
	public static final String WIDGET_INFO = "widget_info_intent";
	public static final String ICON_IS_EXIST = "icon_is_exist";

	public final static int WHAT_CB_OPEN = 0;
	public final static int WHAT_CB_CLOSE = 1;
	public final static int WHAT_ON_ICON_CLICK = 2;
	public final static int WHAT_ON_DEL_CLICK = 3;
	public final static int WHAT_REFRESH_ICON_LIST = 4;
	public final static int WHAT_REFRESH_ICON_LIST_UI = 5;
	// public final static int WHAT_CB_REFRESH_ICON_LIST = 6;
	public final static int WHAT_DEL_ICON_ITEM = 7;
	public final static int WHAT_CB_GET_CURRENT_ICON_LIST = 8;
	public final static int WHAT_ON_LONG_PRESS = 9;
	public final static int WHAT_ADD_ICON_ITEM = 10;
	public final static int WHAT_CB_ADD_ICON_ITEM = 11;
	public final static int WHAT_ON_TOUCH_DOWN = 12;
	public final static int WHAT_ON_TOUCH_UP = 13;
	public final static int WHAT_RESET_FRAME = 14;

	public final static int NORMAL = 0;
	public final static int EDITED = 1;

	public final static String DEF_BACKGROUND_COLOR = "#EFEFEF";
	public final static String DEF_TITLE_TEXT_COLOR = "#9C9C9C";

	public static final int ANIMATION_DURATION = 80;

	public final static int WHAT_RESET_FRAME_COMPLETED = 0;
	public final static int RESET_FRAME_DEALY = 100;

	public final static int WHAT_RESTART_SHAKE_DELAY = 0;
	public final static int START_RESHAKE_DELAY = 1000;

	/** *图标高度相对于gridView高度的占比 */
	public static float ICON_HIGHT_SCALE = (float) 0.8;
	/** *图标名字高度相对于gridView高度的占比 */
	public static float TITLE_HIGHT_SCALE = (float) 0.2;
	/** *标题字体大小高度相对于标题高度的占比 */
	public static float TITLE_TEXT_HIGHT_SCALE = (float) 0.65;

	// /** *图标名字高度相对于总高度的占比 */
	// public static float TITLE_HIGHT_SCALE = (float) 0.1;
	/** *页码指示器高度相对于总高度的占比 */
	public static float PAGE_INDICATOR_HIGHT_SCALE = (float) 0.05;
	// /** *viewGroup top padding相对于总高度的占比 */
	// public static float VIEWGROUP_TOP_PADDING_HIGHT_SCALE = (float) 0.00;
	/** *viewGroup bottom padding相对于总高度的占比 */
	public static float VIEWGROUP_BOTTOM_PADDING_HIGHT_SCALE = (float) 0.01;
	/** *viewGroup right left padding相对于总高度的占比 */
	public static float VIEWGROUP_RL_PADDING_HIGHT_SCALE = (float) 0.03;
	/** *icon top bottom padding相对于总高度的占比 */
	public static float ICON_PADDING_HIGHT_SCALE = (float) 0.05;
	// public static float ICON_BOTTOM_PADDING_HIGHT_SCALE = (float) 0.03;

}
