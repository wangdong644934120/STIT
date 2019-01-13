package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.database.DatabaseManager;
import com.st.p2018.database.UpdateDB;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.LogUtil;

import com.st.p2018.util.MySpeechUtil;
import com.st.p2018.util.MyTextToSpeech;


import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends Activity {

    private PieChart mChart;
    private Button tvD;
    private TextView tvTS;
    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    private List<String> listTS = new ArrayList<String>();

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

    private Button btnKD;
    private Button btnPD;

    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 3000;// 规定有效时间
    long[] mHits = new long[COUNTS];

    private CZSCShow czscShow=null;
    private boolean czscflag=true;

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
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        LogUtil.initLog();// 初始log
        initView();
        initDataBase();
        initSpeechPlug();
        Cache.myContext = this;
        initJXQData();
        initGX1();
        new DeviceCom().start();
    }


    private void initView() {

        btnKD=(Button)findViewById(R.id.kaideng);
        btnKD.setOnClickListener(new onClickListener());
        btnPD=(Button)findViewById(R.id.pandian);
        btnPD.setOnClickListener(new onClickListener());
        rl=(RelativeLayout)findViewById(R.id.mylayout);

        tvD=(Button)findViewById(R.id.dian);
        tvD.setOnClickListener(new onClickListener());
        mChart = (PieChart) findViewById(R.id.chart);
//        tvTS=(TextView)findViewById(R.id.ts);
        initPieChart();

        Cache.myHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("ts") != null) {
                    if(1==1){
                        return;
                    }
                    //显示提示信息
                    listTS.add(bundle.getString("ts"));
                    if (listTS.size() > 15) {
                        listTS.remove(0);
                    }
                    String ts = "";
                    for (String s : listTS) {
                        if(s.contains("时间:")){
                            ts = ts + "<font color='#4A4A4A' size='25'>" + s.substring(3,s.length()) + "</font><br>";
                        }
                        if (s.contains("报警:")) {
                            ts = ts + "<font color='#FF0000' size='25'>" + s.substring(3,s.length())  + "</font><br>";
                        }
                        if(s.contains("状态:")) {
                            ts = ts + "<font color='#4A4A4A' size='25'>" +  s.substring(3,s.length()) + "</font><br>";
                        }
                        if(s.contains("操作:")) {
                            ts = ts + "<font color='#40E0D0' size='25'>" +  s.substring(3,s.length()) + "</font><br>";
                        }
                    }
                    tvTS.setText(Html.fromHtml(ts));
                }
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
                               ivh1.setImageResource(R.drawable.hongwaichufa);
                           }else{
                               //替换红外行程1不触发图片
                               ivh1.setImageResource(R.drawable.hongwaizhengchang);
                           }

                       }
                        if(bundle.get("wz").toString().equals("2")){
                            if(bundle.get("zt").toString().equals("1")){
                                //替换红外行程1触发图片
                                ivh2.setImageResource(R.drawable.hongwaichufa);
                            }else{
                                //替换红外行程1不触发图片
                                ivh2.setImageResource(R.drawable.hongwaizhengchang);
                            }

                        }
                        if(bundle.get("wz").toString().equals("3")){
                            if(bundle.get("zt").toString().equals("1")){
                                //替换红外行程1触发图片
                                ivh3.setImageResource(R.drawable.hongwaichufa);
                            }else{
                                //替换红外行程1不触发图片
                                ivh3.setImageResource(R.drawable.hongwaizhengchang);
                            }

                        }
                        if(bundle.get("wz").toString().equals("4")){
                            if(bundle.get("zt").toString().equals("1")){
                                //替换红外行程1触发图片
                                ivh4.setImageResource(R.drawable.hongwaichufa);
                            }else{
                                //替换红外行程1不触发图片
                                ivh4.setImageResource(R.drawable.hongwaizhengchang);
                            }

                        }
                        if(bundle.get("wz").toString().equals("5")){
                            if(bundle.get("zt").toString().equals("1")){
                                //替换红外行程1触发图片
                                ivh5.setImageResource(R.drawable.hongwaichufa);
                            }else{
                                //替换红外行程1不触发图片
                                ivh5.setImageResource(R.drawable.hongwaizhengchang);
                            }

                        }
                        if(bundle.get("wz").toString().equals("6")){
                            if(!Cache.gx.equals("Ⅰ型")){
                                if(bundle.get("zt").toString().equals("1")){
                                    //替换红外行程1触发图片
                                    ivh6.setImageResource(R.drawable.hongwaichufa);
                                }else{
                                    //替换红外行程1不触发图片
                                    ivh6.setImageResource(R.drawable.hongwaizhengchang);
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
                    mChart.animateY(500, Easing.EasingOption.EaseInCirc);
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
                    }else{
                        initGX2();
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
            }
        };


    }

    //显示Toast函数
    private void displayToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
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
                case R.id.dian:
                    SelectDialog selectDialog = new SelectDialog(MainActivity.this,R.style.dialog);//创建Dialog并设置样式主题
                    Window win = selectDialog.getWindow();
                    WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                    params.x = 240;//设置x坐标
                    params.y = -360;//设置y坐标
                    win.setAttributes(params);
                    selectDialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog
                    selectDialog.show();
                    break;
                case R.id.kaideng:
                    if(Cache.zmdzt){
                        boolean bl=HCProtocol.ST_CloseLight();
                        if(bl){
                            MyTextToSpeech.getInstance().speak("关灯成功");
                            Toast.makeText(MainActivity.this, "关灯成功", Toast.LENGTH_SHORT).show();
                        }else{
                            MyTextToSpeech.getInstance().speak("关灯失败");
                            Toast.makeText(MainActivity.this, "关灯失败", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        boolean bl=HCProtocol.ST_OpenLight();
                        if(bl){
                            MyTextToSpeech.getInstance().speak("开灯成功");
                            Toast.makeText(MainActivity.this, "开灯成功", Toast.LENGTH_SHORT).show();
                        }else{
                            MyTextToSpeech.getInstance().speak("开灯失败");
                            Toast.makeText(MainActivity.this, "开灯失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case R.id.pandian:
                    if(HCProtocol.ST_GetAllCard()){
                    }else{
                        MyTextToSpeech.getInstance().speak("盘点失败");
                        Toast.makeText(MainActivity.this, "盘点失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }

    }

    private boolean initDataBase() {
        DatabaseManager.createDatabaseIfNone(MainActivity.this);// 检测数据库，若不存在则创建
        // 数据库连接测试
        try {

            SQLiteDatabase db = DatabaseManager.openReadWrite();
            if (db != null && db.isDatabaseIntegrityOk()) {
                //logger.info("打开数据库连接成功");
                db.close();// 关闭数据库
            } else {
                return false;// 数据库打开失败或不可用
            }
            UpdateDB upDB = new UpdateDB(MainActivity.this);
            upDB.updata();
        } catch (Exception ex) {
            //logger.error("初始化数据库出错", ex);
            return false;
        }
        return true;
    }

    private void sound(String value) {

        MyTextToSpeech.getInstance().speak(value);
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

    private void initPieChart() {

        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.getDescription().setTextSize(20f);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setCenterText(generateCenterSpannableText("效期图示"));

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setEntryLabelTextSize(30f);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(40f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);


        HashMap map = new HashMap();
        map.put("ygq","已过期(0个)");
        map.put("jxq","近效期(0个)");
        map.put("yxq","远效期(0个)");
        setData(map);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);
        mChart.setOnChartValueSelectedListener(new PieCharLinster());

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(10f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        l.setTextSize(15f);

        // entry label styling
        mChart.setEntryLabelColor(Color.WHITE);
        //mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(20f);


    }

    private void setData(HashMap<String,String> map) {

        entries.clear();
        entries.add(new PieEntry(1, map.get("ygq").toString()));
        entries.add(new PieEntry(1,  map.get("jxq").toString()));
        entries.add(new PieEntry(1,  map.get("yxq").toString()));


        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(20f);

        // add a lot of colors
        dataSet.setColor(Color.GRAY);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(Color.RED);
        colors.add(Color.rgb(238,242,14));
        colors.add(Color.rgb(135,162,86));

        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        //data.setValueFormatter(new PercentFormatter());
        data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private void initGX(){
        RelativeLayout.LayoutParams params ;
        //背景图片
//        params = new RelativeLayout.LayoutParams(390, 390);
//        params.setMargins(20, 20, 20, 20);
//        ImageView iv = new ImageView(this);
//        iv.setBackgroundColor(Color.WHITE);
//        iv.setImageResource(R.drawable.qsh);
//        iv.setLayoutParams(params);
//        rl.addView(iv);

//        ImageView ivh5=new ImageView(this);
//        params = new RelativeLayout.LayoutParams(390, 390);
//        params.setMargins(20, 20, 20, 20);
//        ivh5.setLayoutParams(params);
//        rl.addView(ivh5);
//        RequestOptions options = new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//        Glide.with(this).load(R.drawable.qsh).apply(options).into(ivh5);
    }

    private void initGX1(){
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
        tvmzt.setText("");
//        tvmzt.setText("门状态：门已关");
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
        tvczsc.setText("");
//        tvczsc.setText("操作时长：0分0秒");
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
        tvdeng.setText("");
//        tvdeng.setText("灯状态：关闭");
        tvdeng.setTextColor(Color.WHITE);
        tvdeng.setTextSize(18);
        tvdeng.setLayoutParams(params);
        rl.addView(tvdeng);

        params = new RelativeLayout.LayoutParams(126, 256);
        params.setMargins(400, 40, 0, 0);
        ImageView ivguizi = new ImageView(this);
        ivguizi.setImageResource(R.drawable.guiziyuanshi);
        ivguizi.setLayoutParams(params);
        rl.addView(ivguizi);

        ivh1=new ImageView(this);
        ivh1.setImageResource(R.drawable.hongwaizhengchang);
        params = new RelativeLayout.LayoutParams(110, 40);
        params.setMargins(410, 70, 0, 0);
        ivh1.setLayoutParams(params);
//        ivh1.setRotation(-15);
        rl.addView(ivh1);

        ivh2=new ImageView(this);
        ivh2.setImageResource(R.drawable.hongwaizhengchang);
        params = new RelativeLayout.LayoutParams(110, 40);
        params.setMargins(410, 110, 0, 0);
        ivh2.setLayoutParams(params);
//        ivh2.setRotation(-15);
        rl.addView(ivh2);

        ivh3=new ImageView(this);
        ivh3.setImageResource(R.drawable.hongwaizhengchang);
        params = new RelativeLayout.LayoutParams(110, 40);
        params.setMargins(410, 150, 0, 0);
        ivh3.setLayoutParams(params);
//        ivh2.setRotation(-15);
        rl.addView(ivh3);


        ivh4=new ImageView(this);
        ivh4.setImageResource(R.drawable.hongwaizhengchang);
        params = new RelativeLayout.LayoutParams(110, 40);
        params.setMargins(410, 190, 0, 0);
        ivh4.setLayoutParams(params);
//        ivh2.setRotation(-15);
        rl.addView(ivh4);

        ivh5=new ImageView(this);
        ivh5.setImageResource(R.drawable.hongwaizhengchang);
        params = new RelativeLayout.LayoutParams(110, 40);
        params.setMargins(410, 230, 0, 0);
        ivh5.setLayoutParams(params);
//        ivh2.setRotation(-15);
        rl.addView(ivh5);

//
//
//        d1=new TextView(this);
//        d1.setText("第一层");
//        d1.setId(R.id.textview_1);
//        d1.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 100, 0, 0);
//        d1.setLayoutParams(params);
//        rl.addView(d1);
//        d2=new TextView(this);
//        d2.setText("第二层");
//        d2.setId(R.id.textview_2);
//        d2.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 150, 0, 0);
//        d2.setLayoutParams(params);
//        rl.addView(d2);
//        d3=new TextView(this);
//        d3.setText("第三层");
//        d3.setId(R.id.textview_3);
//        d3.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 200, 0, 0);
//        d3.setLayoutParams(params);
//        rl.addView(d3);
//        d4=new TextView(this);
//        d4.setText("第四层");
//        d4.setId(R.id.textview_4);
//        d4.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 250, 0, 0);
//        d4.setLayoutParams(params);
//        rl.addView(d4);
//        d5=new TextView(this);
//        d5.setText("第五层");
//        d5.setId(R.id.textview_5);
//        d5.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 300, 0, 0);
//        d5.setLayoutParams(params);
//        rl.addView(d5);
//        //灯带1
//        params = new RelativeLayout.LayoutParams(20, 280);
//        params.setMargins(80, 80, 0, 0);
//        ivd1 = new ImageView(this);
//        ivd1.setBackgroundColor(Color.GREEN);
//        ivd1.setImageResource(R.drawable.wsd1);
//        ivd1.setLayoutParams(params);
//        rl.addView(ivd1);
//
//        //门
//        params = new RelativeLayout.LayoutParams(250, 40);
//        params.setMargins(100, 40, 0, 0);
//        ivmen = new ImageView(this);
//        ivmen.setBackgroundColor(Color.YELLOW);
//        ivmen.setImageResource(R.drawable.wsm);
//        ivmen.setLayoutParams(params);
//        rl.addView(ivmen);
//
//
//        //灯带2
//        params = new RelativeLayout.LayoutParams(20, 280);
//        params.setMargins(350, 80, 0, 0);
//        ivd2 = new ImageView(this);
//        ivd2.setBackgroundColor(Color.GREEN);
//        ivd2.setImageResource(R.drawable.wsd1);
//        ivd2.setLayoutParams(params);
//        rl.addView(ivd2);
//
//        ivh1=new ImageView(this);
//        ivh1.setBackgroundColor(Color.BLUE);
//        ivh1.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 100, 0, 0);
//        ivh1.setLayoutParams(params);
////        ivh1.setRotation(-15);
//        rl.addView(ivh1);
//
//        ivh2=new ImageView(this);
//        ivh2.setBackgroundColor(Color.BLUE);
//        ivh2.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 150, 0, 0);
//        ivh2.setLayoutParams(params);
////        ivh2.setRotation(-15);
//        rl.addView(ivh2);
//
//        ivh3=new ImageView(this);
//        ivh3.setBackgroundColor(Color.BLUE);
//        ivh3.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 200, 0, 0);
//        ivh3.setLayoutParams(params);
////        ivh2.setRotation(-15);
//        rl.addView(ivh3);
//
//
//        ivh4=new ImageView(this);
//        ivh4.setBackgroundColor(Color.BLUE);
//        ivh4.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 250, 0, 0);
//        ivh4.setLayoutParams(params);
////        ivh2.setRotation(-15);
//        rl.addView(ivh4);
//
//        ivh5=new ImageView(this);
//        ivh5.setBackgroundColor(Color.BLUE);
//        ivh5.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 300, 0, 0);
//        ivh5.setLayoutParams(params);
////        ivh2.setRotation(-15);
//        rl.addView(ivh5);



    }

    private void initGX2(){
        RelativeLayout.LayoutParams params ;
        //背景图片
        params = new RelativeLayout.LayoutParams(390, 390);
        params.setMargins(20, 20, 20, 20);
        ImageView iv = new ImageView(this);
        iv.setBackgroundColor(Color.BLUE);
        //iv.setImageResource(R.drawable.wsbj1);
        iv.setLayoutParams(params);
        rl.addView(iv);


//        d1=new TextView(this);
//        d1.setText("第一组");
//        d1.setId(R.id.textview_1);
//        d1.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 100, 0, 0);
//        d1.setLayoutParams(params);
//        rl.addView(d1);
//        d2=new TextView(this);
//        d2.setText("第二组");
//        d2.setId(R.id.textview_2);
//        d2.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 150, 0, 0);
//        d2.setLayoutParams(params);
//        rl.addView(d2);
//        d3=new TextView(this);
//        d3.setText("第三组");
//        d3.setId(R.id.textview_3);
//        d3.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 200, 0, 0);
//        d3.setLayoutParams(params);
//        rl.addView(d3);
//        d4=new TextView(this);
//        d4.setText("第四组");
//        d4.setId(R.id.textview_4);
//        d4.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 250, 0, 0);
//        d4.setLayoutParams(params);
//        rl.addView(d4);
//        d5=new TextView(this);
//        d5.setText("第五组");
//        d5.setId(R.id.textview_5);
//        d5.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 300, 0, 0);
//        d5.setLayoutParams(params);
//        rl.addView(d5);
//        d6=new TextView(this);
//        d6.setText("第六组");
//        d6.setId(R.id.textview_6);
//        d6.setOnClickListener(new onClickListener());
//        params = new RelativeLayout.LayoutParams(80, 60);
//        params.setMargins(30, 350, 0, 0);
//        d6.setLayoutParams(params);
//        rl.addView(d6);
//        //灯带1
//        params = new RelativeLayout.LayoutParams(20, 280);
//        params.setMargins(80, 80, 0, 0);
//        ivd1 = new ImageView(this);
//        ivd1.setBackgroundColor(Color.GREEN);
//        ivd1.setImageResource(R.drawable.wsd1);
//        ivd1.setLayoutParams(params);
//        rl.addView(ivd1);
//
//        //门
//        params = new RelativeLayout.LayoutParams(250, 40);
//        params.setMargins(100, 40, 0, 0);
//        ivmen = new ImageView(this);
//        ivmen.setBackgroundColor(Color.YELLOW);
//        ivmen.setImageResource(R.drawable.wsm);
//        ivmen.setLayoutParams(params);
//        rl.addView(ivmen);
//
//
//        //灯带2
//        params = new RelativeLayout.LayoutParams(20, 280);
//        params.setMargins(350, 80, 0, 0);
//        ivd2 = new ImageView(this);
//        ivd2.setBackgroundColor(Color.GREEN);
//        ivd2.setImageResource(R.drawable.wsd1);
//        ivd2.setLayoutParams(params);
//        rl.addView(ivd2);
//
//        ivh1=new ImageView(this);
//        ivh1.setBackgroundColor(Color.BLUE);
//        ivh1.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 100, 0, 0);
//        ivh1.setLayoutParams(params);
//        ivh1.setRotation(-15);
//        rl.addView(ivh1);
//
//        ivh2=new ImageView(this);
//        ivh2.setBackgroundColor(Color.BLUE);
//        ivh2.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 150, 0, 0);
//        ivh2.setLayoutParams(params);
//        ivh2.setRotation(-15);
//        rl.addView(ivh2);
//
//        ivh3=new ImageView(this);
//        ivh3.setBackgroundColor(Color.BLUE);
//        ivh3.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 200, 0, 0);
//        ivh3.setLayoutParams(params);
//        ivh3.setRotation(-15);
//        rl.addView(ivh3);
//
//
//        ivh4=new ImageView(this);
//        ivh4.setBackgroundColor(Color.BLUE);
//        ivh4.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 250, 0, 0);
//        ivh4.setLayoutParams(params);
//        ivh4.setRotation(-15);
//        rl.addView(ivh4);
//
//        ivh5=new ImageView(this);
//        ivh5.setBackgroundColor(Color.BLUE);
//        ivh5.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 300, 0, 0);
//        ivh5.setLayoutParams(params);
//        ivh5.setRotation(-15);
//        rl.addView(ivh5);
//
//        ivh6=new ImageView(this);
//        ivh6.setBackgroundColor(Color.BLUE);
//        ivh6.setImageResource(R.drawable.wshw);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 350, 0, 0);
//        ivh6.setLayoutParams(params);
//        ivh6.setRotation(-15);
//        rl.addView(ivh6);
    }

    private SpannableString generateCenterSpannableText(String value) {

        SpannableString s = new SpannableString(value);
        s.setSpan(new RelativeSizeSpan(1.7f), 0, value.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, value.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, value.length(), 0);
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

    public class MyBValueFormatter implements IValueFormatter {

        public MyBValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) entry.getY());

        }
    }


    public class MyAxisValueFormatter implements IAxisValueFormatter {

        private DecimalFormat mFormat;

        public MyAxisValueFormatter() {
            mFormat = new DecimalFormat("###,###,###,##0.0");
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            //return mFormat.format(value) + " $";
            return String.valueOf((int)value)+"个";
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
        }

        @Override
        public void onNothingSelected() {

        }
    }

    public class BarCharLinster implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, Highlight h) {

//            BarEntry p = (BarEntry)e;
//            String type=fs.get(p.getX());
//            Intent intent = new Intent(MainActivity.this, ProductActivity.class);
//            intent.putExtra("type",type);
//            intent.putExtra("time",yxqjx);
//            startActivity(intent);

            Intent intent = new Intent(MainActivity.this,OperationActivity.class);
            startActivity(intent);

        }

        @Override
        public void onNothingSelected() {

        }
    }

    private void initJXQData(){
        mChart.setCenterText(generateCenterSpannableText("效期图示"));
        ProductDao productDao= new ProductDao();
        List<HashMap<String,String>> list = productDao.getProductByJXQ();
        int ygq=0;
        int jxq=0;
        int yxq=0;
        long current = System.currentTimeMillis();
        long dt = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        long t7=dt+1000*3600*24*7;
        for(HashMap map : list){
            //已过期  小于当前时间
            //近效期  当天0点到7天后24点
            //远效期  8天后0点后
            if(Long.valueOf(map.get("yxq").toString())<dt){
                ygq=ygq+1;
            }else if(Long.valueOf(map.get("yxq").toString())>=dt && Long.valueOf(map.get("yxq").toString())<t7){
                jxq=jxq+1;
            }else if(Long.valueOf(map.get("yxq").toString())>=t7){
                yxq=yxq+1;
            }
        }
        HashMap map = new HashMap();
        map.put("ygq","已过期("+ygq+"个)");
        map.put("jxq","近效期("+jxq+"个)");
        map.put("yxq","远效期("+yxq+"个)");
        setData(map);

    }

    private void continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            Toast.makeText(this, "连续点击了5次,程序退出", Toast.LENGTH_LONG).show();
            android.os.Process.killProcess(android.os.Process.myPid());

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
}
