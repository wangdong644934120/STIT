package com.st.p2018.activity;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;

import com.st.p2018.database.DatabaseManager;
import com.st.p2018.database.UpdateDB;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.stit.R;
import com.st.p2018.util.MySpeechUtil;
import com.st.p2018.util.MyTextToSpeech;

public class LoadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        //new DeviceCom().start();
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

    /**
     * 初始TTS引擎
     */
    private void initSpeechPlug() {
        try {
            if (!MySpeechUtil.checkSpeechServiceInstall(LoadActivity.this)) {
                MySpeechUtil.processInstall(LoadActivity.this,
                        "SpeechService.apk");
            }
            MyTextToSpeech.getInstance().initial(LoadActivity.this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
