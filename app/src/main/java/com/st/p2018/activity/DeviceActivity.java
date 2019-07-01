package com.st.p2018.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.st.p2018.dao.PZDao;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

import java.util.HashMap;
import java.util.List;

public class DeviceActivity extends Activity {

    private TextView gx;
    private CheckBox gc1;
    private CheckBox gc2;
    private CheckBox gc3;
    private CheckBox gc4;
    private CheckBox gc5;
    private CheckBox gc6;
    private TextView tvfh;
    private TextView tvtitle;
    private TextView tvcpxlh;
    private TextView tvyjbbh;
    private  TextView tvgjbbh;
    private Button btnChooseFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_device);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
        initData();
    }

    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("设备信息");
        gx=(TextView)findViewById(R.id.gx);
        gc1=(CheckBox)findViewById(R.id.cb1);
        gc1.setEnabled(false);
        gc2=(CheckBox)findViewById(R.id.cb2);
        gc2.setEnabled(false);
        gc3=(CheckBox)findViewById(R.id.cb3);
        gc3.setEnabled(false);
        gc4=(CheckBox)findViewById(R.id.cb4);
        gc4.setEnabled(false);
        gc5=(CheckBox)findViewById(R.id.cb5);
        gc5.setEnabled(false);
        gc6=(CheckBox)findViewById(R.id.cb6);
        gc6.setEnabled(false);

        if(gx.getText().equals("Ⅰ型")){
            gc6.setVisibility(View.VISIBLE);
        }else{
            gc6.setVisibility(View.INVISIBLE);
        }
        tvcpxlh=(TextView)findViewById(R.id.cpxlh);
        tvyjbbh=(TextView)findViewById(R.id.yjbbh);
        tvgjbbh=(TextView)findViewById(R.id.gjbbh);
        btnChooseFile=(Button)findViewById(R.id.btnchoosefile);
        btnChooseFile.setOnClickListener(new onClickListener());

    }

    private void initData(){
        gx.setText(Cache.gx);
        gc1.setChecked(Cache.gcqy1);
        gc2.setChecked(Cache.gcqy2);
        gc3.setChecked(Cache.gcqy3);
        gc4.setChecked(Cache.gcqy4);
        gc5.setChecked(Cache.gcqy5);
        gc6.setChecked(Cache.gcqy6);
        tvcpxlh.setText(Cache.cpxlh);
        tvyjbbh.setText(Cache.yjbbh);
        tvgjbbh.setText(Cache.gjbbh);


    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.btnchoosefile:
                    //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    //intent.addCategory(Intent.CATEGORY_OPENABLE);
                    //startActivityForResult(intent,1);
                    break;
                case R.id.fh:
                    DeviceActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }



}
