package com.st.p2018.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Administrator on 2018/11/7.
 */

public class ApkInstaller {
    /**
     * 把assets中服务组件apk传到SDcard中，再在SDcord中安装服务组件apk
     *
     * @param context
     * @param assetsApk
     */
    public static String getFileFromAssets(Context context, String assetsApk) {
        try {
            AssetManager assets = context.getAssets();
            // 获取assets资源目录下的SpeechService_1.0.1006.mp3,实际上是SpeechService_1.0.1006.apk,为了避免被编译压缩，修改后缀名。
            InputStream stream;
            stream = assets.open(assetsApk);
            if (stream == null) {
                Toast.makeText(context, "assets no apk", Toast.LENGTH_SHORT).show();
                return null;
            }

            String path= Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"STIT" +File.separator+"tmp";
            File dir=new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String apkPath=path+File.separator+assetsApk;
            //往SDcard中写文件
            if (writeStreamToFile(stream, new File(apkPath))) {
                return apkPath;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    /**
     * 打开语音服务组件下载页面。
     *
     * @param context
     * @param url
     */
    public static void openDownloadWeb(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(it);
    }

    /**
     * 从输入流中写数据到一个文件中。
     *
     * @param stream
     * @param file
     */
    public static boolean writeStreamToFile(InputStream stream, File file) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read;
            while ((read = stream.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
            output.flush();
        } catch (Exception e1) {
            e1.printStackTrace();
            return false;
        } finally {
            try {
                output.close();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 根据apk路径安装apk包。
     *
     * @param context
     * @param apkPath
     */
    public static void installApk(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }
//    /**
//     * 解压文件到指定目录
//     * @param zipPath
//     * @param descDir
//     * @throws IOException
//     */
//    public static void unZipFiles(String zipPath, String descDir) throws IOException {
//        File pathFile = new File(descDir);
//        if (!pathFile.exists()) {
//            pathFile.mkdirs();
//        }
//        ZipFile zip = new ZipFile(new File(zipPath), "GBK");
//        Enumeration entries = zip.getEntries();
//        while (entries.hasMoreElements()) {
//            ZipEntry entry = (ZipEntry) entries.nextElement();
//            String zipEntryName = entry.getName();
//            InputStream in = zip.getInputStream(entry);
//            String outPath = (descDir + File.separator + zipEntryName).replaceAll("\\*", "/");
//            // 判断路径是否存在,不存在则创建文件路径
//            File file = new File(outPath.substring(0, outPath.lastIndexOf('/')));
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
//            if (new File(outPath).isDirectory()) {
//                continue;
//            }
//            // 输出文件路径信息
//            System.out.println(outPath);
//            if (!new File(outPath).exists()) {
//                OutputStream out = new FileOutputStream(outPath);
//                byte[] buf1 = new byte[1024];
//                int len;
//                while ((len = in.read(buf1)) > 0) {
//                    out.write(buf1, 0, len);
//                }
//                in.close();
//                out.close();
//            }
//        }
//    }
    /**
     * 安装程序到system/app下
     *
     * @param apkPath
     * @param apkName
     * @return
     */
    public static int installAppToSystem(String apkPath, String apkName) {
        String cmd0 = "mount -o remount,rw /system";// 让分区可写。
        String cmd1 = "cat " + apkPath + " > /system/app/" + apkName;// 放入系统app下
        String cmd2 = "chmod 777 /system/app/" + apkName;// 将文件属性修改为可执行
        //String cmd3 = "mount -o remount,ro /system";// 还原分区属性，只读。
        return Tools.execRootCmd(new String[] { cmd0, cmd1, cmd2 });
    }
}
