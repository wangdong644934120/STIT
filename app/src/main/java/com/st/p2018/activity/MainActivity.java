package com.st.p2018.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.dao.PZDao;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.database.DatabaseManager;
import com.st.p2018.database.UpdateDB;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.LogUtil;

import com.st.p2018.util.MySpeechUtil;
import com.st.p2018.util.MyTextToSpeech;
import com.st.p2018.util.Utils;


import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;


public class MainActivity extends Activity {

    private PieChart mChart;
    private Button tvD;
    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
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
    private TextView tvtj;
    private TextView tvappTitle;

    private Button btnKD;
    private Button btnPD;

    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 3000;// 规定有效时间
    long[] mHits = new long[COUNTS];

    private CZSCShow czscShow=null;
    private boolean czscflag=true;
    Logger logger;

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

        logger =Logger.getLogger(this.getClass());
        initView();
        initSpeechPlug();
        Cache.myContext = this;
        initJXQData();
        new DeviceCom().start();
        if(Cache.lockScreen.equals("1")){
            logger.info("配置了锁屏");
        }

    }


    private void initView() {
        try{
            tvappTitle=(TextView)findViewById(R.id.apptitle);
            btnKD=(Button)findViewById(R.id.kaideng);
            btnKD.setOnClickListener(new onClickListener());
            btnPD=(Button)findViewById(R.id.pandian);
            btnPD.setOnClickListener(new onClickListener());
            rl=(RelativeLayout)findViewById(R.id.mylayout);
            tvD=(Button)findViewById(R.id.dian);
            tvD.setOnClickListener(new onClickListener());
            mChart = (PieChart) findViewById(R.id.chart);
            initPieChart();
            initHandler();
            initGXQT();
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
                                Intent intent = new Intent(MainActivity.this, SickActivity.class);
                                startActivity(intent);
                            }
                            if(bundle.getString("ui").toString().equals("access")){
                                Intent intent = new Intent(MainActivity.this, AccessConActivity.class);
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
                            mChart.animateY(500, Easing.EasingOption.EaseInCirc);
                        }
                        if(bundle.getString("initJXQExternal")!=null){
                            setData(new HashMap<String, String>());
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
                            pdshow();
                        }
                        if(bundle.getString("appname")!=null){
                            tvappTitle.setText(bundle.getString("appname").toString());
                            mChart.setCenterText(generateCenterSpannableText(Cache.appcode));
                            initJXQData();
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
     *
     * @author dinghaoyang
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
                            MyTextToSpeech.getInstance().speak("关灯成功");
                            Toast.makeText(MainActivity.this, "关灯成功", Toast.LENGTH_SHORT).show();
                        }else{
                            MyTextToSpeech.getInstance().speak("关灯失败");
                            Toast.makeText(MainActivity.this, "关灯失败", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        String app =getResources().getText(R.string.app_name).toString();

                        int a=0;
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
                    Cache.getHCCS=2;
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

    private void initPieChart() {
        try{
            mChart.setUsePercentValues(true);
            mChart.getDescription().setEnabled(false);
            mChart.getDescription().setTextSize(20f);
            mChart.setExtraOffsets(5, 10, 5, 5);

            mChart.setDragDecelerationFrictionCoef(0.95f);

            mChart.setCenterText(generateCenterSpannableText(Cache.appcode));

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
            l.setEnabled(false);

            // entry label styling
            mChart.setEntryLabelColor(Color.WHITE);
            //mChart.setEntryLabelTypeface(mTfRegular);
            mChart.setEntryLabelTextSize(20f);

        }catch (Exception e){
            logger.error("初始化chart显示出错",e);
        }
    }

    private void setData(HashMap<String,String> map) {
        try{
            if(Cache.external){
                String ygq=Cache.mapTotal.get("ygq")==null?"0":String.valueOf(Cache.mapTotal.get("ygq").size());
                String jxq=Cache.mapTotal.get("jxq")==null?"0":String.valueOf(Cache.mapTotal.get("jxq").size());
                String yxq=Cache.mapTotal.get("yxq")==null?"0":String.valueOf(Cache.mapTotal.get("yxq").size());
                map.put("ygq","已过期("+ygq+"个)");
                map.put("jxq","近效期("+jxq+"个)");
                map.put("yxq","远效期("+yxq+"个)");
                if(tvtj!=null){
                    tvtj.setText("数量统计："+String.valueOf(Integer.valueOf(ygq)+Integer.valueOf(jxq)+Integer.valueOf(yxq)));
                }

            }
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
        }catch (Exception e){
            logger.error("设置chart显示内容出错",e);
        }

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

            params = new RelativeLayout.LayoutParams(150, 82);
            params.setMargins(405, 300, 0, 0);
            tvtj = new TextView(this);
            tvtj.setText("数量统计：...");
            tvtj.setTextColor(Color.WHITE);
            tvtj.setTextSize(18);
            tvtj.setLayoutParams(params);
            rl.addView(tvtj);
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
    //初始化效期数据，连接的是本地的数据库
    private void initJXQData(){
        try{
            mChart.setCenterText(generateCenterSpannableText(Cache.appcode));
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
            tvtj.setText("数量统计："+(ygq+jxq+yxq)+"个");
        }catch (Exception e){
            logger.error("初始化效期出错",e);
        }
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

    private void pdshow(){
        Intent intent = new Intent(MainActivity.this, PDActivity.class);
        startActivity(intent);
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

}
