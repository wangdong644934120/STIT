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

import org.apache.log4j.Logger;

import java.util.List;
import java.util.UUID;


public class SickActivity extends Activity {

    private RecyclerView contactList;
    private List<String> contactNames;
    private LinearLayoutManager layoutManager;
    private LetterView letterView;
    private ContactAdapter adapter;
    private TextView tvfh;
    private TextView tvtitle;
    private Button btnSickOK;
    private Button btnSickCancle;
    private ImageView ivGif;
    private RelativeLayout relativeLayout;
    private LinearLayout layoutLoad;
    private Logger logger = Logger.getLogger(SickActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_sick);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("选取患者");
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        btnSickOK=(Button)findViewById(R.id.sickok);
        btnSickOK.setOnClickListener(new onClickListener());
        btnSickCancle=(Button)findViewById(R.id.sickcancle);
        btnSickCancle.setOnClickListener(new onClickListener());
        ivGif=(ImageView)findViewById(R.id.imageview1);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayouttxl);
        layoutLoad=(LinearLayout)findViewById(R.id.loadsicktxl);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        Glide.with(this).load(R.drawable.loadsick).apply(options).into(ivGif);


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
            }
        };
        //contactNames = new String[] {"456","张三丰", "郭靖", "黄蓉", "黄老邪", "赵敏", "123", "天山童姥", "任我行", "于万亭", "陈家洛+2019-12-20+201912200001+心内一", "韦小宝", "$6", "穆人清", "陈圆圆", "郭芙", "郭襄", "穆念慈", "东方不败", "梅超风", "林平之", "林远图", "灭绝师太", "段誉", "鸠摩智"};
        contactList = (RecyclerView) findViewById(R.id.contact_list);
        letterView = (LetterView) findViewById(R.id.letter_view);
        layoutManager = new LinearLayoutManager(this);
        //adapter = new ContactAdapter(this, contactNames);

        contactList.setLayoutManager(layoutManager);
        contactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        //contactList.setAdapter(adapter);

        letterView.setCharacterListener(new LetterView.CharacterClickListener() {
            @Override
            public void clickCharacter(String character) {
                layoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(character), 0);
            }

            @Override
            public void clickArrow() {
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
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
                    CacheSick.sickChoose="";
                    SickActivity.this.finish();
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

    private void sickOK(){
        this.finish();
        if(HCProtocol.ST_OpenDoor()){
            logger.info("下发开门成功");
            //sendCZY(data);
            MyTextToSpeech.getInstance().speak("开门成功");
        }else{
            logger.info("下发开门失败");
            MyTextToSpeech.getInstance().speak("下发开门失败");
        }

    }
    private void getSick(){
        String sendValue="{\"order\":\"patient\",\"data\":\""+Cache.appcode+"\",\"number\":\""+ UUID.randomUUID().toString()+"\"}";
        if(sendExternal(sendValue)){
            return;
        }
    }

    private void showSick(){
        layoutLoad.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.VISIBLE);
        contactNames= CacheSick.getSickMess();
        adapter = new ContactAdapter(this, contactNames);
        contactList.setAdapter(adapter);
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
}
