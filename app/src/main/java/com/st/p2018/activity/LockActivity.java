package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import java.util.UUID;

public class LockActivity extends Activity {

    private EditText zhanghao;
    private EditText mima;
    private Button btnlogin;
    private Button btnreset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_lock);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        initView();
    }
    private void initView(){
        zhanghao=(EditText)findViewById(R.id.zhanghao);
        mima=(EditText)findViewById(R.id.mima);
        btnlogin=(Button)findViewById(R.id.login);
        btnreset=(Button)findViewById(R.id.reset);
        btnlogin.setOnClickListener(new onClickListener());
        btnreset.setOnClickListener(new onClickListener());
        Cache.myHandleLockScreen = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("close") != null) {
                    closeActivity();
                }
            }
        };
    }

    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     *
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.login:
                    btnlogin.setPressed(true);
                    login();
                    btnlogin.setPressed(false);
                    break;
                case R.id.reset:
                    btnreset.setPressed(true);
                    reset();
                    btnreset.setPressed(false);
                    break;

                default:
                    break;
            }
        }

    }

    /**
     * 登录验证
     */
    private void login(){
        if(zhanghao.getText().toString().trim().equals("") || mima.getText().toString().trim().equals("")){
            Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("账号或密码不能为空");
            return;
        }
        String data=zhanghao.getText().toString().trim()+"+"+mima.getText().toString().trim();
        String sendValue="{\"order\":\"power\",\"type\":\"3\",\"code\":\""+Cache.appcode+"\",\"number\":\""+ UUID.randomUUID().toString()+"\",\"data\":\""+data+"\"}";
        if(sendExternal(sendValue)){
            return;
        }
    }

    /**
     * 清空输入框内容
     */
    private void reset(){
        zhanghao.setText("");
        mima.setText("");
    }

    private boolean sendExternal(String sendValue){
        boolean bl=false;
        if(Cache.external){
            bl=true;
            //发送数据到第三方平台
            if(SocketClient.socket!=null){
                SocketClient.send(sendValue);
            }
        }
        return bl;
    }

    public void closeActivity(){
        this.finish();
    }
}
