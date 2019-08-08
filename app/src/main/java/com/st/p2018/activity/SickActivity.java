package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.st.p2018.addbook.ContactAdapter;
import com.st.p2018.addbook.DividerItemDecoration;
import com.st.p2018.addbook.LetterView;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.CacheSick;
import com.st.p2018.util.MyTextToSpeech;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class SickActivity extends Activity {

    private RecyclerView contactList;
    private List<String> contactNames;
    private LinearLayoutManager layoutManager;
    private ContactAdapter adapter;
    private TextView tvfh;
    private TextView tvtitle;
    private Button btnSickOK;
    private Button btnSickCancle;
    private ImageView ivGif;
    private RelativeLayout relativeLayout;
    private LinearLayout layoutLoad;
    private Logger logger = Logger.getLogger(SickActivity.class);
    private String sickgg=""; //1-主界面点击更改患者，2-耗材确认界面更改患者

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_sick);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitlenofh);
        initView();
    }

    private  void initView(){
        try{
            tvtitle=(TextView)findViewById(R.id.title);
            tvtitle.setText("选取患者");
            //tvfh=(TextView)findViewById(R.id.fh);
            //tvfh.setOnClickListener(new onClickListener());
            btnSickOK=(Button)findViewById(R.id.sickok);
            btnSickOK.setOnClickListener(new onClickListener());
            btnSickCancle=(Button)findViewById(R.id.sickcancle);
            btnSickCancle.setOnClickListener(new onClickListener());
            ivGif=(ImageView)findViewById(R.id.imageview1);
            relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayouttxl);
            layoutLoad=(LinearLayout)findViewById(R.id.loadsicktxl);

            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(this).load(R.drawable.loadtongyong).apply(options).into(ivGif);

            if(getIntent().getStringExtra("sickgg")!=null){
                sickgg=(String)getIntent().getStringExtra("sickgg");
            }
            getSick();
            Cache.myHandleSick = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                    //提示信息
                    if (bundle.getString("show") != null) {
                        showSick();
                    }
                    if(bundle.getString("ui")!=null && bundle.getString("ui").toString().equals("connectfail")){
                        Toast.makeText(SickActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    }
                    if(bundle.getString("alert")!=null){
                        Toast toast=Toast.makeText(SickActivity.this,bundle.getString("alert").toString(),Toast.LENGTH_LONG);
                        showMyToast(toast,10*1000);
                    }
                }
            };

            contactList = (RecyclerView) findViewById(R.id.contact_list);
            layoutManager = new LinearLayoutManager(this);
            contactList.setLayoutManager(layoutManager);
            contactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

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
                case R.id.sickok:
                    btnSickOK.setPressed(true);
                    sickOK();
                    btnSickOK.setPressed(false);
                    break;
                case R.id.sickcancle:
                    btnSickCancle.setPressed(true);
                    sickCancle();
                    btnSickCancle.setPressed(false);
                    break;

                case R.id.fh:

                    SickActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 确认
     */
    private void sickOK(){
        try{
            //修改主界面显示内容
            Message message = Message.obtain(Cache.myHandle);
            Bundle bund = new Bundle();
            bund.putString("sickgg","4");
            message.setData(bund);
            Cache.myHandle.sendMessage(message);
            if(sickgg.equals("1")){
                if(HCProtocol.ST_OpenDoor()){
                    logger.info("下发开门成功");
                    MyTextToSpeech.getInstance().speak("门已开");
                }else{
                    logger.info("下发开门失败");
                    MyTextToSpeech.getInstance().speak("开门失败");
                }
            }
            if(sickgg.equals("3")){
                //通过点击耗材确认界面的更改按钮打开
                message = Message.obtain(Cache.myHandleAccess);
                bund = new Bundle();
                bund.putString("sickgg","4");
                message.setData(bund);
                Cache.myHandleAccess.sendMessage(message);
            }

        }catch (Exception e){
            logger.error("患者存在点击确认按钮后出错",e);
        }
        Cache.myHandleSick=null;
        this.finish();

    }

    /**
     * 取消选择
     */
    private void sickCancle(){
        //1- 验证登录成功，2-主界面点击更改按钮，3-耗材确认界面点击更改按钮
        if(sickgg.equals("1")){
            CacheSick.sickChoose="";
            if(HCProtocol.ST_OpenDoor()){
                logger.info("下发开门成功");
                MyTextToSpeech.getInstance().speak("门已开");
            }else{
                logger.info("下发开门失败");
                MyTextToSpeech.getInstance().speak("开门失败");
            }
            Cache.myHandleSick=null;
        }else{
        }
        //关闭界面
        SickActivity.this.finish();

    }
    /**
     * 获取患者信息
     */
    private void getSick(){
        try{
            String sendValue="{\"order\":\"patient\",\"code\":\"" + Cache.appcode + "\",\"data\":\"\",\"number\":\""+ UUID.randomUUID().toString()+"\"}";
            if(sendExternal(sendValue)){
                return;
            }
        }catch (Exception e){
            logger.error("发送获取患者信息出错",e);
        }

    }

    /**
     * 显示患者信息
     */
    private void showSick(){
        try{
            logger.info("显示患者信息");
            contactNames= CacheSick.getSickMess();
            adapter = new ContactAdapter(this, contactNames);
            contactList.setAdapter(adapter);
            layoutLoad.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.VISIBLE);
        }catch (Exception e){
            logger.error("显示患者信息出错",e);
        }

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
