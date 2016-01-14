package org.zywx.wbpalmstar.plugin.uexiconlist.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class IconBean implements Parcelable {
	private String jsonStr;
	private String iconId;
	private String icon;
	private String title;
	private String isCanDel = ConstantUtils.TRUE;
	private String isCanMove = ConstantUtils.TRUE;

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
		return ConstantUtils.TRUE.equals(isCanDel);
	}

	public boolean getIsCanMove() {
		return ConstantUtils.TRUE.equals(isCanMove);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<IconBean> CREATOR = new Creator<IconBean>() {

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
		return "IconBean [jsonStr=" + jsonStr + ", icon=" + icon + ", title=" + title + "]";
	}

}
