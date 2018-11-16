/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.st.p2018.database;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


import com.st.p2018.stit.R;

import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * 数据库升级管理类
 * @author wd
 *
 */
public class UpdateDB {

	private Logger logger = Logger.getLogger(UpdateDB.class.getName());
	private Context context;
	private SharedPreferences mySharedPreferences;//缓存

	/**
	 * 方法说明：升级数据库
	 */
	public UpdateDB(Context context) {
		this.context = context;
		mySharedPreferences= context.getSharedPreferences("DWMS", Activity.MODE_PRIVATE);
	}
	
	public void updata() {
		LinkedHashMap<String, ArrayList<String>> sqlMap = getUpdateSQLStrFromFile();
		Object[] versions = sqlMap.keySet().toArray();
		String verson = versions[versions.length - 1].toString();
		String dababaseVersion = mySharedPreferences.getString("dbVersion", "0");
		if (!verson.equals(dababaseVersion)) {
			doUpdate(sqlMap);
			Editor editor = mySharedPreferences.edit();
			editor.putString("dbVersion",verson);
			editor.commit();
		} else {
			logger.info("数据库无版本升级！");
		}
	}
	/**
	 * 不检查版本，直接升级数据库
	 */
	public void forceUpdata(){
		LinkedHashMap<String, ArrayList<String>> sqlMap = getUpdateSQLStrFromFile();
		doUpdate(sqlMap);
	}
	/**
	 * 方法说明：执行SQL
	 * 
	 * @param
	 * @return
	 */
	private  void doUpdate(LinkedHashMap<String, ArrayList<String>> map) {
		for (String oneVersion : map.keySet()) {
			if (oneVersion.split("\\_").length > 1) {
				logger.info("执行脚本，升级数据库至：" + oneVersion.split("\\_")[1]);
			}
			ArrayList<String> list = map.get(oneVersion);
			for (String oneSql : list) {
				try {
					DataBaseExec.execOther(oneSql, null);
					Thread.sleep(200);
				} catch (Exception ex) {
					logger.error("数据库升级出错,脚本版本："+ oneVersion.split("\\_")[1]);
				}
			}
		}
	}

	/**
	 * 方法说明：从文件中获取所需执行的SQL语句
	 * 
	 * @return
	 */
	private LinkedHashMap<String, ArrayList<String>> getUpdateSQLStrFromFile() {
		LinkedHashMap<String, ArrayList<String>> sqlMap = new LinkedHashMap<String, ArrayList<String>>();
		org.dom4j.Document document = null;
		try {
			InputStream ios = context.getResources().openRawResource(R.raw.update);
			document = new SAXReader().read(ios).getDocument();
		} catch (DocumentException ex) {
			logger.error("读取update.xml文件错误", ex);
		}
		Element rootElt = document.getRootElement(); // 获取根节点
		Iterator parIter = rootElt.elementIterator(); // 获取根节点下的子节点mxCell
		while (parIter.hasNext()) {
			ArrayList<String> oneUpdateList = new ArrayList<String>();
			Element oneUpdateEle = (Element) parIter.next();
			sqlMap.put(oneUpdateEle.getName(), oneUpdateList);
			oneUpdateEle.getName();
			Iterator sqlStrIte = oneUpdateEle.elementIterator("update"); // 获取根节点下的子节点mxCell
			while (sqlStrIte.hasNext()) {
				Element oneSqlStrEle = (Element) sqlStrIte.next();
				oneUpdateList.add(oneSqlStrEle.getText());
			}
		}
		return sqlMap;
	}
}
