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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.util.LinkedList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.zywx.wbpalmstar.base.BUtility;
import org.zywx.wbpalmstar.engine.EBrowserView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;

public class IconListUtils implements ConstantUtils {

    public static void setIconListOption(String jsonStr) {
        try {
            JSONObject optionJson = new JSONObject(jsonStr);
            boolean followWebRoll = true;
            if(optionJson.has(JK_FOLLOW_WEB_ROLL))
            {
                followWebRoll = optionJson.optBoolean(JK_FOLLOW_WEB_ROLL, true);
            }
            followWebRoll = followWebRoll && IconListUtils
                    .isMethodExist(EUEXBASE_CALSS, FOLLOW_WEB_ROLL_METHOD);
            IconListOption.setFollowWebRoll(followWebRoll);
            if (followWebRoll && optionJson.has(JK_INVALIDATE_CHILD)) {
                IconListOption.setInvalidateChild(
                        optionJson.optBoolean(JK_INVALIDATE_CHILD, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setUIConfig(String jsonStr, float scale) {
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject json = new JSONObject(jsonStr);

                int x = (int) (Float.parseFloat(json.optString(JK_UI_X)));
                UIConfig.setX(x);
                UIConfig.setScaleX((int) (x * scale));

                int y = (int) (Float.parseFloat(json.optString(JK_UI_Y)));
                UIConfig.setY(y);
                UIConfig.setScaleY((int) (y * scale));

                int width = (int) (Float.parseFloat(json.optString(JK_UI_W)));
                UIConfig.setWidth(width);
                UIConfig.setScaleWidth((int) (width * scale));

                int hight = (int) (Float.parseFloat(json.optString(JK_UI_H)));
                UIConfig.setHight(hight);
                UIConfig.setScaleHight((int) (hight * scale));

                UIConfig.setLine(
                        ((int) Float.parseFloat(json.optString(JK_UI_LINE))));
                UIConfig.setRow(
                        ((int) Float.parseFloat(json.optString(JK_UI_ROW))));

                UIConfig.setBackgroundColor(Color.parseColor(json
                        .optString(JK_BACKGROUND_COLOR, DEF_BACKGROUND_COLOR)));
                UIConfig.setTileTextColor(Color.parseColor(json
                        .optString(JK_TITLE_TEXT_COLOR, DEF_TITLE_TEXT_COLOR)));
                boolean isShowIconFrame = json.optBoolean(JK_IS_SHOW_ICON_FRAME,
                        false);
                UIConfig.setIsShowIconFrame(isShowIconFrame);
                if (isShowIconFrame) {
                    UIConfig.setIconFrameColor(Color.parseColor(json.optString(
                            JK_ICON_FRAME_COLOR, DEF_ICON_FRAME_COLOR)));
                }
                LogUtils.logDebug(true, "UIconfig:" + jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static Bitmap loadBitmapByUrl(Context mContext, String url,
            String defIconUrl, JSONObject json) {
        Bitmap bitmap = getImage(mContext, url, json);
        if (null == bitmap) {
            bitmap = getImage(mContext, defIconUrl, json);
        }
        return bitmap;
    }

    public static String getDefaultIconUrl(String jsonStr) {
        String defUrl = "";
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                defUrl = json.optString(JK_DEF_ICON_URL);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return defUrl;
    }

    public static LinkedList<IconBean> parseIconBeanList(String jsonArryStr) {
        LinkedList<IconBean> iconList = null;
        if (!TextUtils.isEmpty(jsonArryStr)) {
            iconList = new LinkedList<IconBean>();
            try {
                JSONObject json = new JSONObject(jsonArryStr);
                JSONArray jsonArray = json.getJSONArray(JK_LIST_ITEM);
                if ((jsonArray != null) && (jsonArray.length() > 0)) {
                    for (int i = 0, size = jsonArray.length(); i < size; i++) {
                        JSONObject jsonItem = jsonArray.getJSONObject(i);
                        iconList.add(parseIconBean(jsonItem));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return iconList;
    }

    public static IconBean parseIconBean(JSONObject json) {
        IconBean iconBean = new IconBean();
        iconBean.setJsonStr(json.toString());
        iconBean.setIconId(json.optString(JK_ICON_ID));
        iconBean.setIcon(json.optString(JK_ICON_URL));
        iconBean.setTitle(json.optString(JK_TITLE));
        iconBean.setIsCanDel(json.optString(JK_ICON_CAN_DEL, TRUE));
        iconBean.setIsCanMove(json.optString(JK_ICON_CAN_MOVE, TRUE));
        return iconBean;
    }

    public static int indexOfIconBeans(IconBean iconBean,
            LinkedList<IconBean> list) {
        int index = -1;
        for (int i = 0; i < list.size(); i++) {
            if (iconBean.getIconId().equals(list.get(i).getIconId())) {
                index = i;
                break;
            }
        }
        return index;
    }

    public static boolean isIconExist(IconBean iconBean,
            LinkedList<IconBean> list) {
        boolean isExist = false;
        for (int i = 0; i < list.size(); i++) {
            if (iconBean.getIconId().equals(list.get(i).getIconId())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    public static JSONObject getJsonFromIcon(IconBean iconBean) {
        JSONObject json = null;
        try {
            json = new JSONObject(iconBean.getJsonStr());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getJsonStrFromIconList(LinkedList<IconBean> list) {
        JSONArray jsonArry = new JSONArray();
        JSONObject json = new JSONObject();
        try {
            for (int i = 0; i < list.size(); i++) {
                jsonArry.put(i, getJsonFromIcon(list.get(i)));
            }
            json.put(JK_LIST_ITEM, jsonArry);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    public static LinkedList<IconBean> getPerPageIconList(
            LinkedList<IconBean> iconList, int page) {
        LinkedList<IconBean> gridViewIconList = new LinkedList<IconBean>();
        int pageSize = UIConfig.getLine() * UIConfig.getRow();
        int startPos = page * pageSize;// 当前页的起始位置
        int iEnd = startPos + pageSize;// 当前页的结束位置
        while ((startPos < iconList.size()) && (startPos < iEnd)) {
            gridViewIconList.add(iconList.get(startPos));
            startPos++;
        }
        return gridViewIconList;
    }

    public static Bitmap getImage(Context ctx, String imgUrl,
            JSONObject widgetJson) {
        if (imgUrl == null || imgUrl.length() == 0) {
            return null;
        }
        Bitmap bitmap = null;
        InputStream is = null;
        imgUrl = BUtility.makeRealPath(imgUrl,
                widgetJson.optString(JK_WIDGET_PATH),
                widgetJson.optInt(JK_WIDGET_TYPE));
        try {
            if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
                is = BUtility.getInputStreamByResPath(ctx, imgUrl);
                bitmap = BitmapFactory.decodeStream(is);
            } else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
                imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
                try {
                    is = ctx.getAssets().open(imgUrl);
                    if (is != null) {
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (imgUrl.startsWith("/")) {
                bitmap = BitmapFactory.decodeFile(imgUrl);
            } else if (imgUrl.startsWith("http://")) {
                bitmap = downloadNetworkBitmap(imgUrl);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    public static Bitmap downloadNetworkBitmap(String url) {
        byte[] data = downloadImageFromNetwork(url);
        if (data == null || data.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private static byte[] downloadImageFromNetwork(String url) {
        InputStream is = null;
        byte[] data = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            BasicHttpParams httpParams = new BasicHttpParams();
            HttpResponse httpResponse = new DefaultHttpClient(httpParams)
                    .execute(httpGet);
            int responseCode = httpResponse.getStatusLine().getStatusCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = httpResponse.getEntity().getContent();
                data = transStreamToBytes(is, 4096);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    private static byte[] transStreamToBytes(InputStream is, int buffSize) {
        if (is == null) {
            return null;
        }
        if (buffSize <= 0) {
            throw new IllegalArgumentException(
                    "buffSize can not less than zero.....");
        }
        byte[] data = null;
        byte[] buffer = new byte[buffSize];
        int actualSize = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((actualSize = is.read(buffer)) != -1) {
                baos.write(buffer, 0, actualSize);
            }
            data = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    public static boolean isMethodExist(String className, String methodName) {
        boolean isExist = false;
        try {
            Class<?> mClass = Class.forName(className);
            Method[] methodList = mClass.getMethods();
            for (Method method : methodList) {
                if (methodName.equals(method.getName())) {
                    isExist = true;
                    break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return isExist;
    }

    /**
     * 获取反射调用的方法
     * 
     * @param className
     *            类名
     * @param methodName
     *            方法名
     * @param parmTypeList
     *            参数类型列表
     * @return
     */
    public static Method getReflectionMethod(String className,
            String methodName,
            Class<?>[] parmTypeList) {
        Method mMethod = null;
        try {
            Class<?> mClass = Class.forName(className);
            try {
                /** 根据methodName, parmList获取所有方法包括public和非public */
                mMethod = mClass.getDeclaredMethod(methodName, parmTypeList);
                mMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return mMethod;
    }

    /**
     * getScaleWrap：4.0引擎中添加的获取x5内核网页scale的方法，为兼容旧引擎，故使用反射调用
     * 
     * @param mBrwView
     * @return
     */
    public static float getWebScale(EBrowserView mBrwView) {
        float scale = 1.0f;
        try {
            Method gatScale = EBrowserView.class.getMethod("getScaleWrap",
                    (Class<?>[]) null);
            try {
                scale = (Float) gatScale.invoke(mBrwView, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            scale = getWebScaleEngine3(mBrwView);
        }

        return scale;
    }

    /**
     * getCustomScale：3.0引擎中添加的获取x5内核网页scale的方法，为兼容旧引擎，故使用反射调用
     *
     * @param mBrwView
     * @return
     */
    public static float getWebScaleEngine3(EBrowserView mBrwView) {
        float scale = 1.0f;
        try {
            Method gatScale = EBrowserView.class.getMethod("getCustomScale",
                    (Class<?>[]) null);
            try {
                scale = (Float) gatScale.invoke(mBrwView, (Object[]) null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            scale = getWebScaleOld(mBrwView);
        }

        return scale;
    }

    private static float getWebScaleOld(EBrowserView mBrwView) {
        float nowScale = 1.0f;
        int versionA = Build.VERSION.SDK_INT;
        if (versionA <= 18) {
            nowScale = mBrwView.getScale();
        }
        return nowScale;
    }

}
