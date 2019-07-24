package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.bg.ICellBackgroundFormat;
import com.bin.david.form.data.format.selected.BaseSelectFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
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

import java.util.ArrayList;
import java.util.List;
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
    private Button btnZQ;
    private Button btnYW;
    private Handler myHandler;
    private boolean isSend=false;//是否已经发送（正确倒计时最后一秒点击有误时，可能会发送一次有误和一次成功消息）
    private Logger logger = Logger.getLogger(this.getClass());
    private List<Product> listSelectSave=new ArrayList<Product>();
    private List<Product> listSelectOut=new ArrayList<Product>();
    private List<Product> listRecordSave=new ArrayList<Product>();
    private List<Product> listRecordOut=new ArrayList<Product>();
    private  Button btnSaveSC;
    private Button btnSaveHF;
    private Button btnOutSC;
    private Button btnOutHF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        if(Cache.chooseSick.equals("1")){
            setContentView(R.layout.activity_access_con_sick);
        }else{
            setContentView(R.layout.activity_access_con_nosick);
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();

    }

    private void initView(){
        try{
            tvfh=(TextView)findViewById(R.id.fh);
            tvfh.setOnClickListener(new onClickListener());
            tvtitle=(TextView)findViewById(R.id.title);
            tvtitle.setText("存取确认");
            tvSick=(TextView)findViewById(R.id.sickname);
            tvSaveCount=(TextView)findViewById(R.id.savecount);
            tvOutCount=(TextView)findViewById(R.id.outcount);
            btnZQ=(Button)findViewById(R.id.btnzq);
            btnZQ.setEnabled(false);
            btnZQ.setOnClickListener(new onClickListener());
            btnYW=(Button)findViewById(R.id.btnyw);
            btnYW.setEnabled(false);
            btnYW.setOnClickListener(new onClickListener());
            btnSaveSC=(Button)findViewById(R.id.btnsaveSC);
            btnSaveSC.setOnClickListener(new onClickListener());
            btnSaveHF=(Button)findViewById(R.id.btnsaveHF);
            btnSaveHF.setOnClickListener(new onClickListener());
            btnOutSC=(Button)findViewById(R.id.btnOutSC);
            btnOutSC.setOnClickListener(new onClickListener());
            btnOutHF=(Button)findViewById(R.id.btnOutHF);
            btnOutHF.setOnClickListener(new onClickListener());
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
                        btnZQ.setEnabled(true);
                        btnYW.setEnabled(true);
                        tvSaveCount.setText("共存放"+Cache.listOperaSave.size()+"个");
                        tvOutCount.setText("共取出"+Cache.listOperaOut.size()+"个");
                        initSave();
                        initOut();
                        layoutLoad.setVisibility(View.GONE);
                        linearLayout.setVisibility(View.VISIBLE);

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
    private void initSave() {
        try {

            tableSave = findViewById(R.id.tablesave);
            Column<String> column1 = new Column<>("品牌", "pp");
            column1.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateSaveUI(i);
                }
            });
            Column<String> column2 = new Column<>("名称", "mc");
            column2.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateSaveUI(i);
                }
            });
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            column3.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateSaveUI(i);
                }
            });
            Column<String> column5 = new Column<>("所在位置", "location");
            column5.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateSaveUI(i);
                }
            });

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("", Cache.listOperaSave, column1, column2, column3, column5);
            //设置数据

            tableSave.setTableData(tableData);
            tableSave.getConfig().setShowXSequence(false);
            tableSave.getConfig().setShowYSequence(false);
            tableSave.getConfig().setShowTableTitle(false);
            tableSave.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            tableSave.getConfig().setColumnTitleStyle(new FontStyle(20, Color.WHITE));
            tableSave.getConfig().setContentStyle(new FontStyle(18, Color.BLACK));
            tableSave.getConfig().setContentBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {
                    if (listSelectSave.contains(Cache.listOperaSave.get(cellInfo.position))) {
                        return ContextCompat.getColor(AccessConActivity.this, R.color.jxqyellow);
                    } else {
                        return TableConfig.INVALID_COLOR;
                    }
                }
            });

        } catch (Exception e) {
            logger.error("初始化耗材存放出错",e);
        }
    }

    private void updateSaveUI(int i){
        try{
            if(listSelectSave.contains(Cache.listOperaSave.get(i))){
                listSelectSave.remove(Cache.listOperaSave.get(i));
            }else{
                listSelectSave.add(Cache.listOperaSave.get(i));
            }
            tableSave.refreshDrawableState();
            tableSave.invalidate();
        }catch (Exception e){
            logger.error("更新存放缓存出错",e);
        }

    }

    private void updateOutUI(int i){
        try{
            if(listSelectOut.contains(Cache.listOperaOut.get(i))){
                listSelectOut.remove(Cache.listOperaOut.get(i));
            }else{
                listSelectOut.add(Cache.listOperaOut.get(i));
            }
            tableOut.refreshDrawableState();
            tableOut.invalidate();
        }catch (Exception e){
            logger.error("更新取出缓存出错",e);
        }

    }
    /**
     * 初始化取操作耗材
     */
    private void initOut(){
        try{
            Column<String> column1 = new Column<>("品牌", "pp");
            column1.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateOutUI(i);
                }
            });
            Column<String> column2 = new Column<>("名称", "mc");
            column2.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateOutUI(i);
                }
            });
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            column3.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateOutUI(i);
                }
            });
            //Column<String> column4 = new Column<>("剩余天数", "yxrq");
            Column<String> column5 = new Column<>("所在位置", "location");
            column5.setOnColumnItemClickListener(new OnColumnItemClickListener<String>() {
                @Override
                public void onClick(Column<String> column, String s, String s2, int i) {
                    updateOutUI(i);
                }
            });

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
            tableOut.getConfig().setContentBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {
                    if (listSelectOut.contains(Cache.listOperaOut.get(cellInfo.position))) {
                        return ContextCompat.getColor(AccessConActivity.this, R.color.jxqyellow);
                    } else {
                        return TableConfig.INVALID_COLOR;
                    }
                }
            });
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
                    CacheSick.sickChoose="";
                    Message message = Message.obtain(Cache.myHandle);
                    Bundle bund = new Bundle();
                    bund.putString("sickgg","4");
                    message.setData(bund);
                    Cache.myHandle.sendMessage(message);
                    btnZQ.setPressed(false);
                    break;
                case R.id.btnyw:
                    btnYW.setPressed(true);
                    //关闭界面
                    Cache.myHandleAccess=null;
                    AccessConActivity.this.finish();
                    if(Cache.lockScreen.equals("1") && Cache.mztcgq!=1){
                        message = Message.obtain(Cache.myHandle);
                        bund = new Bundle();
                        bund.putString("ui","lock");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                    }
                    btnYW.setPressed(false);
                    break;
                case R.id.fh:
                    Cache.myHandleAccess=null;
                    AccessConActivity.this.finish();
                    if(Cache.lockScreen.equals("1") && Cache.mztcgq!=1){
                        message = Message.obtain(Cache.myHandle);
                        bund = new Bundle();
                        bund.putString("ui","lock");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                    }
                    break;
                case R.id.btnsaveSC:
                    for(Product product : listSelectSave){
                        if(Cache.listOperaSave.contains(product)){
                            Cache.listOperaSave.remove(product);
                            listRecordSave.add(product);
                        }
                    }
                    tvSaveCount.setText("共存放"+Cache.listOperaSave.size()+"个");
                    initSave();
                    break;
                case R.id.btnsaveHF:
                    for(Product product : listSelectSave){
                        if(!Cache.listOperaSave.contains(product)){
                            Cache.listOperaSave.add(product);
                            listRecordSave.remove(product);
                        }
                    }
                    tvSaveCount.setText("共存放"+Cache.listOperaSave.size()+"个");
                    initSave();

                    break;
                case R.id.btnOutSC:
                    for(Product product : listSelectOut){
                        if(Cache.listOperaOut.contains(product)){
                            Cache.listOperaOut.remove(product);
                            listRecordOut.add(product);
                        }
                    }
                    tvOutCount.setText("共取出"+Cache.listOperaOut.size()+"个");
                    initOut();
                    break;
                case R.id.btnOutHF:
                    for(Product product : listSelectOut){
                        if(!Cache.listOperaOut.contains(product)){
                            Cache.listOperaOut.add(product);
                            listRecordOut.remove(product);
                        }
                    }
                    tvOutCount.setText("共取出"+Cache.listOperaOut.size()+"个");
                    initOut();
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

        try{
            String allproduct="";
            String patient=CacheSick.getSickMessAndID().get(CacheSick.sickChoose)==null?"":CacheSick.getSickMessAndID().get(CacheSick.sickChoose);
            for(Product p : Cache.listOperaSave){
                allproduct=allproduct+"{\"epc\":\""+p.getEpc()+"\",\"location\":\""+p.getLocation()+"\",\"operation\":\"存\",\"patient\":\""+patient+"\"},";
            }
            for(Product p : Cache.listOperaOut){
                allproduct=allproduct+"{\"epc\":\""+p.getEpc()+"\",\"location\":\"" + p.getLocation() + "\",\"operation\":\"取\",\"patient\":\""+patient+"\"},";
            }
            for(Product p : listRecordSave){
                allproduct=allproduct+"{\"epc\":\""+p.getEpc()+"\",\"location\":\"" + p.getLocation() + "\",\"operation\":\"存\",\"patient\":\"\"},";
            }
            for(Product p : listRecordOut){
                allproduct=allproduct+"{\"epc\":\""+p.getEpc()+"\",\"location\":\"" + p.getLocation() + "\",\"operation\":\"取\",\"patient\":\"\"},";
            }
            if(allproduct.length()>1){
                allproduct=allproduct.substring(0,allproduct.length()-1);
            }
            String sendValue="{\"order\":\"patientproduct\",\"code\":\"" + Cache.appcode + "\",\"number\":\""+ UUID.randomUUID().toString()+"\"," +
                    "\"data\":{\"result\":\""+zqoryw+"\",\"operator\":\""+Cache.operatorCode+"\",\"product\":["+allproduct+"]}}";
            if(SocketClient.socket!=null){
                SocketClient.send(sendValue);
            }
            //关闭界面
            Cache.myHandleAccess=null;
            AccessConActivity.this.finish();
            if(Cache.lockScreen.equals("1") && Cache.mztcgq!=1){
                Message message = Message.obtain(Cache.myHandle);
                Bundle bund = new Bundle();
                bund.putString("ui","lock");
                message.setData(bund);
                Cache.myHandle.sendMessage(message);
            }

        }catch (Exception e){
            logger.error("发送耗材数据出错",e);
        }



    }

}
