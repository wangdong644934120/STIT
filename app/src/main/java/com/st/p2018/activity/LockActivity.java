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

import com.st.p2018.dao.ExternalPowerDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class LockActivity extends Activity {

    private EditText zhanghao;
    private EditText mima;
    private Button btnlogin;
    private Logger logger = Logger.getLogger(this.getClass());

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
        try{
            zhanghao=(EditText)findViewById(R.id.zhanghao);
            mima=(EditText)findViewById(R.id.mima);
            btnlogin=(Button)findViewById(R.id.login);

            btnlogin.setOnClickListener(new onClickListener());

            Cache.myHandleLockScreen = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                    //提示信息
                    if (bundle.getString("close") != null) {
                        closeActivity();
                    }
                    if(bundle.getString("ui")!=null && bundle.getString("ui").toString().equals("connectfail")){
                        Toast.makeText(LockActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                    if(bundle.getString("alert")!=null){
                        Toast toast=Toast.makeText(LockActivity.this,bundle.getString("alert").toString(),Toast.LENGTH_LONG);
                        showMyToast(toast,10*1000);
                    }
                }
            };
        }catch (Exception e){
            logger.error("初始化view出错",e);
        }

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


                default:
                    break;
            }
        }

    }

    /**
     * 登录验证
     */
    private void login(){
        try{
            Cache.isAdmin="0";//先配置不是管理员登录
            if(zhanghao.getText().toString().trim().equals("") || mima.getText().toString().trim().equals("")){
                Toast.makeText(this, "账号或密码不能为空", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("账号或密码不能为空");
                return;
            }
            if(zhanghao.getText().toString().trim().equals("admin") && mima.getText().toString().trim().equals("3013507")){
                logger.info("admin超级管理员登录，无需验证权限");
                Cache.isAdmin="1";
                closeActivity();
                return;
            }
            String data=zhanghao.getText().toString().trim()+"+"+mima.getText().toString().trim();
            String sendValue="{\"order\":\"power\",\"type\":\"3\",\"code\":\""+Cache.appcode+"\",\"number\":\""+ UUID.randomUUID().toString()+"\",\"data\":\""+data+"\"}";
            if(Cache.external) {
                boolean bl = sendExternal(sendValue);
                if (!bl) {
                    //发送失败，判断本地是否存在刷卡记录，存在则开门
                    ExternalPowerDao powerDao = new ExternalPowerDao();
                    List<HashMap<String, String>> listPower = powerDao.getPower(data, "","3");
                    if (listPower != null && !listPower.isEmpty()) {
                        logger.info("第三方平台权限核验失败，本地用户名密码核验结果：" + listPower.get(0).get("code"));
                        //下发开门指令
                        if (HCProtocol.ST_OpenDoor()) {
                            logger.info("下发开门成功");
                        }
                    } else {
                        logger.info("第三方平台权限核验失败，本地用户名密码核验失败");
                    }
                }
            }
        }catch (Exception e){
            logger.error("登录验证出错",e);
        }

    }



    private boolean sendExternal(String sendValue){
        boolean bl=false;
        //发送数据到第三方平台
        if(SocketClient.socket!=null){
            bl=SocketClient.send(sendValue);
        }
        return bl;
    }

    public void closeActivity(){
        Cache.myHandleLockScreen=null;
        this.finish();
    }
    public void showMyToast(final Toast toast, final int cnt) {
        final Timer timer =new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        },0,3000);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }
}
