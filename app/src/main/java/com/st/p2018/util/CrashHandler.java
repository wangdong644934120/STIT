package com.st.p2018.util;

import android.content.Context;
import android.os.Environment;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/**
 * Created by Administrator on 2019/8/18.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    private static Logger logger = Logger.getLogger(CrashHandler.class);

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 核心方法，当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {

           String logPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator+"STIT" + File.separator + "logs";
            File file = new File(logPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                FileWriter fw = new FileWriter(logPath + File.separator + "errorlog.log", true);
                fw.write(new Date() + "\n");        // 错误信息        // 这里还可以加上当前的系统版本，机型型号 等等信息
                StackTraceElement[] stackTrace = arg1.getStackTrace();
                fw.write(arg1.getMessage() + "\n");
                for (int i = 0; i < stackTrace.length; i++) {
                    fw.write("file:" + stackTrace[i].getFileName() + " class:" + stackTrace[i].getClassName() + " method:" + stackTrace[i].getMethodName() + " line:" + stackTrace[i].getLineNumber() + "\n");
                }
                fw.write("\n");
                fw.close();
            } catch (IOException e) {
                logger.error("crash handler", e);
            }

            logger.error("未捕获的异常",arg1);
        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
