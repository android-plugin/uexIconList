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

/**
 * @author wanglei
 *
 */
public class IconListOption implements ConstantUtils {
    /** 是否跟随网页滚动 */
    private static boolean isFollowWebRoll = true;

    /**
     * 由于翻页闪屏问题，只有在江苏烟草项目特定的网页布局下，view随网页滚动的时才会出现，所以插件只针对该项目做了修改。
     * ********************************************苏烟草项目使用此插件时，避免翻页闪屏，需传入此配置,如下：
     * var option = {"is_follow_web_roll": "true", "isInvalidateChild":"true"};
     * uexIconList.setOption(JSON.stringify(option);
     */
    private static boolean isInvalidateChild = false;

    public static boolean isFollowWebRoll() {
        return isFollowWebRoll;
    }

    public static void setFollowWebRoll(boolean isFollowWeb) {
        isFollowWebRoll = isFollowWeb;
    }

    public static boolean isInvalidateChild() {
        return isInvalidateChild;
    }

    public static void setInvalidateChild(boolean invalidateChild) {
        isInvalidateChild = invalidateChild;
    }

}
