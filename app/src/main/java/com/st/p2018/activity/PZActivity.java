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
import android.widget.Toast;

import com.st.p2018.dao.PZDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import java.util.HashMap;
import java.util.List;

public class PZActivity extends Activity {

    private SeekBar sb;
    private Spinner spDK;
    private Button btnOK;
    private int dl;
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
        spDK=(Spinner)findViewById(R.id.spdk);
        btnOK=(Button)findViewById(R.id.btnok);
        btnOK.setOnClickListener(new onClickListener());

        spPD=(Spinner)findViewById(R.id.sppd);
        edpdcs=(EditText)findViewById(R.id.pdcs);
    }

    private void initData(){
        spDK.setSelection(Cache.zmd);
        spPD.setSelection(Cache.pc);
        edpdcs.setText(String.valueOf(Cache.pccs));
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
                    boolean bl=HCProtocol.ST_SetWorkModel(lightModel,pc,pccs);
                    btnOK.setPressed(false);
                    if(bl){
                        Toast.makeText(PZActivity.this, "下传配置成功", Toast.LENGTH_LONG).show();
                        MyTextToSpeech.getInstance().speak("下传配置成功");
                    }else{
                        Toast.makeText(PZActivity.this, "下传配置失败", Toast.LENGTH_LONG).show();
                        MyTextToSpeech.getInstance().speak("下传配置失败");
                    }
                    break;
                default:
                    break;
            }
        }

    }

}
