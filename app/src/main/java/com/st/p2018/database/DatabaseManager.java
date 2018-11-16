package com.st.p2018.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.st.p2018.stit.R;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 数据库创建、打开管理类
 * @author lichenfeng
 *
 */
@SuppressLint("NewApi")
public class DatabaseManager {

	private static Logger logger = Logger.getLogger(DatabaseManager.class);
	private static final int BUFFER_SIZE = 1024;//读取文件缓存大小
	// 得到SD卡路径
	public static final String DATABASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "STIT"
			+ File.separator + "DataBase";
	public static final String DATABASE_FILENAME = "stit_client.db";// 数据库文件名称
	public static boolean locked = false;//数据库可用状态标志量

	/**
	 * 如果数据库不存在则创建
	 * @param context
	 * @return
	 */
	// 得到操作数据库的对象
	public static boolean createDatabaseIfNone(Context context) {
		try {
			logger.info("检测数据库是否存在");
			// 得到数据库的完整路径名
			String databaseFilename = DATABASE_PATH + File.separator + DATABASE_FILENAME;
			// 将数据库文件从资源文件放到合适地方（资源文件也就是数据库文件放在项目的res下的raw目录中）/mnt/internal_sd/STIT/DataBase/stit_client.db
			// 将数据库文件复制到SD卡中
			File dir = new File(DATABASE_PATH);
			if (!dir.exists()) {
				dir.mkdir();
			}
			// 判断是否存在该文件
			if (!(new File(databaseFilename)).exists()) {
				// 不存在得到数据库输入流对象
				InputStream is = context.getResources().openRawResource(R.raw.stit_client);
				// 创建输出流
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				// 将数据输出
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				// 关闭资源
				fos.close();
				is.close();
				// 执行升级操作，不检查版本号
				new UpdateDB(context).forceUpdata();
			}
			return true;
		} catch (Exception ex) {
			logger.error("向SD卡创建数据库出错", ex);
			return false;
		}

	}
	/**
	 * 打开数据库：只读连接
	 * @return
	 */
	public synchronized static SQLiteDatabase openReadOnly() {
		if (locked) {
			logger.info("数据库不可用");
			return null;
		} else {
			String dbPath = DATABASE_PATH + File.separator + DATABASE_FILENAME;
			try {
				SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY,errorHandler);
				//db.setLockingEnabled(false);//取消数据库的锁检查
				return db;
			} catch (Exception ex) {
				logger.error("打开数据库连接失败", ex);
				return null;
			}
		}			
	}
	/**
	 * 打开数据库：读、写连接
	 * @return
	 */
	public synchronized static SQLiteDatabase openReadWrite() {
		if (locked) {
			logger.info("数据库不可用");
			return null;
		} else {
			String dbPath = DATABASE_PATH + File.separator + DATABASE_FILENAME;
			try {
				SQLiteDatabase db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE,errorHandler);
				//db.setLockingEnabled(true);//打开数据库的锁检查
				//db.isDatabaseIntegrityOk();//检测数据库是否可用
				return db;
			} catch (Exception ex) {
				logger.error("打开数据库连接失败", ex);
				return null;
			}
		}	
	}
	/**
	 * 打开数据库错误回调
	 */
	private static DatabaseErrorHandler errorHandler=new DatabaseErrorHandler(){
		public void onCorruption(SQLiteDatabase dbObj) {
			logger.error("打开数据库连接失败.............");
        }
	};
}
