package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.st.p2018.stit.R;
import com.st.p2018.view.PercentCircle;

public class ProgressDialog extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
//        PercentCircle percentCircle = (PercentCircle) findViewById(R.id.percentCircle);
        PercentCircle percentCircle2 = (PercentCircle) findViewById(R.id.percentCircle2);
        //percentCircle.setTargetPercent(100);
        percentCircle2.setTargetPercent(100);

    }
}
