package com.st.p2018.util;

import android.os.Environment;



import org.apache.log4j.Level;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * log工具
 * @author wd
 *
 */
public class LogUtil {
	public static void initLog() {
		LogConfigurator logConfigurator = new LogConfigurator();

		//String fname = context.getFilesDir().getPath().toString() + "/screenshot.png";

		String file=Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "STIT"
				+ File.separator + "logs"+ File.separator+ "log.log";
		System.out.println("是否存在："+new File(file).exists());
		logConfigurator.setFileName(file);
		logConfigurator.setRootLevel(Level.INFO);//设置LOG级别INFO
		logConfigurator.setLevel("com.example", Level.INFO);
		logConfigurator.setFilePattern("[%-d{yyyy-MM-dd HH:mm:ss.SSS}][%5p][%-10c][%L]%m%n");
		logConfigurator.setMaxBackupSize(20);//最多记录20个
		logConfigurator.setMaxFileSize(1024 * 1024); //
		logConfigurator.setImmediateFlush(true);
		logConfigurator.configure();
	}
}
