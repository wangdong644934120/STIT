package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.st.p2018.entity.Product;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.bin.david.form.core.SmartTable;
import com.st.p2018.util.CacheSick;

import org.apache.log4j.Logger;

import java.util.UUID;


public class AccessConActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private SmartTable tableSave;
    private SmartTable tableOut;
    private TextView tvSick;
    private TextView tvSaveCount;
    private TextView tvOutCount;
    private ImageView ivGif;
    private LinearLayout linearLayout;
    private LinearLayout layoutLoad;
    private Button btnGG;
    private Button btnZQ;
    private Button btnYW;
    private Handler myHandler;
    CloseThread closeThread=null;
    private boolean isSend=false;//是否已经发送（正确倒计时最后一秒点击有误时，可能会发送一次有误和一次成功消息）
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_access_con);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitlenofh);
        initView();


    }

    private void initView(){
        try{
            tvtitle=(TextView)findViewById(R.id.title);
            tvtitle.setText("存取确认");
            btnGG=(Button)findViewById(R.id.btngg);
            btnGG.setOnClickListener(new onClickListener());
            tvSick=(TextView)findViewById(R.id.sickname);
            tvSaveCount=(TextView)findViewById(R.id.savecount);
            tvOutCount=(TextView)findViewById(R.id.outcount);
            btnZQ=(Button)findViewById(R.id.btnzq);
            btnZQ.setOnClickListener(new onClickListener());
            btnYW=(Button)findViewById(R.id.btnyw);
            btnYW.setOnClickListener(new onClickListener());
            ivGif=(ImageView)findViewById(R.id.imageview1);
            linearLayout=(LinearLayout)findViewById(R.id.linnerLayouttxl);
            layoutLoad=(LinearLayout)findViewById(R.id.loadaccesstxl);
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(this).load(R.drawable.loadtongyong).apply(options).into(ivGif);

            tvSick.setText(CacheSick.sickChoose);
            Cache.myHandleAccess= new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                    //点击更改按钮后显示选择的患者信息
                    if (bundle.getString("sickgg") != null) {
                        tvSick.setText(CacheSick.sickChoose);
                    }
                    //显示耗材存取情况信息
                    if(bundle.getString("show")!=null){
                        tvSaveCount.setText("共存放"+Cache.listOperaSave.size()+"个");
                        tvOutCount.setText("共取出"+Cache.listOperaOut.size()+"个");
                        initSave();
                        initOut();
                        layoutLoad.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);
                        closeThread =new CloseThread();
                        closeThread.start();
                    }


                }
            };
            myHandler= new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                    //提示信息
                    if (bundle.getString("value") != null) {
                        btnZQ.setText(bundle.getString("value"));
                    }
                }};
        }catch (Exception e){
            logger.error("初始化view出错",e);
        }
    }

    /**
     * 初始化存操作耗材
     */
    private void initSave(){
        try{
            Column<String> column1 = new Column<>("品牌", "pp");
            Column<String> column2 = new Column<>("名称", "mc");
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            //Column<String> column4 = new Column<>("剩余天数", "yxrq");
            Column<String> column5 = new Column<>("所在位置", "szwz");

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("", Cache.listOperaSave, column1, column2, column3,  column5);
            //设置数据
            tableSave = findViewById(R.id.tablesave);
            tableSave.setTableData(tableData);
            tableSave.getConfig().setShowXSequence(false);
            tableSave.getConfig().setShowYSequence(false);
            tableSave.getConfig().setShowTableTitle(false);
            tableSave.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            tableSave.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
            tableSave.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        }catch (Exception e){
            logger.error("初始化存操作耗材数据出错",e);
        }

    }

    /**
     * 初始化取操作耗材
     */
    private void initOut(){
        try{
            Column<String> column1 = new Column<>("品牌", "pp");
            Column<String> column2 = new Column<>("名称", "mc");
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            //Column<String> column4 = new Column<>("剩余天数", "yxrq");
            Column<String> column5 = new Column<>("所在位置", "szwz");

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("", Cache.listOperaOut, column1, column2, column3,  column5);
            //设置数据
            tableOut = findViewById(R.id.tableout);
            tableOut.setTableData(tableData);
            tableOut.getConfig().setShowXSequence(false);
            tableOut.getConfig().setShowYSequence(false);
            tableOut.getConfig().setShowTableTitle(false);
            tableOut.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            tableOut.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
            tableOut.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        }catch (Exception e){
            logger.error("初始化取操作耗材数据出错",e);
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
                case R.id.btnzq:
                    btnZQ.setPressed(true);
                    sendQR("0");
                    btnZQ.setPressed(false);
                    break;
                case R.id.btnyw:
                    btnYW.setPressed(true);
                    sendQR("-1");
                    btnYW.setPressed(false);
                    break;
                case R.id.btngg:
                    Intent intent = new Intent(AccessConActivity.this, SickActivity.class);
                    intent.putExtra("sickgg",true);
                    startActivity(intent);
                    break;
                case R.id.fh:
                    AccessConActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 发送正确或有误数据
     * @param zqoryw
     */
    private void sendQR(String zqoryw){
        if(isSend){
            return;
        }else{
            isSend=true;
        }
        closeThread.close();
        try{
            String allproduct="";
            for(Product p : Cache.listOperaSave){
                allproduct=allproduct+"{\"epc\":\""+p.getEpc()+"\",\"location\":\""+p.getSzwz()+"\",\"operation\":\"存\"},";
            }
            for(Product p : Cache.listOperaOut){
                allproduct=allproduct+"{\"epc\":\""+p.getEpc()+"\",\"location\":\"" + p.getSzwz() + "\",\"operation\":\"取\"},";
            }
            if(allproduct.length()>1){
                allproduct=allproduct.substring(0,allproduct.length()-1);
            }
            String sendValue="{\"order\":\"patientproduct\",\"code\":\"" + Cache.appcode + "\",\"number\":\""+ UUID.randomUUID().toString()+"\"," +
                    "\"data\":{\"result\":\""+zqoryw+"\",\"patient\":\""+CacheSick.getSickMessAndID().get(CacheSick.sickChoose)+"\"," +
                    "\"operator\":\""+Cache.operatorCode+"\",\"product\":["+allproduct+"]}}";
            if(SocketClient.socket!=null){
                SocketClient.send(sendValue);
            }

        }catch (Exception e){
            logger.error("发送耗材数据出错",e);
        }



    }

    class CloseThread extends Thread{
        int i=10;
        public void run(){
            if((Cache.listOperaSave.size()+Cache.listOperaOut.size())==0){
                i=10;
            }else if((Cache.listOperaSave.size()+Cache.listOperaOut.size())<=10){
                i=60;
            }else{
                i=180;
            }
            for(;i>0;i--){
                Message message = Message.obtain(myHandler);
                Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                data.putString("value","正确("+i+"s)");
                message.setData(data);
                myHandler.sendMessage(message);

                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                }
            }
            sendQR("0");
            AccessConActivity.this.finish();
            if(Cache.lockScreen.equals("1") && Cache.mztcgq!=1){
                Message message = Message.obtain(Cache.myHandle);
                Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                bund.putString("ui","lock");
                message.setData(bund);
                Cache.myHandle.sendMessage(message);
            }
        }

        public void close(){
            i=0;
        }
    }


}
