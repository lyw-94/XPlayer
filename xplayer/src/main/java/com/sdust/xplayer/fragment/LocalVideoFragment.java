package com.sdust.xplayer.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.adapter.LocalVideoAdapter;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.helper.VideoHelper;
import com.sdust.xplayer.utils.DialogUtils;
import com.sdust.xplayer.utils.FileUtils;
import com.sdust.xplayer.utils.StringUtils;
import com.sdust.xplayer.utils.ToastUtils;

import java.util.ArrayList;


public class LocalVideoFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener {

	private Context mContext;
	/** fragment对应的view */
	private View mView;
	/** 离线视频列表 */
	private ListView mListView;
	/** 离线视频集合 */
	private ArrayList<Video> mVideoList;
	/** 适配器  */
	private LocalVideoAdapter mAdapter;
	/** 进度条  */
	private ProgressBar mProgressBar;
	/** 加载中  */
	private TextView mTvLoading;
	/** 暂无视频  */
	private TextView mTvNoVideo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_localvideo, null);
		initView();
		initData();
		return mView;
	}

	private void initView() {
		mListView = (ListView) mView.findViewById(R.id.listview_local_video);
		mProgressBar = (ProgressBar) mView.findViewById(R.id.progressbar);
		mTvLoading = (TextView) mView.findViewById(R.id.tv_loading);
		mTvNoVideo = (TextView) mView.findViewById(R.id.tv_novideo);

		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
	}

	private void initData() {
		mContext = getActivity();
		LoadVideoTask task = new LoadVideoTask();
		// 加载视频
		task.execute();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
								   final int position, long id) {
		final Video video = mVideoList.get(position);
		new AlertDialog.Builder(mContext)
				.setCancelable(true)
				.setItems(new String[]{getString(R.string.play), getString(R.string.delete),
						getString(R.string.rename), getString(R.string.details)},
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch (which) {
									case 0:
										VideoHelper.playVideo(mContext, position, mVideoList);
										break;
									case 1:
										mVideoList.remove(video);
										VideoHelper.deleteVideo(video);
										mAdapter.notifyDataSetChanged();
										ToastUtils.showToast("删除成功");
										break;
									case 2: {
										View v = View.inflate(mContext, R.layout.rename_dialog, null);
										final EditText reName = (EditText) v.findViewById(R.id.rename_video);
										reName.setText(video.name);
										//reName.setSelection(0, reName.getText().toString().indexOf("."));
										AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
										builder.setTitle(getString(R.string.rename));
										builder.setView(v);
										builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												String newName = reName.getText().toString();
												if (!newName.endsWith(".3gp") && !newName.endsWith(".mp4") && !newName.endsWith(".mkv") && !newName.endsWith(".rmvb") ) {
													ToastUtils.showToast("命名不合规范！");
												} else if (FileUtils.isSameWithExistsFile(newName,
														video.url.substring(0, video.url.lastIndexOf("/")))) {
													ToastUtils.showToast("与已有文件冲突！");
												} else if (!video.name.equals(newName) && VideoHelper.renameVideo(video, newName)) {
													ToastUtils.showToast("重命名成功！");
													int firstvisibleposition = mListView.getFirstVisiblePosition();
													View v = mListView.getChildAt(position - firstvisibleposition);
													TextView tv = (TextView) v.findViewById(R.id.txt_video_title);
													tv.setText(newName);
												}
												dialog.dismiss();
											}
										});
										builder.show();
									}
										break;
									case 3:
										String content = VideoHelper.generateVideoDetails(video);
										DialogUtils.alertDialog(mContext, getString(R.string.details), content);
										break;
									default:
										break;
								}
							}
						}).show();

		return true;
	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		VideoHelper.playVideo(mContext, position, mVideoList);
	}

	/**
	 * created by Liu Yongwei
	 * 加载并显示所有视频的列表
	 */
	class LoadVideoTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			mVideoList = (ArrayList<Video>) VideoHelper.getVideos();
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mProgressBar.setVisibility(View.GONE);
			mTvLoading.setVisibility(View.GONE);
			mAdapter = new LocalVideoAdapter(mContext, mVideoList,
					R.layout.list_item_local_video);
			mListView.setAdapter(mAdapter);
			if (mVideoList.size() == 0) {
				mTvNoVideo.setVisibility(View.VISIBLE);
			}
			// 这里需要再开一个线程去加载视频的分辨率信息，因为该操作是耗时的
			VideoHelper.GetVideoInfoTask getVideoInfoTask = new VideoHelper.GetVideoInfoTask();
			getVideoInfoTask.execute(mVideoList);
		}
	}

}