package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;

import com.st.p2018.addbook.ContactAdapter;
import com.st.p2018.addbook.DividerItemDecoration;
import com.st.p2018.addbook.LetterView;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.UUID;

public class SickActivity extends Activity {

    private RecyclerView contactList;
    private String[] contactNames;
    private LinearLayoutManager layoutManager;
    private LetterView letterView;
    private ContactAdapter adapter;
    private TextView tvtitle;

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
        getSick();

        contactNames = new String[] {"456","张三丰", "郭靖", "黄蓉", "黄老邪", "赵敏", "123", "天山童姥", "任我行", "于万亭", "陈家洛+2019-12-20+201912200001+心内一", "韦小宝", "$6", "穆人清", "陈圆圆", "郭芙", "郭襄", "穆念慈", "东方不败", "梅超风", "林平之", "林远图", "灭绝师太", "段誉", "鸠摩智"};
        contactList = (RecyclerView) findViewById(R.id.contact_list);
        letterView = (LetterView) findViewById(R.id.letter_view);
        layoutManager = new LinearLayoutManager(this);
        adapter = new ContactAdapter(this, contactNames);

        contactList.setLayoutManager(layoutManager);
        contactList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        contactList.setAdapter(adapter);

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

    private void getSick(){
        String sendValue="{\"order\":\"patient\",\"data\":\""+Cache.appcode+"\",\"number\":\""+ UUID.randomUUID().toString()+"\"}";
        if(sendExternal(sendValue)){
            return;
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
}
