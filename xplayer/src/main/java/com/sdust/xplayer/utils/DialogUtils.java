package com.sdust.xplayer.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sdust.xplayer.R;

/**
 * Created by Liu Yongwei on 2016/5/6.
 * <p>
 * version : 1.0
 */
public class DialogUtils {

    /**
     * 弹出带一个确定按钮的对话框
     * @param context   上下文对象，注意这个不能用applicationContext代替！！
     * @param title     对话框标题
     * @param content   对话框内容
     */
    public static void alertDialog(Context context, String title, String content) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        builder.setTitle(title);
        builder.setMessage(content);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 弹出一个带EditText的对话框
     * @param context  不能用applicationcontext代替
     * @param title    对话框标题
     * @param content  EditText默认文本
     * @return EditText编辑后文本
     */
    public static String alertDialogWithEditText(Context context, String title, String content) {
        final Bundle bundle = new Bundle();
        View v = View.inflate(context, R.layout.rename_dialog, null);
        final EditText reName = (EditText) v.findViewById(R.id.rename_video);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        builder.setTitle(title);
        builder.setView(v);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String s = reName.getText().toString();
                // 这里实在不知道咋整了，，
                bundle.putString("content", s);
                bundle.putBoolean("clicked", true);
                dialog.dismiss();
            }
        });

        reName.setText(content);
        builder.show();

        if (bundle.getBoolean("clicked")) {
            return bundle.getString("content");
        } else {
            return content;
        }
    }
}
