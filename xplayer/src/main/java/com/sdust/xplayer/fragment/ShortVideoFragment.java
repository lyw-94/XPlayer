package com.sdust.xplayer.fragment;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.activities.MediaRecorderActivity;
import com.sdust.xplayer.adapter.LocalVideoAdapter;
import com.sdust.xplayer.entity.Video;
import com.sdust.xplayer.helper.VideoHelper;
import com.sdust.xplayer.utils.DialogUtils;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;


public class ShortVideoFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

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

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float startX = 0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (event.getX() - startX > 0) {
                            LogUtils.e("should hide");
                        }
                        if (event.getX() - startX < 0) {
                            LogUtils.e("should show");
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        //
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
        mTakeVideo.setOnClickListener(this);
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
        VideoHelper.playVideo(mContext, position, mShortVideoList);
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
                                                    ToastUtils.showToast("命名不合规范");
                                                } else if (!video.name.equals(newName) && VideoHelper.renameVideo(video, newName)) {
                                                        ToastUtils.showToast("重命名成功");
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
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File shortVideoDir;
        if (dcim.exists()) {
            shortVideoDir = new File(dcim + "/xplayer/");
        } else {
            shortVideoDir = new File(dcim.getPath().replace("/sdcard/",
                    "/sdcard-ext/") + "/xplayer/");
        }

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
    }
}
