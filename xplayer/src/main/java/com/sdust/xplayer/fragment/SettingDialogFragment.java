package com.sdust.xplayer.fragment;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sdust.xplayer.R;
import com.sdust.xplayer.utils.LogUtils;

/**
 * Created by Liu Yongwei on 2016/5/27.
 * <p/>
 * version : 1.0
 */
public class SettingDialogFragment extends DialogFragment implements RadioGroup.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    private RadioGroup videoQuality;
    private RadioButton nomal;
    private RadioButton flex;
    private RadioButton high;

    private Button confirm;
    private SeekBar videoSpeed;
    private CheckBox videoCycle;
    private TextView tvVideoSpeed;
    private static int quality = 1;
    private static int speed = 10;
    private static boolean isCycle;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // 去掉标题
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.fragment_dialog_setting, null);

        confirm = (Button) v.findViewById(R.id.confirm);
        videoQuality = (RadioGroup) v.findViewById(R.id.video_quality);
        videoSpeed = (SeekBar) v.findViewById(R.id.play_speed);
        videoCycle = (CheckBox) v.findViewById(R.id.play_recycle);
        tvVideoSpeed = (TextView) v.findViewById(R.id.tv_play_speed);
        nomal = (RadioButton) v.findViewById(R.id.normal);
        flex = (RadioButton) v.findViewById(R.id.flex);
        high = (RadioButton) v.findViewById(R.id.high);

        videoQuality.setOnCheckedChangeListener(this);
        videoSpeed.setOnSeekBarChangeListener(this);
        videoCycle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCycle = true;
                } else {
                    isCycle = false;
                }
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float speed = videoSpeed.getProgress();
                boolean isCycle = videoCycle.isChecked();
                listener.onConfirm(quality, speed/10.0f, isCycle);
            }
        });


        // 下面这些用来恢复上次设置的值，避免每次生成对话框都要重新设置
        if (quality != 1) {
            if (quality == 0) {
                flex.setChecked(true);
            }
            if (quality == 2) {
                high.setChecked(true);
            }
        }
        if (isCycle) {
            videoCycle.setChecked(true);
        }

        videoSpeed.setProgress(speed);
        tvVideoSpeed.setText("播放速度：" + speed/10.0 + "倍");

        return v;
    }

    private OnSettingCompleteListener listener;
    public void setOnSettingCompleteListener (OnSettingCompleteListener listener){
        this.listener = listener;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.flex:
                quality = 0;
                break;
            case R.id.normal:
                quality = 1;
                break;
            case R.id.high:
                quality = 2;
                break;
            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        speed = videoSpeed.getProgress();
        tvVideoSpeed.setText("播放速度：" + videoSpeed.getProgress()/10.0 + "倍");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnSettingCompleteListener {
        /**
         * 点击确定按钮时执行
         * @param videoQuality  画质
         * @param videoSpeed    播放速度
         * @param isCycle       是否循环播放
         */
         void onConfirm(int videoQuality, float videoSpeed, boolean isCycle);
    }
}
