package com.st.p2018.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2018/11/7.
 */

public class Tools {
    /**
     * 调用该方法可能会导致阻塞 如果不授权则返回1，正常返回0
     *
     * @param cmds
     * @return
     */
    public static int execRootCmd(String[] cmds) {
        int result = -1;
        Process p = null;
        DataOutputStream dos = null;
        try {
            // p = Runtime.getRuntime().exec("su");// 执行su产生一个具有root权限的进程
            File f = new File("/system/xbin/ru");
            if (f.exists()) {
                p = Runtime.getRuntime().exec("/system/xbin/ru");
            } else {
                p = Runtime.getRuntime().exec("su");
            }
            dos = new DataOutputStream(p.getOutputStream());
            for (String cmd : cmds) {
                dos.writeBytes(cmd + "\n");// 不加\n会导致进程阻塞
                dos.flush();
            }
            dos.writeBytes("exit\n");// 执行结束退出
            dos.flush();
            System.out.println("Output Success!");
            p.waitFor();// 导致当前线程等待，如有必要，一直要等到由该 Process
            // 对象表示的进程已经终止。如果已终止该子进程，此方法立即返回。
            result = p.exitValue();
            System.out.println("Exit code:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭输出流
            if (dos != null) {
                try {
                    dos.close();
                    p.destroy();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
