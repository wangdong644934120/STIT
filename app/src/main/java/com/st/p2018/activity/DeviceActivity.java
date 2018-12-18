package com.st.p2018.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
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
    private Button btnClose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        initView();
        initData();
    }

    private void initView(){
        gx=(TextView)findViewById(R.id.gx);
        gc1=(CheckBox)findViewById(R.id.cb1);
        gc2=(CheckBox)findViewById(R.id.cb2);
        gc3=(CheckBox)findViewById(R.id.cb3);
        gc4=(CheckBox)findViewById(R.id.cb4);
        gc5=(CheckBox)findViewById(R.id.cb5);
        gc6=(CheckBox)findViewById(R.id.cb6);

        if(gx.getText().equals("Ⅰ型")){
            gc6.setVisibility(View.VISIBLE);
        }else{
            gc6.setVisibility(View.INVISIBLE);
        }
        btnClose=(Button)findViewById(R.id.close);
        btnClose.setOnClickListener(new onClickListener());
    }

    private void initData(){
        gx.setText(Cache.gx);
        gc1.setChecked(Cache.gcqy1);
        gc2.setChecked(Cache.gcqy2);
        gc3.setChecked(Cache.gcqy3);
        gc4.setChecked(Cache.gcqy4);
        gc5.setChecked(Cache.gcqy5);
        gc6.setChecked(Cache.gcqy6);
    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.close:
                    DeviceActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

}
