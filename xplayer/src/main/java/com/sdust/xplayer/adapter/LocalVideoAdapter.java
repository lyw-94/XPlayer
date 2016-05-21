package com.sdust.xplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.utils.BitmapUtils;
import com.sdust.xplayer.utils.StringUtils;

import java.util.List;


public class LocalVideoAdapter extends CommonAdapter<Video>{

	public LocalVideoAdapter(Context context, List<Video> mDatas,
			int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(final ViewHolder helper, Video item, int position) {

		// TODO: 这里加载缩略图的策略要换，
		// 像下面这样的话每次都会加载，没有缓存而且生成时间较长，会卡顿
		// 获得视频缩略图
		Bitmap bitmap = BitmapUtils.getVideoThumbnail(item.url);
		// 设置视频的分辨率属性
		item.resolutionW = bitmap.getWidth();
		item.resolutionH = bitmap.getHeight();
//		// 压缩到指定大小
		bitmap = BitmapUtils.scaleBitmap(bitmap, ApplicationConfig.VIDEO_THUBM_WIDTH,
				ApplicationConfig.VIDEO_THUBM_HEIGHT);
		helper.setImageBitmap(R.id.img_video_icon, bitmap);
//		img = helper.getView(R.id.img_video_icon);
//		new LoadThumbnailTask().execute(item);
		helper.setText(R.id.txt_video_title, item.name);
		helper.setText(R.id.txt_video_duration, StringUtils.generateTime(item.duration));
		helper.setText(R.id.txt_video_size,
				StringUtils.generateFileSize(item.size));
	}

	ImageView img;

	class LoadThumbnailTask extends AsyncTask<Video, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(Video... params) {
			Bitmap bitmap = BitmapUtils.getVideoThumbnail(params[0].url);
			params[0].resolutionW = bitmap.getWidth();
			params[0].resolutionH = bitmap.getHeight();
			bitmap = BitmapUtils.scaleBitmap(bitmap, ApplicationConfig.VIDEO_THUBM_WIDTH,
					ApplicationConfig.VIDEO_THUBM_HEIGHT);
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			img.setImageBitmap(bitmap);
		}
	}
}
