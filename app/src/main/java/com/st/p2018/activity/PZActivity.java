package com.st.p2018.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.st.p2018.dao.PZDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;

import java.util.HashMap;
import java.util.List;

public class PZActivity extends Activity {

    private SeekBar sb;
    private Spinner spGX;
    private Spinner spDK;
    private Button btnOK;
    private int dl;
    private CheckBox gc1;
    private CheckBox gc2;
    private CheckBox gc3;
    private CheckBox gc4;
    private CheckBox gc5;
    private CheckBox gc6;
    private Spinner spPD;
    private EditText edpdcs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pz);
        initView();
        initData();
    }

    private void initView(){
        sb=(SeekBar) findViewById(R.id.sb);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置屏幕的亮度
                setScreenBrightness(seekBar.getProgress());
                dl=seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        spGX=(Spinner)findViewById(R.id.spgx);

        spGX.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if(spGX.getSelectedItem().toString().equals("Ⅰ型")){
                    gc6.setVisibility(View.VISIBLE);
                }else{
                    gc6.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        spDK=(Spinner)findViewById(R.id.spdk);

        btnOK=(Button)findViewById(R.id.btnok);
        btnOK.setOnClickListener(new onClickListener());
        gc1=(CheckBox)findViewById(R.id.cb1);
        gc2=(CheckBox)findViewById(R.id.cb2);
        gc3=(CheckBox)findViewById(R.id.cb3);
        gc4=(CheckBox)findViewById(R.id.cb4);
        gc5=(CheckBox)findViewById(R.id.cb5);
        gc6=(CheckBox)findViewById(R.id.cb6);
        if(spGX.getSelectedItem().toString().equals("Ⅰ型")){
            gc6.setVisibility(View.VISIBLE);
        }else{
            gc6.setVisibility(View.INVISIBLE);
        }
        spPD=(Spinner)findViewById(R.id.sppd);
        edpdcs=(EditText)findViewById(R.id.pdcs);
    }

    private void initData(){
        PZDao pzDao = new PZDao();
        List<HashMap<String,String>> list = pzDao.getPZ();
        spGX.setSelection(list.get(0).get("gx").toString().equals("Ⅰ型")?0:1);
        int dk;
        if(list.get(0).get("dk").toString().equals("灯自动")){
            dk=0;
        }else if(list.get(0).get("dk").toString().equals("灯常开")){
            dk=1;
        }else {
            dk=2;
        }
        spDK.setSelection(dk);
        gc1.setChecked(list.get(0).get("gc1").toString().equals("1")?true:false);
        gc2.setChecked(list.get(0).get("gc2").toString().equals("1")?true:false);
        gc3.setChecked(list.get(0).get("gc3").toString().equals("1")?true:false);
        gc4.setChecked(list.get(0).get("gc4").toString().equals("1")?true:false);
        gc5.setChecked(list.get(0).get("gc5").toString().equals("1")?true:false);
        gc6.setChecked(list.get(0).get("gc6").toString().equals("1")?true:false);

    }
    /**
     * 设置屏幕的亮度
     */
    private void setScreenBrightness(int process) {

        //设置当前窗口的亮度值.这种方法需要权限android.permission.WRITE_EXTERNAL_STORAGE
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        float f = process / 255.0F;
        localLayoutParams.screenBrightness = f;
        getWindow().setAttributes(localLayoutParams);
        //修改系统的亮度值,以至于退出应用程序亮度保持
        saveBrightness(getContentResolver(),process);

    }
    public  void saveBrightness(ContentResolver resolver, int brightness) {
        //改变系统的亮度值
        //这里需要权限android.permission.WRITE_SETTINGS
        //设置为手动调节模式
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //保存到系统中
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
    }

    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.btnok:
                    btnOK.setPressed(true);
//                    HashMap<String,String> mapSave = new HashMap<String,String>();
//                    mapSave.put("gx",spGX.getSelectedItem().toString());
//                    mapSave.put("dk",spDK.getSelectedItem().toString());
//                    mapSave.put("pl",String.valueOf(dl));
//                    mapSave.put("gc1",gc1.isChecked()?"1":"0");
//                    mapSave.put("gc2",gc2.isChecked()?"1":"0");
//                    mapSave.put("gc3",gc3.isChecked()?"1":"0");
//                    mapSave.put("gc4",gc4.isChecked()?"1":"0");
//                    mapSave.put("gc5",gc5.isChecked()?"1":"0");
//                    mapSave.put("gc6",gc6.isChecked()?"1":"0");
//                    PZDao pzDao= new PZDao();
//                    pzDao.updatePZ(mapSave);
                    int lightModel=0;
                    if(spDK.getSelectedItem().toString().equals("灯自动")){
                        lightModel=0;
                    }else if(spDK.getSelectedItem().toString().equals("灯常开")){
                        lightModel=1;
                    }else if(spDK.getSelectedItem().toString().equals("灯常关")){
                        lightModel=2;
                    }
                    int pc=0;
                    if(spPD.getSelectedItem().toString().equals("全部盘存")){
                        pc=0;
                    }else if(spPD.getSelectedItem().toString().equals("触发盘存")){
                        pc=1;
                    }
                    int pccs=1;
                    try{
                        pccs=Integer.valueOf(edpdcs.getText().toString());
                    }catch (Exception e){

                    }

                    HCProtocol.ST_SetWorkModel(lightModel,pc,pccs);
                    btnOK.setPressed(false);

//                    HCProtocol.ST_SetWorkModel()
                    break;
                default:
                    break;
            }
        }

    }

}
