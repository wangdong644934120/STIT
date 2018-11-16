package com.st.p2018.database;

//import java.sql.Date;


import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * 数据库维护类
 * 
 * @author lichenfeng
 * 
 */
public class MaintainDB {

	private static Logger logger = Logger.getLogger(DatabaseManager.class);
	private static final int BUFFER_SIZE = 1024 * 1024 * 3;// 读取文件流缓存去大小
	//private static String backupPath = CacheLocal.getBACKUP_PATH();// 备份文件路径
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 删除旧数据
	 */
	public static void delOldData() {
//		try {
//			int originalRecordSaveCount = CacheConfigIni.getRecordSaveCount();
//			String sql = "select time from dwms_t_originalrecord"
//					+ " order by time desc limit " + originalRecordSaveCount + ",1";
//			List<HashMap<String, String>> data = DataBaseExec.execQueryForMap(sql, null);
//			if (data.size() > 0) {
//				// 执行删除操作
//				long time = Long.parseLong(data.get(0).get("time"));
//				String delSQL = "delete from dwms_t_originalrecord where time<" + time;
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//				if (DataBaseExec.execOther(delSQL, null)) {
//					logger.info(sdf.format(time) + "之前的数据删除成功！");
//				} else {
//					logger.info("删除旧数据失败！");
//				}
//			}
//		} catch (Exception ex) {
//			logger.error("旧数据删除方法出错！", ex);
//		}
	}

}
