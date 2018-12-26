package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.MyTextToSpeech;

public class KZActivity extends Activity {

    private Button btnKM;
    private Button btnKD;
    private Button btnGD;
    private Button btnSCSYZW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kz);
        initView();
    }
    private void initView(){
        btnKM=(Button)findViewById(R.id.km);
        btnKM.setOnClickListener(new onClickListener());
        btnKD=(Button)findViewById(R.id.kd);
        btnKD.setOnClickListener(new onClickListener());
        btnGD=(Button)findViewById(R.id.gd);
        btnGD.setOnClickListener(new onClickListener());
        btnSCSYZW=(Button)findViewById(R.id.scsyzw);
        btnSCSYZW.setOnClickListener(new onClickListener());
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
                case R.id.km:
                    btnKM.setPressed(true);
                    boolean bl=HCProtocol.ST_OpenDoor();
                    btnKM.setPressed(false);
                    if(bl){
                        sendTS("开门成功");
                    }else{
                        sendTS("开门失败");
                    }
                    break;

                case R.id.kd:
                    btnKD.setPressed(true);
                    bl=HCProtocol.ST_OpenLight();
                    btnKD.setPressed(false);
                    if(bl){
                        sendTS("开灯成功");
                    }else{
                        sendTS("开灯失败");
                    }
                    break;
                case R.id.gd:
                    btnGD.setPressed(true);
                    bl=HCProtocol.ST_CloseLight();
                    btnGD.setPressed(false);
                    if(bl){
                        sendTS("关灯成功");
                    }else{
                        sendTS("关灯失败");
                    }
                    break;
                case R.id.scsyzw:
                    btnSCSYZW.setPressed(true);
                    bl=HCProtocol.ST_DeleteZW(1,0);
                    btnSCSYZW.setPressed(false);
                    if(bl){
                        sendTS("删除所有指纹成功");
                    }else{
                        sendTS("删除所有指纹失败");
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private  void sendTS(String value){
        MyTextToSpeech.getInstance().speak(value);
        Toast.makeText(this, value, Toast.LENGTH_SHORT).show();
    }
}

