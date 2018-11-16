package com.st.p2018.database;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 数据库操作封装类（主要）
 * 
 * @author lichenfeng
 * 
 */
public class DataBaseExec {

	private static Logger logger = Logger.getLogger(DataBaseExec.class);
	private static ReadWriteLock lock = new ReentrantReadWriteLock();
	private static final Lock readLock = lock.readLock();
	private static final Lock writeLock = lock.writeLock();
	// 方法缓存，某个实体类与其内部字段的get、set方法对应关系的缓存
	private static HashMap<Class<?>, HashMap<String, Method>> cacheMethodMap = new HashMap<Class<?>, HashMap<String, Method>>();
	private static String currentLockSQL;//当前正在占用写锁的SQL语句,便于排查耗时插入语句

	/**
	 * 更新、插入
	 * 
	 * @param sql
	 * @return
	 */
	public static boolean execOther(String sql, Object[] bindArgs) {
		SQLiteDatabase db = null;
		try {
			writeLock.lock();
			currentLockSQL=sql;
			db = DatabaseManager.openReadWrite();// 打开读写数据库
			if (db != null) {
				if (bindArgs == null) {
					db.execSQL(sql);
				} else {
					db.execSQL(sql, bindArgs);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			logger.error("execOther操作失败", ex);
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			writeLock.unlock();
		}
	}

	/**
	 *   带有事务的更新、插入
	 * @param db 打开的数据库
	 * @param sql 语句
	 * @param bindArgs
	 * @return
	 */
	public static boolean execOtherTransaction(SQLiteDatabase db , String sql, Object[] bindArgs) {
		try {
			writeLock.lock();
			currentLockSQL=sql;
			if (db != null) {
				if (bindArgs == null) {
					db.execSQL(sql);
				} else {
					db.execSQL(sql, bindArgs);
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			logger.error("execOther操作失败", ex);
			return false;
		} finally {
			writeLock.unlock();
		}
	}
	
	/**
	 * 批量插入、批量更新等
	 * 
	 * @param sql
	 * @param params
	 * @return
	 */
	// String sql = "INSERT INTO table (number, nick) VALUES (?, ?)";
	public static boolean multiExec(String sql, List<String[]> params) {
		SQLiteDatabase db = null;
		try {
			writeLock.lock();
			currentLockSQL=sql;
			db = DatabaseManager.openReadWrite();// 打开读写数据库
			if (db != null) {
				db.beginTransaction();// 手动设置开始事务
				try {
					SQLiteStatement stmt = db.compileStatement(sql);
					for (String[] param : params) {
						for (int i = 0; i < param.length; i++) {
							stmt.bindString(i + 1, param[i] == null ? "" : param[i]);
						}
						stmt.execute();
						stmt.clearBindings();
					}
					stmt.close();
					db.setTransactionSuccessful();// 设置事务处理成功，不设置会自动回滚不提交
					// 在setTransactionSuccessful和endTransaction之间不进行任何数据库操作
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					db.endTransaction();// 处理完成
				}
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			logger.error("批量操作执行失败", ex);
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			writeLock.unlock();
		}

	}

	/**
	 * 查询 结果集为List<map>
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return List<HashMap<String,String>>
	 */
	public static List<HashMap<String, String>> execQueryForMap(String sql, String[] selectionArgs) {
		SQLiteDatabase db = null;
		try {
			long time= System.currentTimeMillis();
			readLock.lock();
			if((System.currentTimeMillis()-time)>1000){
				logger.info("查询："+sql+"遭遇阻塞时长大于1000ms的文件锁，当前占用写锁语句为："+currentLockSQL+"阻塞时长为："+(System.currentTimeMillis()-time));
			}
			db = DatabaseManager.openReadOnly();// 打开只读数据库
			if (db != null) {
				Cursor cursor = db.rawQuery(sql, selectionArgs);
				cursor.moveToFirst();
				String[] columns = cursor.getColumnNames();
				List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				while (!cursor.isAfterLast()) {
					HashMap<String, String> hm = new HashMap<String, String>();
					for (String col : columns) {
						hm.put(col, cursor.getString(cursor.getColumnIndex(col)));
					}
					list.add(hm);
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error("execQueryForMap操作失败", ex);
			return null;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			readLock.unlock();
		}

	}

	/**
	 * 查询 结果集为List<bean>
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return List
	 */
	@SuppressLint("DefaultLocale")
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List execQueryForBean(String sql, String[] selectionArgs, Class<?> c) {
		SQLiteDatabase db = null;
		try {
			long time= System.currentTimeMillis();
			readLock.lock();
			if((System.currentTimeMillis()-time)>1000){
				logger.info("查询："+sql+"遭遇阻塞时长大于1000ms的文件锁，当前占用写锁语句为："+currentLockSQL+"阻塞时长为："+(System.currentTimeMillis()-time));
			}
			db = DatabaseManager.openReadOnly();// 打开只读数据库
			if (db != null) {
				Cursor cursor = db.rawQuery(sql, selectionArgs);
				cursor.moveToFirst();
				String[] columns = cursor.getColumnNames();
				HashMap<String, Method> methodMap = getMethodMap(c);// 获取类中set方法与字段映射的集合
				List list = new ArrayList();
				while (!cursor.isAfterLast()) {
					Object obj = null;
					try {
						obj = c.newInstance();
					} catch (Exception e) {
					}
					for (String col : columns) {
						try {
							int index = cursor.getColumnIndex(col);
							Method method = methodMap.get(col.toLowerCase());// 字段不区分大小写
							Class<?> argType = method.getParameterTypes()[0];// 获取方法的参数类型
							if (argType.equals(String.class)) {
								method.invoke(obj, cursor.getString(index));
							} else if (argType.equals(int.class)) {
								method.invoke(obj, cursor.getInt(index));
							} else if (argType.equals(long.class)) {
								method.invoke(obj, cursor.getLong(index));
							} else if (argType.equals(float.class)) {
								method.invoke(obj, cursor.getFloat(index));
							} else if (argType.equals(double.class)) {
								method.invoke(obj, cursor.getDouble(index));
							} else if (argType.equals(short.class)) {
								method.invoke(obj, cursor.getShort(index));
							} else {
								method.invoke(obj, cursor.getString(index));
							}
						} catch (Exception ex) {
						}
					}
					list.add(obj);// 将结果添加到结果集
					cursor.moveToNext();
				}
				cursor.close();
				return list;
			} else {
				return null;
			}
		} catch (Exception ex) {
			logger.error("execQueryForBean操作失败", ex);
			return null;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			readLock.unlock();
		}

	}

	/**
	 * 获取实体类中set方法与字段映射的集合
	 * 
	 * @param c
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static HashMap<String, Method> getMethodMap(Class<?> c) {
		if (cacheMethodMap.get(c) != null) {
			return cacheMethodMap.get(c);
		} else {
			Field[] fields = c.getDeclaredFields();// 获取所有字段属性
			HashMap<String, Method> tmpMethodNameMap = new HashMap<String, Method>();// 方法名称小写到大写的映射集合
			// 将类中所有方法对应到其小写表示
			for (Method method : c.getMethods()) {
				tmpMethodNameMap.put(method.getName().toLowerCase(), method);
			}
			HashMap<String, Method> methodMap = new HashMap<String, Method>();
			for (Field field : fields) {
				String name = field.getName().toLowerCase();// 字段不区分大小写
				Method method = tmpMethodNameMap.get("set" + name);// 获取方法名,不区分大小写
				if (method != null) {
					try {
						if (method.getParameterTypes().length == 1) {// 设参数长度为1的，即为规范的set方法
							methodMap.put(name, method);// 将方法封装到集合
						}
					} catch (Exception ex) {
					}
				}
			}
			cacheMethodMap.put(c, methodMap);
			return methodMap;
		}
	}
	/*
	 * rawQuery()方法的第一个参数为select语句；第二个参数为select语句中占位符参数的值，如果select语句没有使用占位符，
	 * 该参数可以设置为null。 带占位符参数的select语句使用例子如下： Cursor cursor =
	 * db.rawQuery("select * from person where name like ? and age=?", new
	 * String[]{"%传智%", "4"}); 为了防止用户输入中含有‘ & 等特殊字符
	 */
	
	
	
    public static boolean batchExecute(List<String> sqlList) {
        if (sqlList == null || sqlList.isEmpty()) {
            return false;
        }
		SQLiteDatabase db = null;
		try {
			writeLock.lock();
			currentLockSQL="piliangcharu shiwu";
			db = DatabaseManager.openReadWrite();// 打开读写数据库
			if (db != null) {
				db.beginTransaction();//开始事务
				try {
					for (int i = 0; i < sqlList.size(); i++) {
						String sql = sqlList.get(i);
						db.execSQL(sql);
					}
				    db.setTransactionSuccessful();//调用此方法会在执行到endTransaction() 时提交当前事务，如果不调用此方法会回滚事务
				} finally {
				    db.endTransaction();//由事务的标志决定是提交事务，还是回滚事务
				} 
				return true;
			} else {
				return false;
			}
		} catch (Exception ex) {
			logger.error("execOther操作失败", ex);
			return false;
		} finally {
			try {
				db.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			writeLock.unlock();
		}
    }
	
}