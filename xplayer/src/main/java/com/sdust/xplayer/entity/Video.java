package com.sdust.xplayer.entity;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable{

	private static final long serialVersionUID = 1L;

	/** 视频名称 */
	public String name;
	/** 视频大小 */
	public Long size = 0l;
	/** 视频时长 */
	public Long duration = 0l;
	/** 视频地址 */
	public String url;
	/** 视频缩略图路径 */
	public String thumbPath;
	/** 视频分辨率-宽 */
	public int resolutionW;
	/** 视频分辨率-高 */
	public int resolutionH;

	public Video() {}
	protected Video(Parcel in) {
		name = in.readString();
		url = in.readString();
		thumbPath = in.readString();
		size = in.readLong();
		duration = in.readLong();
		resolutionW = in.readInt();
		resolutionH = in.readInt();
	}

	public static final Creator<Video> CREATOR = new Creator<Video>() {
		@Override
		public Video createFromParcel(Parcel in) {
			return new Video(in);
		}

		@Override
		public Video[] newArray(int size) {
			return new Video[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(url);
		dest.writeString(thumbPath);
		dest.writeLong(size);
		dest.writeLong(duration);
		dest.writeInt(resolutionW);
		dest.writeInt(resolutionH);
	}
}
