package com.sj.contactlistview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * 通用动态申请权限Activity
 * @author SJ
 */
public class RequestPermissionsActivity extends AppCompatActivity {

    String[] permissions = { Manifest.permission.READ_CONTACTS};
    private boolean isCheckPermissions = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissions(permissions);
    }

    /**
     * 申请权限
     *
     * @param permissions
     *            需要申请的权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(@NonNull String[] permissions) {
        List<String> requestPermissionList = new ArrayList<String>();
        // 找出所有未授权的权限
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(RequestPermissionsActivity.this,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission);
            }
        }
        if (requestPermissionList.isEmpty()) {
            // 已经全部授权
            startActivity();
        } else {
            // 申请授权
            requestPermissions(requestPermissionList.toArray(new String[requestPermissionList.size()]), 1);
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != 1) {
            return;
        }

        if (grantResults.length > 0) {
            final List<String> deniedPermissionList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permissions[i]);
                }
            }

            if (deniedPermissionList.isEmpty()) {
                // 已经全部授权
                startActivity();
            } else {
                // 勾选了对话框中”Don’t ask again”的选项, 返回false
                for (String deniedPermission : deniedPermissionList) {
                    boolean flag = shouldShowRequestPermissionRationale(deniedPermission);
                    if (!flag) {
                        // 拒绝授权
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RequestPermissionsActivity.this);
                        AlertDialog alert = alertDialog.setMessage(
                                "不想用联系人列表的可以自己下载工程添加假数据。反正我是懒得加，怕不安全的可以断网运行")
                                .setCancelable(false).setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        toAppDetail();
                                        isCheckPermissions = true;
                                    }
                                }).setNegativeButton("不开启", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        System.exit(0);
                                    }
                                }).create();
                        alert.show();
                        return;
                    }
                }
                // 拒绝授权
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(RequestPermissionsActivity.this);
                AlertDialog alert = alertDialog.setMessage(
                        "不想用联系人列表的可以自己下载工程添加假数据。反正我是懒得加,怕不安全的可以断网运行")
                        .setCancelable(false).setPositiveButton("去开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(
                                        deniedPermissionList.toArray(new String[deniedPermissionList.size()]));
                            }
                        }).setNegativeButton("不开启", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                System.exit(0);
                            }
                        }).create();
                alert.show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isCheckPermissions) {
            requestPermissions(permissions);
            isCheckPermissions = false;
        }
    }

    public void startActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     */
    public void toAppDetail() {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            startActivity(localIntent);
        } catch (Exception e) {
            Intent intent =  new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        }
    }
}
