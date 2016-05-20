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

import android.os.Parcel;
import android.os.Parcelable;

public class IconBean implements Parcelable, ConstantUtils {
    private String jsonStr;
    private String iconId;
    private String icon;
    private String title;
    private String isCanDel = TRUE;
    private String isCanMove = TRUE;

    public IconBean() {

    }

    public IconBean(Parcel source) {
        this.jsonStr = source.readString();
        this.iconId = source.readString();
        this.icon = source.readString();
        this.title = source.readString();
        this.isCanDel = source.readString();
        this.isCanMove = source.readString();
    }

    public void setJsonStr(String jsonStr) {
        this.jsonStr = jsonStr;
    }

    public void setIconId(String iconId) {
        this.iconId = iconId;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJsonStr() {
        return jsonStr;
    }

    public void setIsCanDel(String isCanDel) {
        this.isCanDel = isCanDel;
    }

    public void setIsCanMove(String isCanMove) {
        this.isCanMove = isCanMove;
    }

    public String getIconId() {
        return iconId;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsCanDel() {
        return TRUE.equals(isCanDel);
    }

    public boolean getIsCanMove() {
        return TRUE.equals(isCanMove);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public final static Parcelable.Creator<IconBean> CREATOR = new Creator<IconBean>() {

        @Override
        public IconBean[] newArray(int size) {
            return new IconBean[size];
        }

        @Override
        public IconBean createFromParcel(Parcel source) {
            return new IconBean(source);
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(jsonStr);
        dest.writeString(iconId);
        dest.writeString(icon);
        dest.writeString(title);
        dest.writeString(isCanDel);
        dest.writeString(isCanMove);
    }

    @Override
    public String toString() {
        return "IconBean [jsonStr=" + jsonStr + ", icon=" + icon + ", title="
                + title + "]";
    }

}
