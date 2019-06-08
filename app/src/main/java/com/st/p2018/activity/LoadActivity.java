package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.st.p2018.dao.PZDao;
import com.st.p2018.database.DatabaseManager;
import com.st.p2018.database.UpdateDB;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.LogUtil;
import com.st.p2018.util.MySpeechUtil;
import com.st.p2018.util.MyTextToSpeech;
import com.st.p2018.util.Utils;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;

public class LoadActivity extends Activity {

    private Logger logger;
    private TextView tvload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_load);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        //加载日志
        LogUtil.initLog();// 初始log
        logger = Logger.getLogger(this.getClass());
        initView();
        new MainThread().start();

    }
    private void initView(){
        tvload=(TextView)findViewById(R.id.load);

    }
    private boolean initDataBase() {

        try {
            DatabaseManager.createDatabaseIfNone(LoadActivity.this);// 检测数据库，若不存在则创建
            // 数据库连接测试
            SQLiteDatabase db = DatabaseManager.openReadWrite();
            if (db != null && db.isDatabaseIntegrityOk()) {
                //logger.info("打开数据库连接成功");
                db.close();// 关闭数据库
            } else {
                return false;// 数据库打开失败或不可用
            }
            UpdateDB upDB = new UpdateDB(LoadActivity.this);
            upDB.updata();
        } catch (Exception ex) {
            //logger.error("初始化数据库出错", ex);
            return false;
        }
        return true;
    }

    private void initAppName(){
        try{
            PZDao pzDao= new PZDao();
            List<HashMap<String,String>> listPZ = pzDao.getPZ();
            if(listPZ==null || listPZ.isEmpty()){
                Cache.appname="高值耗材柜";
                Cache.appcode="0";
            }else{
                Cache.appname=listPZ.get(0).get("appname")==null?"高值耗材柜":listPZ.get(0).get("appname").toString();
                Cache.appcode=listPZ.get(0).get("appcode")==null?"0":listPZ.get(0).get("appcode").toString();
                Cache.ServerIP=listPZ.get(0).get("serverip")==null?"0":listPZ.get(0).get("serverip").toString();
                Cache.ServerPort=Integer.valueOf(listPZ.get(0).get("serverport")==null?"0":listPZ.get(0).get("serverport").toString());
            }
            if(!Cache.ServerIP.equals("") && Cache.ServerPort!=0){
                logger.info("配置了第三方平台");
                Cache.external=true;
                new SocketClient().start();
            }
        }catch (Exception e){
            logger.error("初始化app名称及编号出错",e);
        }
    }

    class MainThread extends Thread{
        public void run(){
                try{
                    Utils.getPingYin("张");
                    initDataBase();
                    initAppName();
                    Intent intent = new Intent(LoadActivity.this, MainActivity.class);
                    startActivity(intent);
                    closeLoad();
                }catch (Exception e){
                }
        }
    }

    public void closeLoad(){
        this.finish();
    }
}
