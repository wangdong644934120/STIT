package com.st.p2018.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

public class SelectDialog extends AlertDialog {

    private Button btnRY;
    private Button btnHC;
    private Button btnKZ;
    private Button btnPZ;
    private Button btnSBXX;
    private Button btnTA;

    public SelectDialog(Context context, int theme) {
        super(context, theme);
    }

    public SelectDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slt_cnt_type);
        initView();
    }

    private void initView(){
        btnRY=(Button)findViewById(R.id.btnRY);
        btnRY.setOnClickListener(new onClickListener());
        btnHC=(Button)findViewById(R.id.btnHC);
        btnHC.setOnClickListener(new onClickListener());
        btnKZ=(Button)findViewById(R.id.btnKZ);
        btnKZ.setOnClickListener(new onClickListener());
        btnPZ=(Button)findViewById(R.id.btnPZ);
        btnPZ.setOnClickListener(new onClickListener());
        btnSBXX=(Button)findViewById(R.id.btnSBXX);
        btnSBXX.setOnClickListener(new onClickListener());
        btnTA=(Button)findViewById(R.id.ta);
        btnTA.setOnClickListener(new onClickListener());
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
                case R.id.btnRY:
                    openui("ry");
                    break;
                case R.id.btnHC:
                    openui("hc");
                    break;
                case R.id.btnKZ:
                    openui("kz");
                    break;
                case R.id.btnPZ:
                    openui("pz");
                    break;
                case R.id.btnSBXX:
                    openui("sbxx");
                    break;
                case R.id.ta:
                    openui("ta");
                    break;
                default:
                    break;
            }
        }

        private void openui(String ui){
            Message message = Message.obtain(Cache.myHandle);
            Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            data.putString("ui",ui);
            message.setData(data);
            Cache.myHandle.sendMessage(message);
            SelectDialog.this.dismiss();
        }

    }

}
