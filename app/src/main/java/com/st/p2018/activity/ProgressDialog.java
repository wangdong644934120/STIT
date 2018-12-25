package com.st.p2018.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.WindowManager;

import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.view.PercentCircle;

public class ProgressDialog extends Activity {

    public static final String action = "jason.broadcast.action";
    BroadcastReceiver broadcastReceiver;
    PercentCircle percentCircle2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
//        PercentCircle percentCircle = (PercentCircle) findViewById(R.id.percentCircle);
        percentCircle2 = (PercentCircle) findViewById(R.id.percentCircle2);
        //percentCircle.setTargetPercent(100);
        percentCircle2.setTargetPercent(1);

        IntentFilter filter = new IntentFilter(action);
        registerReceiver(broadcastReceiver, filter);

        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent){
            // TODO Auto-generated method stub  
                String value=intent.getExtras().getString("data");
                if(value.equals("closepd")){
                    ProgressDialog.this.finish();
                }else{
                    percentCircle2.setTargetPercent(Integer.valueOf(value));
                    percentCircle2.update();
                }

            }
        };
    }
        protected void onDestroy() {
                super.onDestroy();
                unregisterReceiver(broadcastReceiver);
        };
}
