package com.st.p2018.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.entity.Product;
import com.st.p2018.external.SocketClient;
import com.st.p2018.externalentity.TotalMessage;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.CacheSick;

import com.st.p2018.util.MySpeechUtil;
import com.st.p2018.util.MyTextToSpeech;


import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {

    private BarChart barChart;
    private Button tvD;
    private RelativeLayout rl;
    private ImageView ivh1;
    private ImageView ivh2;
    private ImageView ivh3;
    private ImageView ivh4;
    private ImageView ivh5;
    private ImageView ivh6;
    private ImageView ivmen;
    private ImageView ivdeng;
    private TextView tvczy;
    private TextView tvczsc;
    private TextView tvmzt;
    private TextView tvdeng;
    private TextView tvSickMessage;
    private TextView tvappTitle;
    private TextView tvjxq;
    private TextView tvzkc;
    private Button btnsickxuanze;
    private Button btnKD;
    private Button btnPD;

    private CZSCShow czscShow=null;
    private boolean czscflag=true;
    private Logger logger =Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用自定义标题栏
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        //String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if(!Cache.external){
            //如果没有连接第三方平台，无法启用锁屏
            Cache.chooseSick="0";
            Cache.lockScreen="0";
        }
        initView();
        initSpeechPlug();
        Cache.myContext = this;
        new DeviceCom().start();
        if(Cache.lockScreen.equals("1")){
            logger.info("配置了锁屏");
        }
        if(!Cache.external){
            initJXQData();
        }

    }
    //界面显示
    private void initView() {
        try{
            if(Cache.chooseSick.equals("1")){
                LinearLayout linner=(LinearLayout)findViewById(R.id.linnerlayoutsick);
                linner.setVisibility(View.VISIBLE);
                LinearLayout linnerZT=(LinearLayout)findViewById(R.id.linnerlayoutsszt);
                linnerZT.setVisibility(View.GONE);
            }
            tvappTitle=(TextView)findViewById(R.id.apptitle);
            btnKD=(Button)findViewById(R.id.kaideng);
            btnKD.setOnClickListener(new onClickListener());
            btnPD=(Button)findViewById(R.id.pandian);
            btnPD.setOnClickListener(new onClickListener());
            rl=(RelativeLayout)findViewById(R.id.mylayout);
            tvD=(Button)findViewById(R.id.dian);
            tvD.setOnClickListener(new onClickListener());
            barChart = (BarChart) findViewById(R.id.barchart);

            btnsickxuanze=(Button)findViewById(R.id.sickxuanze);
            btnsickxuanze.setOnClickListener(new onClickListener());
            tvSickMessage=(TextView)findViewById(R.id.sickmessage);
            tvSickMessage.setOnClickListener(new onClickListener());
            tvjxq=(TextView)findViewById(R.id.tvjxq);
            tvzkc=(TextView)findViewById(R.id.tvzkc);
            initBarChart();
            initHandler();
            initGXQT();
            tvSickMessage.setText(CacheSick.sickChoose);

        }catch (Exception e){
            logger.error("显示view出错",e);
        }

    }

    /**
     * 初始TTS引擎
     */
    private void initSpeechPlug() {
        try {
            if (!MySpeechUtil.checkSpeechServiceInstall(MainActivity.this)) {
                MySpeechUtil.processInstall(MainActivity.this,
                        "SpeechService.apk");
            }
            MyTextToSpeech.getInstance().initial(MainActivity.this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initHandler(){
        try{
            Cache.myHandle = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                    try{
                        //菜单栏
                        if(bundle.getString("ui")!=null){

                            if(bundle.getString("ui").toString().equals("ry")){
                                Intent intent = new Intent(MainActivity.this, PersonActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("hc")){
                                Intent intent = new Intent(MainActivity.this, HCActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("kz")){
                                Intent intent = new Intent(MainActivity.this, KZActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("pz")){
                                Intent intent = new Intent(MainActivity.this, PZActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("sbxx")){
                                Intent intent = new Intent(MainActivity.this, DeviceActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("sick")){
                                //登录验证成功打开患者选择界面
                                Intent intent = new Intent(MainActivity.this, SickActivity.class);
                                intent.putExtra("sickgg","1");
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("access")){
                                Intent intent = new Intent(MainActivity.this, AccessConActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("accesslocal")){
                                Intent intent = new Intent(MainActivity.this, AccessConLocalActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("lock")){
                                Intent intent = new Intent(MainActivity.this, LockActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("pd")){
                                Intent intent = new Intent(MainActivity.this, PDActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("cd")){
                                SelectDialog selectDialog = new SelectDialog(MainActivity.this,R.style.dialog);//创建Dialog并设置样式主题
                                Window win = selectDialog.getWindow();
                                WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                                params.x = 220;//设置x坐标
                                params.y = -315;//设置y坐标
                                win.setAttributes(params);
                                selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                                selectDialog.show();
                            }
                            if(bundle.getString("ui").toString().equals("tccx")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setIcon(android.R.drawable.ic_dialog_info);
                                builder.setTitle("提示");
                                builder.setMessage("您确定要退出程序吗？");
                                builder.setCancelable(true);

                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showBar();
                                        System.exit(0);
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                builder.create().show();
                            }

                            if(bundle.getString("ui").toString().equals("connectfail")){
                                Toast.makeText(MainActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                            }
                            if(bundle.getString("ui").toString().equals("productsearch")){
                                Intent intent = new Intent(MainActivity.this, ProductSearchActivity.class);
                                startActivity(intent);
                            }
                        }
                        //缩略图更新
                        if(bundle.getString("type")!=null){

                            if(bundle.get("type").toString().equals("men")){
                                if(bundle.get("zt").toString().equals("1")){
                                    //
                                    if(czscShow==null){
                                        czscflag=true;
                                        czscShow=new CZSCShow();
                                        czscShow.start();
                                    }
                                    // /替换开门图片
                                    ivmen.setImageResource(R.drawable.menkai);
                                    tvmzt.setText("门状态：门已开");
                                }else{
                                    //替换关门图片
                                    tvczy.setText("操作员：");
                                    tvczsc.setText("操作时长：00:00");
                                    czscflag=false;
                                    ivmen.setImageResource(R.drawable.menguan);
                                    tvmzt.setText("门状态：门已关");
                                }
                            }
                            if(bundle.get("type").toString().equals("deng")){
                                if(bundle.get("zt").toString().equals("1")){
                                    //替换开灯图片
                                    ivdeng.setImageResource(R.drawable.dengkai);
                                    btnKD.setBackgroundResource(R.drawable.guandeng);
                                    tvdeng.setText("灯状态：灯已开");
                                }else{
                                    //替换关灯图片
                                    ivdeng.setImageResource(R.drawable.dengguan);
                                    btnKD.setBackgroundResource(R.drawable.kaideng);
                                    tvdeng.setText("灯状态：灯已关");

                                }
                            }
                            if(bundle.get("type").toString().equals("hwxc")){
                                if(bundle.get("wz").toString().equals("1")){
                                    if(bundle.get("zt").toString().equals("1")){
                                        //替换红外行程1触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh1.setImageResource(R.drawable.hongwaichufa1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh1.setImageResource(R.drawable.hongwaichufa2);
                                        }

//                                ivh1.setVisibility(View.VISIBLE);
                                    }else{
                                        //替换红外行程1不触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh1.setImageResource(R.drawable.hongwaizhengchang1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh1.setImageResource(R.drawable.hongwaizhengchang2);
                                        }
                                    }

                                }
                                if(bundle.get("wz").toString().equals("2")){
                                    if(bundle.get("zt").toString().equals("1")){
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh2.setImageResource(R.drawable.hongwaichufa1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh2.setImageResource(R.drawable.hongwaichufa2);
                                        }
                                        //替换红外行程1触发图片
                                    }else{
                                        //替换红外行程1不触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh2.setImageResource(R.drawable.hongwaizhengchang1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh2.setImageResource(R.drawable.hongwaizhengchang2);
                                        }
                                    }

                                }
                                if(bundle.get("wz").toString().equals("3")){
                                    if(bundle.get("zt").toString().equals("1")){
                                        //替换红外行程1触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh3.setImageResource(R.drawable.hongwaichufa1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh3.setImageResource(R.drawable.hongwaichufa2);
                                        }
                                    }else{
                                        //替换红外行程1不触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh3.setImageResource(R.drawable.hongwaizhengchang1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh3.setImageResource(R.drawable.hongwaizhengchang2);
                                        }
                                    }
                                }
                                if(bundle.get("wz").toString().equals("4")){
                                    if(bundle.get("zt").toString().equals("1")){
                                        //替换红外行程1触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh4.setImageResource(R.drawable.hongwaichufa1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh4.setImageResource(R.drawable.hongwaichufa2);
                                        }
                                    }else{
                                        //替换红外行程1不触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh4.setImageResource(R.drawable.hongwaizhengchang1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh4.setImageResource(R.drawable.hongwaizhengchang2);
                                        }
                                    }
                                }
                                if(bundle.get("wz").toString().equals("5")){
                                    if(bundle.get("zt").toString().equals("1")){
                                        //替换红外行程1触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh5.setImageResource(R.drawable.hongwaichufa1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh5.setImageResource(R.drawable.hongwaichufa2);
                                        }
                                    }else{
                                        //替换红外行程1不触发图片
                                        if(Cache.gx.equals("Ⅰ型")){
                                            ivh5.setImageResource(R.drawable.hongwaizhengchang1);
                                        }else if(Cache.gx.equals("Ⅱ型")){
                                            ivh5.setImageResource(R.drawable.hongwaizhengchang2);
                                        }
                                    }

                                }
                                if(bundle.get("wz").toString().equals("6")){
                                    if(!Cache.gx.equals("Ⅰ型")){
                                        if(bundle.get("zt").toString().equals("1")){
                                            //替换红外行程1触发图片
                                            if(Cache.gx.equals("Ⅰ型")){
                                                ivh6.setImageResource(R.drawable.hongwaichufa1);
                                            }else if(Cache.gx.equals("Ⅱ型")){
                                                ivh6.setImageResource(R.drawable.hongwaichufa2);
                                            }
                                        }else{
                                            //替换红外行程1不触发图片
                                            if(Cache.gx.equals("Ⅰ型")){
                                                ivh6.setImageResource(R.drawable.hongwaizhengchang1);
                                            }else if(Cache.gx.equals("Ⅱ型")){
                                                ivh6.setImageResource(R.drawable.hongwaizhengchang2);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(bundle.getString("pd")!=null){
                            String value=bundle.getString("pd");
                            if(value.equals("openpd")){
                                Intent intent = new Intent(MainActivity.this, ProgressDialog.class);
                                startActivity(intent);
                            }
                        }
                        if(bundle.getString("initJXQ")!=null){
                            initJXQData();
                            //barChart.animateY(500, Easing.EasingOption.EaseInCirc);
                        }
                        if(bundle.getString("initJXQExternal")!=null){
                            //setData(new HashMap<String, String>());
                            setDataBarChart();
                            barChart.animateY(500, Easing.EasingOption.EaseInCirc);
                        }
                        if(bundle.getString("record")!=null){
                            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                            startActivity(intent);
                        }
                        if(bundle.getString("gx")!=null){
                            //根据柜型更新缩略图
                            if(Cache.gx.equals("Ⅰ型")){
                                //I型柜
                                initGX1();
                            }else if(Cache.gx.equals("Ⅱ型")){
                                initGX2();
                            }else{
                                initNO();
                            }
                        }
                        if(bundle.getString("czy")!=null){
                            //根据柜型更新缩略图
                            tvczy.setText("操作员："+bundle.getString("czy"));

                        }
                        if(bundle.getString("czsc")!=null){
                            //更新操作时长
                            tvczsc.setText("操作时长："+bundle.getString("czsc"));
                        }
                        if(bundle.getString("pdzjm")!=null){
                            //显示盘点结果
                            Intent intent = new Intent(MainActivity.this, PDActivity.class);
                            startActivity(intent);
                        }
                        if(bundle.getString("appname")!=null){
                            tvappTitle.setText(bundle.getString("appname").toString());
                            /*barChart.setCenterText(generateCenterSpannableText(Cache.appcode));
                            initJXQData();*/
                        }
                        if(bundle.getString("sickgg")!=null){
                            tvSickMessage.setText(CacheSick.sickChoose);
                        }
                    }catch(Exception ex){
                        logger.error("handler内显示出错",ex);
                    }

                }
            };
        }catch (Exception e){
            logger.error("初始化handler出错",e);
        }

    }
    //显示Toast函数
    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 单击事件监听
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try{

            }catch (Exception e){
                logger.error("",e);
            }
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.dian:
                    if(Cache.isAdmin.equals("0")){
                        //普通用户登录,需要进行权限验证
                        Intent intent = new Intent(MainActivity.this, PassActivity.class);
                        startActivity(intent);
                        return;
                    }
                    SelectDialog selectDialog = new SelectDialog(MainActivity.this,R.style.dialog);//创建Dialog并设置样式主题
                    Window win = selectDialog.getWindow();
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.x = 220;//设置x坐标
                    params.y = -315;//设置y坐标
                    win.setAttributes(params);
                    selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                    selectDialog.show();
                    break;
                case R.id.kaideng:
                    logger.info("点击开灯");
                    if(Cache.zmdzt){
                        boolean bl=HCProtocol.ST_CloseLight();
                        if(bl){
                            MyTextToSpeech.getInstance().speak("灯已关");
                            Toast.makeText(MainActivity.this, "灯已关", Toast.LENGTH_SHORT).show();
                        }else{
                            MyTextToSpeech.getInstance().speak("关灯失败");
                            Toast.makeText(MainActivity.this, "关灯失败", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        String app =getResources().getText(R.string.app_name).toString();
                        boolean bl=HCProtocol.ST_OpenLight();
                        if(bl){
                            MyTextToSpeech.getInstance().speak("灯已开");
                            Toast.makeText(MainActivity.this, "灯已开", Toast.LENGTH_SHORT).show();
                        }else{
                            MyTextToSpeech.getInstance().speak("开灯失败");
                            Toast.makeText(MainActivity.this, "开灯失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case R.id.pandian:
                    Cache.getHCCS=2;
                    if(HCProtocol.ST_GetAllCard()){
                    }else{
                        MyTextToSpeech.getInstance().speak("盘点失败");
                        Toast.makeText(MainActivity.this, "盘点失败", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.sickxuanze:
                    Intent intent = new Intent(MainActivity.this, SickActivity.class);
                    intent.putExtra("sickgg","2");
                    startActivity(intent);
                    break;
                case R.id.sickmessage:
                    if(tvSickMessage.getText().equals("")){
                        return;
                    }
                    String patient=CacheSick.getSickMessAndID().get(CacheSick.sickChoose)==null?"":CacheSick.getSickMessAndID().get(CacheSick.sickChoose);
                    String send="{\"order\":\"productsearch\",\"number\":\""+UUID.randomUUID().toString()+"\",\"data\":\""+patient+"\"}";
                    SocketClient.send(send);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 初始化图表控件
     */
    private void initBarChart(){
        barChart.setDrawBarShadow(false);//true绘画的Bar有阴影。
        barChart.setDrawValueAboveBar(true);//true文字绘画在bar上
        barChart.getDescription().setEnabled(false);
        barChart.setMaxVisibleValueCount(60);
        barChart.setPinchZoom(false);//false只能单轴缩放
        barChart.setDrawGridBackground(false);
        barChart.setScaleEnabled(false);
        IAxisValueFormatter xAxisFormatter = new MyAxisValueFormatter();
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setTextSize(20f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setTextSize(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
//        rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setTextSize(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(15f);
        l.setTextSize(20f);
        l.setXEntrySpace(8f);
        l.setEnabled(true);
        barChart.setOnChartValueSelectedListener(new BarCharLinster());

        setEmptyData();
    }

    /**
     * 显示图标数据
     */
    private void setDataBarChart() {
        if(!Cache.gcqy1){
            try{
                for(int i=1;i<15;i++){
                    if(Cache.gcqy1){
                        break;
                    }else{
                        Thread.sleep(500);
                    }
                }
            }catch (Exception e){

            }

        }
        try{
            int kc=0;
            ArrayList<BarEntry> yValskc = new ArrayList<BarEntry>();
            if(Cache.mapTotal.get("1")!=null){
                kc=kc+Cache.mapTotal.get("1").getJxq().size()+Cache.mapTotal.get("1").getQt().size();
                yValskc.add(new BarEntry(1, Cache.mapTotal.get("1").getJxq().size()+Cache.mapTotal.get("1").getQt().size()));
            }else{
                if(Cache.gcqy1){
                    yValskc.add(new BarEntry(1, 0));
                }
            }
            if(Cache.mapTotal.get("2")!=null){
                kc=kc+Cache.mapTotal.get("2").getJxq().size()+Cache.mapTotal.get("2").getQt().size();
                yValskc.add(new BarEntry(2, Cache.mapTotal.get("2").getJxq().size()+Cache.mapTotal.get("2").getQt().size()));
            }else{
                if(Cache.gcqy2){
                    yValskc.add(new BarEntry(2, 0));
                }
            }
            if(Cache.mapTotal.get("3")!=null){
                kc=kc+Cache.mapTotal.get("3").getJxq().size()+Cache.mapTotal.get("3").getQt().size();
                yValskc.add(new BarEntry(3, Cache.mapTotal.get("3").getJxq().size()+Cache.mapTotal.get("3").getQt().size()));
            }else{
                if(Cache.gcqy3){
                    yValskc.add(new BarEntry(3, 0));
                }
            }
            if(Cache.mapTotal.get("4")!=null){
                kc=kc+Cache.mapTotal.get("4").getJxq().size()+Cache.mapTotal.get("4").getQt().size();
                yValskc.add(new BarEntry(4, Cache.mapTotal.get("4").getJxq().size()+Cache.mapTotal.get("4").getQt().size()));
            }else{
                if(Cache.gcqy4){
                    yValskc.add(new BarEntry(4, 0));
                }
            }
            if(Cache.mapTotal.get("5")!=null){
                kc=kc+Cache.mapTotal.get("5").getJxq().size()+Cache.mapTotal.get("5").getQt().size();
                yValskc.add(new BarEntry(5, Cache.mapTotal.get("5").getJxq().size()+Cache.mapTotal.get("5").getQt().size()));
            }else{
                if(Cache.gcqy5){
                    yValskc.add(new BarEntry(5, 0));
                }
            }
            if(Cache.mapTotal.get("6")!=null){
                kc=kc+Cache.mapTotal.get("6").getJxq().size()+Cache.mapTotal.get("6").getQt().size();
                yValskc.add(new BarEntry(6, Cache.mapTotal.get("6").getJxq().size()+Cache.mapTotal.get("6").getQt().size()));
            }else{
                if(Cache.gcqy6){
                    yValskc.add(new BarEntry(6, 0));
                }
            }


            BarDataSet set1;
            set1 = new BarDataSet(yValskc, "库存");
            set1.setDrawIcons(false);
            set1.setColor(Color.rgb(0x08,0x76,0x28));
            set1.setValueTextColor(Color.rgb(0x08,0x76,0x28));
            set1.setValueFormatter(new MyBValueFormatter());

            ArrayList<BarEntry> yValsjxq = new ArrayList<BarEntry>();

            int jxq=0;
            if(Cache.mapTotal.get("1")!=null){
                jxq=jxq+Cache.mapTotal.get("1").getJxq().size();
                yValsjxq.add(new BarEntry(1, Cache.mapTotal.get("1").getJxq().size()));
            }else{
                if(Cache.gcqy1){
                    yValsjxq.add(new BarEntry(1, 0));
                }
            }
            if(Cache.mapTotal.get("2")!=null){
                jxq=jxq+Cache.mapTotal.get("2").getJxq().size();
                yValsjxq.add(new BarEntry(2, Cache.mapTotal.get("2").getJxq().size()));
            }
            else{
                if(Cache.gcqy2){
                    yValsjxq.add(new BarEntry(2, 0));
                }
            }
            if(Cache.mapTotal.get("3")!=null){
                jxq=jxq+Cache.mapTotal.get("3").getJxq().size();
                yValsjxq.add(new BarEntry(3, Cache.mapTotal.get("3").getJxq().size()));
            }else{
                if(Cache.gcqy3){
                    yValsjxq.add(new BarEntry(3, 0));
                }
            }
            if(Cache.mapTotal.get("4")!=null){
                jxq=jxq+Cache.mapTotal.get("4").getJxq().size();
                yValsjxq.add(new BarEntry(4, Cache.mapTotal.get("4").getJxq().size()));
            }else{
                if(Cache.gcqy4){
                    yValsjxq.add(new BarEntry(4, 0));
                }
            }
            if(Cache.mapTotal.get("5")!=null){
                jxq=jxq+Cache.mapTotal.get("5").getJxq().size();
                yValsjxq.add(new BarEntry(5, Cache.mapTotal.get("5").getJxq().size()));
            }else{
                if(Cache.gcqy5){
                    yValsjxq.add(new BarEntry(5, 0));
                }
            }
            if(Cache.mapTotal.get("6")!=null){
                jxq=jxq+Cache.mapTotal.get("6").getJxq().size();
                yValsjxq.add(new BarEntry(6, Cache.mapTotal.get("6").getJxq().size()));
            }else{
                if(Cache.gcqy6){
                    yValsjxq.add(new BarEntry(6, 0));
                }
            }


            BarDataSet set2;
            set2 = new BarDataSet(yValsjxq, "近效期");
            set2.setDrawIcons(false);
            set2.setColor(Color.rgb(0XDE, 0xb2, 0x00));
            set2.setValueTextColor(Color.rgb(0XDE, 0xb2, 0x00));
            set2.setValueFormatter(new MyBValueFormatter());

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(30f);
            data.setBarWidth(0.5f);

            barChart.setData(data);
            tvjxq.setText(String.valueOf(jxq));
            tvzkc.setText(String.valueOf(kc));
        }catch (Exception e){
            logger.error("显示图表数据出错",e);
        }

    }

    /**
     * 设置图标默认空
     */
    private void setEmptyData(){
        ArrayList<BarEntry> yValskc = new ArrayList<BarEntry>();
        yValskc.add(new BarEntry(1,0));
        yValskc.add(new BarEntry(2,0));
        yValskc.add(new BarEntry(3,0));
        yValskc.add(new BarEntry(4,0));
        yValskc.add(new BarEntry(5,0));
        if(!Cache.gx.equals("Ⅰ型")){
            yValskc.add(new BarEntry(6,0));
        }

        BarDataSet set1;
        set1 = new BarDataSet(yValskc, "库存");
        set1.setDrawIcons(false);
        set1.setColor(Color.rgb(0x08,0x76,0x28));
        set1.setValueTextColor(Color.rgb(0x08,0x76,0x28));
        set1.setValueFormatter(new MyBValueFormatter());

        BarDataSet set2;
        set2 = new BarDataSet(yValskc, "近效期");
        set2.setDrawIcons(false);
        set2.setColor(Color.rgb(0XDE, 0xb2, 0x00));
        set2.setValueTextColor(Color.rgb(0XDE, 0xb2, 0x00));
        set2.setValueFormatter(new MyBValueFormatter());

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        dataSets.add(set2);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(30f);
        data.setBarWidth(0.5f);

        barChart.setData(data);
    }

    //显示柜型中其他内容
    private void initGXQT(){
        try{
            rl.removeAllViews();
            RelativeLayout.LayoutParams params ;
            //操作员图片
            params = new RelativeLayout.LayoutParams(82, 82);
            params.setMargins(40, 50, 0, 0);
            ImageView iv = new ImageView(this);
            iv.setImageResource(R.drawable.caozuoyuan);
            iv.setLayoutParams(params);
            rl.addView(iv);

            params = new RelativeLayout.LayoutParams(150, 82);
            params.setMargins(30, 150, 0, 0);
            tvczy = new TextView(this);
            tvczy.setText("操作员：");
            tvczy.setTextColor(Color.WHITE);
            tvczy.setTextSize(18);
            tvczy.setLayoutParams(params);
            rl.addView(tvczy);

            params = new RelativeLayout.LayoutParams(82, 82);
            params.setMargins(40, 200, 0, 0);
            ivmen = new ImageView(this);
            ivmen.setImageResource(R.drawable.menguan);
            ivmen.setLayoutParams(params);
            rl.addView(ivmen);

            params = new RelativeLayout.LayoutParams(150, 82);
            params.setMargins(30, 300, 0, 0);
            tvmzt = new TextView(this);
//        tvmzt.setText("");
            tvmzt.setText("门状态：…");
            tvmzt.setTextColor(Color.WHITE);
            tvmzt.setTextSize(18);
            tvmzt.setLayoutParams(params);
            rl.addView(tvmzt);

            params = new RelativeLayout.LayoutParams(82, 82);
            params.setMargins(220, 50, 0, 0);
            ImageView ivsc = new ImageView(this);
            ivsc.setImageResource(R.drawable.shichang);
            ivsc.setLayoutParams(params);
            rl.addView(ivsc);

            params = new RelativeLayout.LayoutParams(250, 82);
            params.setMargins(190, 150, 0, 0);
            tvczsc = new TextView(this);
//        tvczsc.setText("");
            tvczsc.setText("操作时长：00:00");
            tvczsc.setTextColor(Color.WHITE);
            tvczsc.setTextSize(18);
            tvczsc.setLayoutParams(params);
            rl.addView(tvczsc);

            params = new RelativeLayout.LayoutParams(82, 82);
            params.setMargins(220, 200, 0, 0);
            ivdeng = new ImageView(this);
            ivdeng.setImageResource(R.drawable.dengguan);
            ivdeng.setLayoutParams(params);
            rl.addView(ivdeng);

            params = new RelativeLayout.LayoutParams(150, 82);
            params.setMargins(200, 300, 0, 0);
            tvdeng = new TextView(this);
//        tvdeng.setText("");
            tvdeng.setText("灯状态：…");
            tvdeng.setTextColor(Color.WHITE);
            tvdeng.setTextSize(18);
            tvdeng.setLayoutParams(params);
            rl.addView(tvdeng);

/*            params = new RelativeLayout.LayoutParams(150, 82);
            params.setMargins(405, 300, 0, 0);
            tvtj = new TextView(this);
            tvtj.setText("数量统计：...");
            tvtj.setTextColor(Color.WHITE);
            tvtj.setTextSize(18);
            tvtj.setLayoutParams(params);
            rl.addView(tvtj);*/
        }catch (Exception e){
            logger.error("初始化柜子其他图片出错",e);
        }
    }

    private void initGX1(){
        try{
            RelativeLayout.LayoutParams params ;
            params = new RelativeLayout.LayoutParams(126, 256);
            params.setMargins(400, 40, 0, 0);
            ImageView ivguizi = new ImageView(this);
            ivguizi.setImageResource(R.drawable.guiziyuanshi1);
            ivguizi.setLayoutParams(params);
            rl.addView(ivguizi);

            int yxcs=0;//有效层数
            if(Cache.gcqy1){
                yxcs=1;
            }
            if(Cache.gcqy2){
                yxcs=2;
            }
            if(Cache.gcqy3){
                yxcs=3;
            }
            if(Cache.gcqy4){
                yxcs=4;
            }
            if(Cache.gcqy5){
                yxcs=5;
            }
            List<Integer> listYXCS=initXY(yxcs);
            if(yxcs>=1){
                ivh1=new ImageView(this);
                ivh1.setImageResource(R.drawable.hongwaizhengchang1);
//        ivh1.setImageResource(R.drawable.hongwaichufa);
                params = new RelativeLayout.LayoutParams(100, 40);
                params.setMargins(413, listYXCS.get(0), 0, 0);
                ivh1.setLayoutParams(params);
                rl.addView(ivh1);
//        ivh1.setVisibility(View.INVISIBLE);
            }
            if(yxcs>=2){

                ivh2=new ImageView(this);
//        ivh2.setImageResource(R.drawable.hongwaichufa);
                ivh2.setImageResource(R.drawable.hongwaizhengchang1);
                params = new RelativeLayout.LayoutParams(100, 40);
                params.setMargins(413, listYXCS.get(1), 0, 0);
                ivh2.setLayoutParams(params);
                rl.addView(ivh2);
//        ivh2.setVisibility(View.INVISIBLE);
            }
            if(yxcs>=3){
                ivh3=new ImageView(this);
//        ivh3.setImageResource(R.drawable.hongwaichufa);
                ivh3.setImageResource(R.drawable.hongwaizhengchang1);
                params = new RelativeLayout.LayoutParams(100, 40);
                params.setMargins(413, listYXCS.get(2), 0, 0);
                ivh3.setLayoutParams(params);
                rl.addView(ivh3);
//        ivh3.setVisibility(View.INVISIBLE);
            }
            if(yxcs>=4){
                ivh4=new ImageView(this);
//        ivh4.setImageResource(R.drawable.hongwaichufa);
                ivh4.setImageResource(R.drawable.hongwaizhengchang1);
                params = new RelativeLayout.LayoutParams(100, 40);
                params.setMargins(413, listYXCS.get(3), 0, 0);
                ivh4.setLayoutParams(params);
                rl.addView(ivh4);
//        ivh4.setVisibility(View.INVISIBLE);
            }
            if(yxcs>=5){
                ivh5=new ImageView(this);
//        ivh5.setImageResource(R.drawable.hongwaichufa);
                ivh5.setImageResource(R.drawable.hongwaizhengchang1);
                params = new RelativeLayout.LayoutParams(100, 40);
                params.setMargins(413, listYXCS.get(4), 0, 0);
                ivh5.setLayoutParams(params);
                rl.addView(ivh5);
//        ivh5.setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            logger.error("初始化1号柜子出错",e);
        }

    }

    //初始化柜型位置
    private List<Integer> initXY(int yxcs){
        List<Integer> list = new ArrayList<Integer>();
        if(yxcs==1){
            list.add(150);
        }else if(yxcs==2){
            list.add(110);
            list.add(190);
        }else if(yxcs==3){
            list.add(90);
            list.add(150);
            list.add(210);
        }else if(yxcs==4){
            list.add(90);
            list.add(130);
            list.add(170);
            list.add(210);
        }else if(yxcs==5){
            list.add(70);
            list.add(110);
            list.add(150);
            list.add(190);
            list.add(230);
        }
        return list;
    }

    private void initGX2(){
        try{
            RelativeLayout.LayoutParams params ;
            params = new RelativeLayout.LayoutParams(126, 256);
            params.setMargins(400, 40, 0, 0);
            ImageView ivguizi = new ImageView(this);
            ivguizi.setImageResource(R.drawable.guiziyuanshi2);
            ivguizi.setLayoutParams(params);
            rl.addView(ivguizi);

            ivh1=new ImageView(this);
            ivh1.setImageResource(R.drawable.hongwaizhengchang2);
//        ivh1.setImageResource(R.drawable.hongwaichufa);
            params = new RelativeLayout.LayoutParams(16, 214);
            params.setMargins(410, 63, 0, 0);
            ivh1.setLayoutParams(params);
            rl.addView(ivh1);
//        ivh1.setVisibility(View.INVISIBLE);

            ivh2=new ImageView(this);
//        ivh2.setImageResource(R.drawable.hongwaichufa);
            ivh2.setImageResource(R.drawable.hongwaizhengchang2);
            params = new RelativeLayout.LayoutParams(16, 214);
            params.setMargins(428, 63, 0, 0);
            ivh2.setLayoutParams(params);
            rl.addView(ivh2);
//        ivh2.setVisibility(View.INVISIBLE);

            ivh3=new ImageView(this);
//        ivh3.setImageResource(R.drawable.hongwaichufa);
            ivh3.setImageResource(R.drawable.hongwaizhengchang2);
            params = new RelativeLayout.LayoutParams(16, 214);
            params.setMargins(446, 63, 0, 0);
            ivh3.setLayoutParams(params);
            rl.addView(ivh3);
//        ivh3.setVisibility(View.INVISIBLE);


            ivh4=new ImageView(this);
//        ivh4.setImageResource(R.drawable.hongwaichufa);
            ivh4.setImageResource(R.drawable.hongwaizhengchang2);
            params = new RelativeLayout.LayoutParams(16, 214);
            params.setMargins(464, 63, 0, 0);
            ivh4.setLayoutParams(params);
            rl.addView(ivh4);
//        ivh4.setVisibility(View.INVISIBLE);

            ivh5=new ImageView(this);
//        ivh5.setImageResource(R.drawable.hongwaichufa);
            ivh5.setImageResource(R.drawable.hongwaizhengchang2);
            params = new RelativeLayout.LayoutParams(16, 214);
            params.setMargins(482, 63, 0, 0);
            ivh5.setLayoutParams(params);
            rl.addView(ivh5);
//        ivh5.setVisibility(View.INVISIBLE);

            ivh6=new ImageView(this);
//        ivh5.setImageResource(R.drawable.hongwaichufa);
            ivh6.setImageResource(R.drawable.hongwaizhengchang2);
            params = new RelativeLayout.LayoutParams(16, 214);
            params.setMargins(500, 63, 0, 0);
            ivh6.setLayoutParams(params);
            rl.addView(ivh6);
        }catch (Exception e){
            logger.error("初始化2号柜子出错",e);
        }
    }

    private void initNO(){
        try{
            RelativeLayout.LayoutParams params ;
            params = new RelativeLayout.LayoutParams(126, 256);
            params.setMargins(400, 40, 0, 0);
            ImageView ivguizi = new ImageView(this);
            ivguizi.setImageResource(R.drawable.guizibukeyong);
            ivguizi.setLayoutParams(params);
            rl.addView(ivguizi);
        }catch (Exception e){
            logger.error("初始化错误柜型显示出错",e);
        }
    }

    private SpannableString generateCenterSpannableText(String value) {

        SpannableString s = new SpannableString(value);
        s.setSpan(new RelativeSizeSpan(5f), 0, value.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD), 0, value.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.argb(255,0x45,0x8b,0x00)), 0, value.length(), 0);

        //Typeface.NORMAL  #458B00
//        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    public class MyValueFormatter implements IValueFormatter {

        public MyValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            //return String.valueOf((int) entry.getY());
            return "";
        }
    }

    public class MyAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return String.valueOf((int)value)+"层";
        }
    }

    public class MyBValueFormatter implements IValueFormatter {

        public MyBValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) entry.getY());
        }
    }

    public class Day1AxisValueFormatter implements IAxisValueFormatter {
        HashMap<Float,String> fs ;


        public Day1AxisValueFormatter(HashMap<Float,String> fs) {
            this.fs=fs;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return fs.get(value);
        }
    }

    public class PieCharLinster implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, Highlight h) {

            try{
                PieEntry p = (PieEntry)e;
                String lab="";
                if(p.getLabel().contains("已过期")){
                    lab="已过期";

                }
                if(p.getLabel().contains("近效期")){
                    lab="近效期";
                }
                if(p.getLabel().contains("远效期")){
                    lab="远效期";
                }
                Intent  intent = new Intent(MainActivity.this,OperationActivity.class);
                intent.putExtra("yxq",lab);
                intent.putExtra("title",p.getLabel());
                startActivity(intent);
            }catch (Exception ex){
                logger.error("chart监听出错",ex);
            }

        }

        @Override
        public void onNothingSelected() {

        }
    }

    public class BarCharLinster implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, Highlight h) {

            BarEntry p = (BarEntry)e;


//            String type=fs.get(p.getX());
            Intent intent = new Intent(MainActivity.this, OperationActivity.class);
            intent.putExtra("ceng",String.valueOf((int)e.getX()));
//            intent.putExtra("time",yxqjx);
            startActivity(intent);

            /*Intent intent = new Intent(MainActivity.this,OperationActivity.class);
            startActivity(intent);*/
        }

        @Override
        public void onNothingSelected() {

        }
    }
    //初始化效期数据，连接的是本地的数据库
    private void initJXQData(){
        try{
            Cache.mapTotal.clear();

            ProductDao productDao=new ProductDao();
            List<HashMap<String,String>> list=productDao.getProductByJXQ();
            for(HashMap map : list){
                TotalMessage totalMessage;
                if(Cache.mapTotal.get(map.get("wz").toString())==null){
                    totalMessage=new TotalMessage();
                    getTotal(totalMessage,map);
                    Cache.mapTotal.put(map.get("wz").toString(),totalMessage);
                }else{
                    totalMessage=Cache.mapTotal.get(map.get("wz").toString());
                    getTotal(totalMessage,map);
                }
            }
            setDataBarChart();
        }catch (Exception e){
            logger.error("初始化效期出错",e);
        }
    }

    /**
     * 构建统计信息
     */
    private void getTotal(TotalMessage totalMessage,HashMap map){
        totalMessage.setLocation(map.get("wz").toString());
        Product product=new Product();
        product.setPp(map.get("pp").toString());
        product.setMc(map.get("zl").toString());
        product.setYxrq(map.get("yxq").toString());
        product.setXqpc(map.get("gg").toString());
        product.setEpc(map.get("card").toString());
        product.setLocation(map.get("wz").toString());
        if(System.currentTimeMillis()-Long.valueOf(map.get("yxq").toString())>30*24*60*60*1000){
            //非近效期耗材
            totalMessage.getQt().add(product);
        }else{
            //近效期耗材
            totalMessage.getJxq().add(product);
        }
    }

    @Override
    protected void onDestroy(){
        czscflag=false;
        System.exit(0);
        super.onDestroy();
    }

    class CZSCShow extends Thread{
        private Logger logger = Logger.getLogger(this.getClass());
        public void run(){

            int time=1;
            while(czscflag){
                try{
                    Message message = Message.obtain(Cache.myHandle);
                    Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                    data.putString("czsc",secToTime(time));
                    message.setData(data);
                    Cache.myHandle.sendMessage(message);
                    time=time+1;
                    /*if(time/600>0 && time%600==0){
                        HCProtocol.ST_GetAllCard();
                    }*/
                }catch (Exception e){
                    logger.error(e);
                }
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }
            czscShow=null;
        }
        // a integer to xx:xx:xx
        public  String secToTime(int time) {
            String timeStr = null;
            int hour = 0;
            int minute = 0;
            int second = 0;
            if (time <= 0)
                return "00:00";
            else {
                minute = time / 60;
                if (minute < 60) {
                    second = time % 60;
                    timeStr = unitFormat(minute) + ":" + unitFormat(second);
                } else {
                    hour = minute / 60;
                    if (hour > 99)
                        return "99:59:59";
                    minute = minute % 60;
                    second = time - hour * 3600 - minute * 60;
                    timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                }
            }
            return timeStr;
        }

        public  String unitFormat(int i) {
            String retStr = null;
            if (i >= 0 && i < 10)
                retStr = "0" + Integer.toString(i);
            else
                retStr = "" + i;
            return retStr;
        }

    }

    /**
     * 打开状态栏
     */
    private void showBar() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{
                    "am", "startservice", "-n", "com.android.systemui/.SystemUIService"});
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据到第三方平台
     */
    private void sendExternalProduct(String order){
        //发送数据到第三方平台
        HashMap<String,String> map =new HashMap<String,String>();
        if(SocketClient.socket!=null){
            Set<String> pr=map.keySet();
            HashMap<String,List<String>> mapJSON=new HashMap<String,List<String>>(); //key--location，List--耗材EPC

            if(Cache.pc==0){
                //如果是全部盘存，则将所有位置的标签耗材都要发送（可能该层耗材被全部拿走）
                int cs=0;
                if(Cache.gcqy1){
                    cs=1;
                }
                if(Cache.gcqy2){
                    cs=2;
                }
                if(Cache.gcqy3){
                    cs=3;
                }
                if(Cache.gcqy4){
                    cs=4;
                }
                if(Cache.gcqy5){
                    cs=5;
                }
                if(Cache.gcqy6){
                    cs=6;
                }
                for(int i=1;i<=cs;i++){
                    mapJSON.put(String.valueOf(i),new ArrayList<String>());
                }
            }else if(Cache.pc==1){
                //如果是触发盘存，则需要将所有触发的红外所在层的标签发送（可能该层耗材被全部拿走）
                for(String cf : Cache.cfpdcs){
                    if(cf.equals("0")){
                        continue;
                    }
                    mapJSON.put(cf,new ArrayList<String>());
                }
                Cache.cfpdcs.clear();
            }

            for(String p : pr){
                if(mapJSON.get(map.get(p))==null){
                    List<String> listP = new ArrayList<String>();
                    listP.add(p);
                    mapJSON.put(map.get(p),listP);
                }else{
                    mapJSON.get(map.get(p)).add(p);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"order\":\""+order+"\",\"code\":\""+Cache.appcode+"\",\"operator\":\""+Cache.operatorCode+"\",\"number\":\"");
            sb.append(UUID.randomUUID().toString()).append("\",\"data\":[");
            Set<String> location = mapJSON.keySet();
            for(String loa : location){
                sb.append("{\"location\":\"").append(loa).append("\",");
                sb.append("\"data\":[");
                List<String> listCard= mapJSON.get(loa);
                if(!listCard.isEmpty()){
                    for(String card : listCard){
                        sb.append("\"").append(card).append("\",");
                    }
                    sb.deleteCharAt(sb.length()-1).append("]},");
                }else{
                    sb.append("]},");
                }

            }
            if(!location.isEmpty()){
                sb.deleteCharAt(sb.length()-1);
            }
            sb.append("]}");
            String sendValue=sb.toString();
            map.clear();
            SocketClient.send(sendValue);
        }
    }

}
