package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;

public class KZActivity extends Activity {

    private Button btnKM;
    private Button btnGM;
    private Button btnKD;
    private Button btnGD;
    private Button btnKYH;
    private Button btnZWYH;
    private Button btnPCSJ;
    private Button btnTJZW;
    private Button btnSCZW;
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
        btnGM=(Button)findViewById(R.id.gm);
        btnGM.setOnClickListener(new onClickListener());
        btnKD=(Button)findViewById(R.id.kd);
        btnKD.setOnClickListener(new onClickListener());
        btnGD=(Button)findViewById(R.id.gd);
        btnGD.setOnClickListener(new onClickListener());
        btnKYH=(Button)findViewById(R.id.kyh);
        btnKYH.setOnClickListener(new onClickListener());
        btnZWYH=(Button)findViewById(R.id.zwyh);
        btnZWYH.setOnClickListener(new onClickListener());
        btnPCSJ=(Button)findViewById(R.id.pcsj);
        btnPCSJ.setOnClickListener(new onClickListener());
        btnTJZW=(Button)findViewById(R.id.tjzw);
        btnTJZW.setOnClickListener(new onClickListener());
        btnSCZW=(Button)findViewById(R.id.sczw);
        btnSCZW.setOnClickListener(new onClickListener());
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
                    HCProtocol.ST_OpenDoor();
                    btnKM.setPressed(false);

                    break;
                case R.id.gm:
                    btnGM.setPressed(true);
//                    HCProtocol.ST_SetWorkModel(lightModel,pc,pccs);
                    btnGM.setPressed(false);

                    break;
                case R.id.kd:
                    btnKD.setPressed(true);
                    HCProtocol.ST_OpenLight();
                    btnKD.setPressed(false);

                    break;
                case R.id.gd:
                    btnGD.setPressed(true);
                    HCProtocol.ST_CloseLight();
                    btnGD.setPressed(false);

                    break;
                case R.id.kyh:
                    btnKYH.setPressed(true);
                    HCProtocol.ST_GetUser(0);
                    btnKYH.setPressed(false);

                    break;
                case R.id.zwyh:
                    btnZWYH.setPressed(true);
                    HCProtocol.ST_GetUser(1);
                    btnZWYH.setPressed(false);

                    break;
                case R.id.pcsj:
                    btnZWYH.setPressed(true);
                    HCProtocol.ST_GetCard();
                    btnZWYH.setPressed(false);

                    break;
                case R.id.scsyzw:
                    btnSCSYZW.setPressed(true);
                    HCProtocol.ST_DeleteZW(1,1);
                    btnSCSYZW.setPressed(false);

                    break;
                case R.id.tjzw:
                    btnTJZW.setPressed(true);
                    HCProtocol.ST_AddSaveZW(1);
                    btnTJZW.setPressed(false);

                    break;
                case R.id.sczw:
                    btnSCZW.setPressed(true);
                    HCProtocol.ST_DeleteZW(0,1);
                    btnSCZW.setPressed(false);

                    break;
                default:
                    break;
            }
        }

    }
}

