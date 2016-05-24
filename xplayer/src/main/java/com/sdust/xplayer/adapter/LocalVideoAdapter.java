package com.sdust.xplayer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.helper.VideoHelper;
import com.sdust.xplayer.utils.BitmapUtils;
import com.sdust.xplayer.utils.FileUtils;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.StringUtils;

import org.xutils.common.util.FileUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class LocalVideoAdapter extends CommonAdapter<Video>{

	public LocalVideoAdapter(Context context, List<Video> mDatas,
			int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		File dir = new File(ApplicationConfig.cacheDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
	}

	@Override
	public void convert(final ViewHolder helper, Video item, int position) {
		loadVideoIcon(helper, item);
		helper.setText(R.id.txt_video_title, item.name);
		helper.setText(R.id.txt_video_duration, StringUtils.generateTime(item.duration));
		helper.setText(R.id.txt_video_size,
				StringUtils.generateFileSize(item.size));
	}

	/**
	 * 加载视频缩略图
	 * @param viewHolder   videoHolder
	 * @param video		   要加载缩略图的视频
     */
	private void loadVideoIcon(ViewHolder viewHolder, Video video) {
		String path = ApplicationConfig.cacheDir + "/" + video.name.substring(0, video.name.lastIndexOf(".")) + ".jpg";
		LogUtils.e("path:" + path);
		File f = new File(path);
		if (f.exists()) {
			ImageOptions options = new ImageOptions.Builder()
					.setLoadingDrawableId(R.drawable.default_video_icon)
					.setFailureDrawableId(R.drawable.default_video_icon)
					.build();
			x.image().bind((ImageView) viewHolder.getView(R.id.img_video_icon), "file:///" + path, options);
			LogUtils.e("框架加载");
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// 获得视频缩略图
			Bitmap bitmap = VideoHelper.getVideoThumbnail(video.url);

			// 压缩
			bitmap = BitmapUtils.scaleBitmap(bitmap, ApplicationConfig.VIDEO_THUBM_WIDTH,
					ApplicationConfig.VIDEO_THUBM_HEIGHT);
			viewHolder.setImageBitmap(R.id.img_video_icon, bitmap);
			// 缓存到本地
			BitmapUtils.saveBitmapToLocal(mContext, bitmap, path);
			LogUtils.e("手动加载");
		}
	}

}
