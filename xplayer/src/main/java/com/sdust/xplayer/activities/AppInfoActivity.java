package com.sdust.xplayer.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sdust.xplayer.R;

/**
 * Created by Liu Yongwei on 2016/5/31.
 * <p/>
 * version : 1.0
 */
public class AppInfoActivity extends AppCompatActivity {

    private static final String openSourceProject = "vitamio\n  github：https://github.com/yixia/VitamioBundle\n\n" +
            "VitamioBundle\n github：https://github.com/yixia/VitamioBundle\n\n" +
            "xuilts\n github：https://github.com/wyouflf/xUtils3\n\n";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_appinfo);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.opensource_project).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog;

                new AlertDialog.Builder(AppInfoActivity.this)
                        .setTitle("开源库")
                        .setCancelable(true)
                        .setMessage(openSourceProject)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
                            }
                        }).create().show();
//                dialog.show();
            }
        });
    }
}
