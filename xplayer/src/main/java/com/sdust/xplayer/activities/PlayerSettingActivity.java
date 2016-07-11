package com.sdust.xplayer.activities;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.sdust.xplayer.R;
import com.sdust.xplayer.config.ApplicationConfig;
import com.sdust.xplayer.utils.LogUtils;
import com.sdust.xplayer.utils.SharedPreferenceUtils;

/**
 * Created by Liu Yongwei on 2016/5/25.
 * <p/>
 * version : 1.0
 */
public class PlayerSettingActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

    /**
     * 画质   0：流畅  1：普通  2：高画质
     */
    private int videoQuantity = 1;

    /**
     * 缓冲大小： 0:0  1:512k  2:1M 3: 4M  4:8M
     */
//    private int bufferSize = 1;
//    private boolean autoPlayNext = true; // 是否自动播放下一个
//    private boolean bufferVideo;  // 是否缓存视频到sd卡
//    private boolean disPlaySubtitle = true;   // 是否自动加载字幕

    private CheckBox mCbAutoPlayNext;
    private CheckBox mBufferVideo;
    private CheckBox mDisplaySubtitle;
    private EditText mFilterSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_setting);

        init();
    }

    private void init() {
        initView();
        setListener();
        boolean autoPlay = SharedPreferenceUtils.getBooleanValue("auto_play");
        mCbAutoPlayNext.setChecked(autoPlay);
        boolean fileBuffer = SharedPreferenceUtils.getBooleanValue("file_buffer");
        mBufferVideo.setChecked(fileBuffer);
        boolean displaySubtitle = SharedPreferenceUtils.getBooleanValue("display_subtitle");
        mDisplaySubtitle.setChecked(displaySubtitle);
    }

    private void initView() {
        mCbAutoPlayNext = (CheckBox) findViewById(R.id.cb_auto_play_next_video);
        mBufferVideo = (CheckBox) findViewById(R.id.cb_buffer_online_video);
        mDisplaySubtitle = (CheckBox) findViewById(R.id.cb_auto_display_subtitle);
        mFilterSize = (EditText) findViewById(R.id.filter_size);
    }

    private void setListener() {
        findViewById(R.id.layout_video_quantity).setOnClickListener(this);
        findViewById(R.id.layout_buffer_size).setOnClickListener(this);
        findViewById(R.id.layout_auto_play_next_video).setOnClickListener(this);
        findViewById(R.id.layout_file_buffer).setOnClickListener(this);
        findViewById(R.id.layout_display_subtitle).setOnClickListener(this);
        findViewById(R.id.layout_video_aspectRatio).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        mCbAutoPlayNext.setOnCheckedChangeListener(this);
        mBufferVideo.setOnCheckedChangeListener(this);
        mDisplaySubtitle.setOnCheckedChangeListener(this);
//        mFilterSize.setOn
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_video_quantity:  //画质
                alertVideoQuantityDialog();
                break;
            case R.id.layout_buffer_size:  // 缓冲大小
                alertBufferSizeDialog();
                break;
            case R.id.layout_video_aspectRatio:
                alertVideoAspectRatioDialog();
                break;
            case R.id.back:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * 弹出画质单选对话框
     */
    private void alertVideoQuantityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择画质");
        final String[] items = new String[]{"流畅", "普通", "高画质"};

        int quantity = SharedPreferenceUtils.getIntValues("video_quantity");
        // 第二个参数表示默认选中的条目。
        builder.setSingleChoiceItems(items, quantity, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoQuantity = which;
                SharedPreferenceUtils.putIntValues("video_quantity", which);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 弹出缓冲大小单选对话框
     */
    private void alertBufferSizeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择缓冲大小");
        final String[] items = new String[]{"0", "512KB", "1MB", "4MB", "8MB"};

        int size = SharedPreferenceUtils.getIntValues("buffer_size");
        builder.setSingleChoiceItems(items, size, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceUtils.putIntValues("buffer_size", which);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 弹出默认画面比例单选对话框
     */
    private void alertVideoAspectRatioDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择默认的视频画面比例");
        final String[] items = new String[]{"自动检测", "1:1", "4:3", "3:2", "14:9", "16:9"};

        int aspectratio = SharedPreferenceUtils.getIntValues("video_aspectratio");
        builder.setSingleChoiceItems(items, aspectratio, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferenceUtils.putIntValues("video_aspectratio", which);
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_auto_display_subtitle:  // 加载同名字幕
                SharedPreferenceUtils.putBoolValues("display_subtitle", isChecked);
                break;
            case R.id.cb_auto_play_next_video:   // 自动播放下一个
                SharedPreferenceUtils.putBoolValues("auto_play", isChecked);
                break;
            case R.id.cb_buffer_online_video:    // 缓存在线视频
                SharedPreferenceUtils.putBoolValues("file_buffer", isChecked);
                break;
            default:
                break;
        }
    }
}