package com.st.p2018.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */

public class MySpeechUtil {
    /**
     * 判断手机中是否安装了讯飞语音+
     *
     * @param context
     * @return
     */
    public static boolean checkSpeechServiceInstall(Context context) {
        String packageName = "com.iflytek.speechcloud";
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if (packageInfo.packageName.equals(packageName)) {
                return true;
            } else {
                continue;
            }
        }
        return false;
    }

    /**
     * 如果服务组件没有安装，有两种安装方式。 1.直接打开语音服务组件下载页面，进行下载后安装。
     * 2.把服务组件apk安装包放在assets中，为了避免被编译压缩，修改后缀名为mp3，然后copy到SDcard中进行安装。
     */
    public static boolean processInstall(Context context, String assetsApk) {
        String apkPath=ApkInstaller.getFileFromAssets(context, assetsApk);
        if (apkPath!=null) {
            //权限<uses-permission android:name="android.permission.INSTALL_PACKAGES" />
            ApkInstaller.installApk(context, apkPath);
            return true;
        }else{
            Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
