package com.st.p2018.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.st.p2018.dao.PZDao;
import com.st.p2018.database.DatabaseManager;
import com.st.p2018.database.UpdateDB;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.device.HCProtocol;
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
    private ImageView imageviewload;

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
        closeBar();
        new MainThread().start();

    }
    private void initView(){
        imageviewload=(ImageView) findViewById(R.id.imageviewload);
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(R.drawable.loading).apply(options).into(imageviewload);

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
                Cache.lockScreen="0";
                Cache.chooseSick="0";
            }else{
                Cache.appname=listPZ.get(0).get("appname")==null?"高值耗材柜":listPZ.get(0).get("appname").toString();
                Cache.appcode=listPZ.get(0).get("appcode")==null?"0":listPZ.get(0).get("appcode").toString();
                Cache.ServerIP=listPZ.get(0).get("serverip")==null?"0":listPZ.get(0).get("serverip").toString();
                Cache.ServerPort=Integer.valueOf((listPZ.get(0).get("serverport")==null || listPZ.get(0).get("serverport").equals(""))?"0":listPZ.get(0).get("serverport").toString());
                Cache.lockScreen=listPZ.get(0).get("lockscreen")==null?"0":listPZ.get(0).get("lockscreen").toString();
                Cache.chooseSick=listPZ.get(0).get("choosesick")==null?"0":listPZ.get(0).get("choosesick").toString();
            }

        }catch (Exception e){
            logger.error("初始化app名称及编号出错",e);
        }
    }

    class MainThread extends Thread{
        public void run(){
                try{
                    Cache.isFirstStart=true;
                    initDataBase();
                    initAppName();
                    if( Cache.chooseSick.equals("1")){
                        Utils.getPingYin("张");
                    }
                    getAppVersionName(LoadActivity.this);
                    initExternal();
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

    /**
     * 关闭底部状态栏
     */
    private void closeBar() {
        try {
            //需要root 权限
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79";

            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }
            //需要root 权限
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity " + ProcID + " s16 com.android.systemui"}); //WAS 79
            proc.waitFor();

        } catch (Exception ex) {
           logger.error("aa",ex);
        }
    }



    /**
     * 初始化对外连接
     */
    private void initExternal(){
        if(!Cache.ServerIP.equals("") && Cache.ServerPort!=0){
            logger.info("配置了第三方平台:"+Cache.ServerIP+" "+Cache.ServerPort);
            Cache.external=true;
            new SocketClient().start();
            try{
                Thread.sleep(500);
            }catch (Exception e){

            }

        }
    }

    /**
     * 返回当前程序版本名
     */
    private  void getAppVersionName(Context context) {

        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            Cache.apkversion = pi.versionName;
            logger.info("apk程序版本号："+Cache.apkversion);
        } catch (Exception e) {
            logger.error("获取apk程序版本号出错",e);
        }
    }

}
