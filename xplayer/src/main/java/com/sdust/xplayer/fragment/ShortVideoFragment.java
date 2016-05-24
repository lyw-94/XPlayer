package com.sdust.xplayer.fragment;


import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.activities.MediaRecorderActivity;
import com.sdust.xplayer.activities.ShortVideoPlayerActivity;
import com.sdust.xplayer.adapter.LocalVideoAdapter;
import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.helper.VideoHelper;
import com.sdust.xplayer.utils.DialogUtils;
import com.sdust.xplayer.utils.FileUtils;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.StringUtils;
import com.sdust.xplayer.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;

import javax.xml.datatype.Duration;

public class ShortVideoFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnTouchListener {

    private Context mContext;

    /**
     * fragment对应的view
     */
    private View mView;

    /**
     * 拍摄短视频
     */
    private ImageView mTakeVideo;

    /**
     * 短视频列表
     */
    private ListView mListView;

    /**
     * 短视频集合
     */
    private ArrayList<Video> mShortVideoList;
    private LocalVideoAdapter mAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.newvideo");
        BroadcastReceiver newVideoCreatedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getShortVideos();
                mAdapter.notifyDataSetChanged();
            }
        };
        broadcastManager.registerReceiver(newVideoCreatedReceiver, intentFilter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_shortvideo, null);
        initView();
        initData();
        return mView;
    }

    private void initView() {
        mListView = (ListView) mView.findViewById(R.id.short_video_list);
        mTakeVideo = (ImageView) mView.findViewById(R.id.take_video);

        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mTakeVideo.setOnClickListener(this);

        mListView.setOnTouchListener(this);
    }


    private void initData() {
        mContext = getActivity();
        mShortVideoList = new ArrayList<>();
        getShortVideos();
        mAdapter = new LocalVideoAdapter(mContext, mShortVideoList,
                R.layout.list_item_local_video);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_video:
                Intent intent = new Intent(mContext, MediaRecorderActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //VideoHelper.playVideo(mContext, position, mShortVideoList);
        Intent intent = new Intent(mContext, ShortVideoPlayerActivity.class);
        intent.putExtra("path", mShortVideoList.get(position).url);
        intent.putExtra("entry", "shortVideoFragment");
        mContext.startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final Video video = mShortVideoList.get(position);
        new AlertDialog.Builder(mContext)
                .setCancelable(true)
                .setItems(new String[]{
                                getString(R.string.play),
                                getString(R.string.delete),
                                getString(R.string.rename),
                                getString(R.string.details),
                                getString(R.string.push_video)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        VideoHelper.playVideo(mContext, position, mShortVideoList);
                                        break;
                                    case 1:
                                        mShortVideoList.remove(video);
                                        VideoHelper.deleteVideo(video);
                                        mAdapter.notifyDataSetChanged();
                                        ToastUtils.showToast("删除成功");
                                        break;
                                    case 2: {
                                        View v = View.inflate(mContext, R.layout.rename_dialog, null);
                                        final EditText reName = (EditText) v.findViewById(R.id.rename_video);
                                        reName.setText(video.name);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setTitle(getString(R.string.rename));
                                        builder.setView(v);
                                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String newName = reName.getText().toString();
                                                if (!newName.endsWith(".3gp") && !newName.endsWith(".mp4")) {
                                                    ToastUtils.showToast("命名不合规范！");
                                                } else if (FileUtils.isSameWithExistsFile(newName,
                                                        video.url.substring(0, video.url.lastIndexOf("/")))) {
                                                    ToastUtils.showToast("与已有文件冲突！");
                                                } else if (!video.name.equals(newName) && VideoHelper.renameVideo(video, newName)) {
                                                    ToastUtils.showToast("重命名成功！");
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
                                    case 4:
                                        ToastUtils.showToast("上传，未实现");
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }).show();
        return true;
    }

    /**
     * 得到目录下所有短视频
     */
    public void getShortVideos() {
        File shortVideoDir;
        shortVideoDir = new File(ApplicationConfig.videoDir);

        File[] files = shortVideoDir.listFiles();
        mShortVideoList.clear();
        for (File f : files) {
            if (f.getName().endsWith(".mp4")) {  // 视频
                Video video = new Video();
                video.name = f.getName();
                video.size = f.length();
                video.url = f.getAbsolutePath();

                mShortVideoList.add(video);
            }
        }
        // 这里需要再开一个线程去加载视频的分辨率信息，因为该操作是耗时的
        GetShortVideoInfoTask getVideoInfoTask = new GetShortVideoInfoTask();
        getVideoInfoTask.execute(mShortVideoList);
    }

    class GetShortVideoInfoTask extends AsyncTask<ArrayList<Video>, Integer, Void> {

        @Override
        protected Void doInBackground(ArrayList<Video>... params) {
            ArrayList<Video> list = params[0];

            for (int i = 0; i < list.size(); i++) {
                Video video = list.get(i);
                video.resolutionH = Integer.parseInt(VideoHelper.getVideoHeight(video.url));
                video.resolutionW = Integer.parseInt(VideoHelper.getVideoWidth(video.url));
                if (video.duration == 0) {
                    video.duration = Long.parseLong(VideoHelper.getVideoDuration(video.url));
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (int i = 0; i < mListView.getChildCount(); i++) {
                View v = mListView.getChildAt(i);
                TextView tv = (TextView) v.findViewById(R.id.txt_video_duration);
                tv.setText(StringUtils.generateTime(mShortVideoList.get(i).duration));
            }
        }
    }

    float lastY = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
                LogUtils.e("down:" + lastY);
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtils.e("getY:" + event.getY() + "  lastY: " + lastY);
                if (event.getY() - lastY > 5) {
                    LogUtils.e("下滑");
                    mListView.getLastVisiblePosition();
                    mListView.getChildCount();
                    addVideoImgAnimation(0);
                } else if (event.getY() - lastY < -5) {
                    LogUtils.e("上滑");
                    addVideoImgAnimation(1);
                }
                lastY = event.getY();
                break;
            default:
                break;
        }

        return false;
    }


    // 添加按钮是显示还是隐藏
    private boolean isAppear = true;

    /**
     * 添加按钮动画效果
     */
    private void addVideoImgAnimation(int type) {
//        if (!isDataFillOneScreen()) {   // 如果数据不满一屏，则屏蔽动画。
//            return;
//        }
        ObjectAnimator objectAnimator = null;
        if (type == 0 && !isAppear) {
            objectAnimator = ObjectAnimator.ofFloat(mTakeVideo, "TranslationY", 0);
            isAppear = true;
            objectAnimator.setDuration(700);
            objectAnimator.start();
        }
        if (type == 1 && isAppear) {
            objectAnimator = ObjectAnimator.ofFloat(mTakeVideo, "TranslationY", 200);
            isAppear = false;
            objectAnimator.setDuration(700);
            objectAnimator.start();
        }
    }

    /**
     * 判断listview的数据是否充满一屏幕
     * @return
     *      true:  充满一屏
     *      false：未充满一屏
     */
    private boolean isDataFillOneScreen() {
        if ((mListView.getFirstVisiblePosition() == 0)
                && (mListView.getLastVisiblePosition() == mListView.getChildCount() - 1) ) {
            return false;
        }

        return true;
    }
}
