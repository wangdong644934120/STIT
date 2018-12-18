package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.OperationAdapter;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.entity.RecordAdapter;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2018/12/17.
 */

public class RecordActivity extends Activity {

    private ListView listHeaders;// 表头ListView
    private ListView listResults;// //查询结果listview
    private List<HashMap<String, String>> mQueryData = new ArrayList<HashMap<String, String>>() ;
    private String type;
    private String  time;
    private Button btnClose;
    private Handler myHandler;
    CloseThread closeThread=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        Intent intent = getIntent();
        initView();
        this.setTitle("存放记录");

        initGridHeader();// 初始表头
        initQueryGrid();// 初始查询结果表格
        closeThread =new CloseThread();
        closeThread.start();
    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
        btnClose=(Button)findViewById(R.id.close);
        listHeaders = (ListView) findViewById(R.id.listHeaders);
        listResults = (ListView) findViewById(R.id.listResults);
        btnClose.setOnClickListener(new onClickListener());
        myHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("value") != null) {
                    btnClose.setText(bundle.getString("value"));
                }
            }};
    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.close:
                    closeThread.close();
                    RecordActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }
    /**
     * 初始化表头
     */
    private void initGridHeader() {
        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("pp", "品牌");
        map.put("type", "种类");
        map.put("gg", "规格");
        map.put("cz", "操作");
        map.put("wz","位置");
        data.add(map);
        RecordAdapter adapter = new RecordAdapter(this, data);
        listHeaders.setAdapter(adapter);
    }

    /**
     * 初始查询结果表格
     */
    private void initQueryGrid() {
        getdata();
        RecordAdapter adapter = new RecordAdapter(this, mQueryData);
        listResults.setAdapter(adapter);

    }

    /**
     * 从数据库查询数据
     *
     * @return
     */
    private void getdata() {
        for(int i=0;i<100;i++)
        for(ProductRecord pr : Cache.listPR){
            HashMap<String ,String> map = new HashMap<>();
            map.put("pp",pr.getPp().toString());
            map.put("type",pr.getType().toString());
            map.put("gg",pr.getGg().toString());
            map.put("cz",pr.getCz().toString());
//            map.put("wz",pr.getWz());
            map.put("wz",String.valueOf(i));
            mQueryData.add(map);
        }

    }


    class CloseThread extends Thread{
        int i=10;
        public void run(){
            if(mQueryData.size()==0){
                i=5;
            }else if(mQueryData.size()<=5){
                i=10;
            }else{
                i=mQueryData.size()*2;
            }
            for(;i>0;i--){
                Message message = Message.obtain(myHandler);
                Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                data.putString("value","关闭("+i+"s)");
                message.setData(data);
                System.out.println(i);
                myHandler.sendMessage(message);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                }
            }
            RecordActivity.this.finish();
        }

        public void close(){
            i=0;
        }
    }
}
