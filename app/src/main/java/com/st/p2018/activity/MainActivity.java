package com.st.p2018.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.database.DatabaseManager;
import com.st.p2018.database.UpdateDB;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.entity.ProductBar;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.LogUtil;

import com.st.p2018.util.MySpeechUtil;
import com.st.p2018.util.MyTextToSpeech;


import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.spec.ECField;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {


    private Button btnPerson;
    private Button btnPD;
    private Button btnKM;
    private PieChart mChart;
    private Button tvD;
    private TextView tvTS;


    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    private List<String> listTS = new ArrayList<String>();
    HashMap<Float,String> fs=new HashMap<Float,String>();
    private String yxqjx;

    private RelativeLayout rl;
    private TextView d1;
    private TextView d2;
    private TextView d3;
    private TextView d4;
    private TextView d5;

    private TextView h1;
    private TextView h2;
    private TextView h3;
    private TextView h4;
    private TextView h5;

    private TextView men;




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
        //new DeviceCom().start();


    }


    private void initView() {
        rl=(RelativeLayout)findViewById(R.id.mylayout);
        initG();
//        btnPerson = (Button) findViewById(R.id.person);// 查询按钮
//        btnPD = (Button) findViewById(R.id.pd);
//        btnKM=(Button) findViewById(R.id.km);
//        btnPerson.setOnClickListener(new onClickListener());
//        btnPD.setOnClickListener(new onClickListener());
        //btnKM.setOnClickListener(new onClickListener());
        tvD=(Button)findViewById(R.id.dian);
        tvD.setOnClickListener(new onClickListener());
        mChart = (PieChart) findViewById(R.id.chart);


        tvTS=(TextView)findViewById(R.id.ts);
        initPieChart();

        //initBarChart(Cache.getProduct().get("乐普"));


        Cache.myHandle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                if (bundle.getString("ts") != null) {
                    //显示提示信息
                    listTS.add(bundle.getString("ts"));
                    if (listTS.size() > 6) {
                        listTS.remove(0);
                    }
                    String ts = "";
                    for (String s : listTS) {
                        if (s.contains("失败")) {
                            ts = ts + "<font color='#FF0000' size='25'>" + s + "</font><br>";
                        } else {
                            ts = ts + "<font color='#008000' size='25'>" + s + "</font><br>";
                        }
                        //ts=ts+s+"\n";
                    }
                    tvTS.setText(Html.fromHtml(ts));
                }
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
                    if(bundle.getString("ui").toString().equals("pd")){
                        Intent intent = new Intent(MainActivity.this, PersonActivity.class);
                        startActivity(intent);
                    }
                }
                //Html.fromHtml("<font color='#ff4461' size='5'>"+"哈哈测试:"+"</font>"+"<br>"+"<font color='#ff8833' size='20'>"+"效果怎么样呢"+"</font>")
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

        mChart.setCenterText(generateCenterSpannableText());

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



        setData();
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


    private void setData() {
        entries.add(new PieEntry(1, "已过期(0个)"));
        entries.add(new PieEntry(1, "1天-7天(20个)"));
        entries.add(new PieEntry(1, "7天-15天(30个)"));
        entries.add(new PieEntry(1, "15天以上(500个)"));

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
        colors.add(Color.YELLOW);
        colors.add(Color.BLUE);
        colors.add(Color.GREEN);
//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.JOYFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.COLORFUL_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.LIBERTY_COLORS)
//            colors.add(c);
//
//        for (int c : ColorTemplate.PASTEL_COLORS)
//            colors.add(c);
//
//        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

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

    private void initG(){
        RelativeLayout.LayoutParams params ;
        //背景图片
        params = new RelativeLayout.LayoutParams(390, 390);
        params.setMargins(20, 20, 20, 20);
        ImageView iv = new ImageView(this);
        iv.setBackgroundColor(Color.BLUE);
        iv.setImageResource(R.drawable.wsbj1);
        iv.setLayoutParams(params);
        rl.addView(iv);


        d1=new TextView(this);
        d1.setText("第一层");
        params = new RelativeLayout.LayoutParams(80, 60);
        params.setMargins(30, 100, 0, 0);
        d1.setLayoutParams(params);
        rl.addView(d1);
        d2=new TextView(this);
        d2.setText("第二层");
        params = new RelativeLayout.LayoutParams(80, 60);
        params.setMargins(30, 150, 0, 0);
        d2.setLayoutParams(params);
        rl.addView(d2);
        d3=new TextView(this);
        d3.setText("第三层");
        params = new RelativeLayout.LayoutParams(80, 60);
        params.setMargins(30, 200, 0, 0);
        d3.setLayoutParams(params);
        rl.addView(d3);
        d4=new TextView(this);
        d4.setText("第四层");
        params = new RelativeLayout.LayoutParams(80, 60);
        params.setMargins(30, 250, 0, 0);
        d4.setLayoutParams(params);
        rl.addView(d4);
        d5=new TextView(this);
        d5.setText("第五层");
        params = new RelativeLayout.LayoutParams(80, 60);
        params.setMargins(30, 300, 0, 0);
        d5.setLayoutParams(params);
        rl.addView(d5);
        //灯带1
        params = new RelativeLayout.LayoutParams(20, 280);
        params.setMargins(80, 80, 0, 0);
        ImageView ivd1 = new ImageView(this);
        ivd1.setBackgroundColor(Color.GREEN);
        ivd1.setLayoutParams(params);
        rl.addView(ivd1);

        //门
        params = new RelativeLayout.LayoutParams(250, 40);
        params.setMargins(100, 40, 0, 0);
        ImageView ivmen = new ImageView(this);
        ivmen.setBackgroundColor(Color.YELLOW);
        ivmen.setLayoutParams(params);
        rl.addView(ivmen);


        //灯带2
        params = new RelativeLayout.LayoutParams(20, 280);
        params.setMargins(350, 80, 0, 0);
        ImageView ivd2 = new ImageView(this);
        ivd2.setBackgroundColor(Color.GREEN);
        ivd2.setLayoutParams(params);
        rl.addView(ivd2);

        //红外
//        ImageView ivh1=new ImageView(this);
//        ivh1.setBackgroundColor(Color.RED);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 100, 0, 0);
//        ivh1.setLayoutParams(params);
//        rl.addView(ivh1);
//
//        ImageView ivh2=new ImageView(this);
//        ivh2.setBackgroundColor(Color.RED);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 150, 0, 0);
//        ivh2.setLayoutParams(params);
//        rl.addView(ivh2);
//
//        ImageView ivh3=new ImageView(this);
//        ivh3.setBackgroundColor(Color.RED);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 200, 0, 0);
//        ivh3.setLayoutParams(params);
//        rl.addView(ivh3);
//
//        ImageView ivh4=new ImageView(this);
//        ivh4.setBackgroundColor(Color.RED);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 250, 0, 0);
//        ivh4.setLayoutParams(params);
//        rl.addView(ivh4);
//
//        ImageView ivh5=new ImageView(this);
//        ivh5.setBackgroundColor(Color.RED);
//        params = new RelativeLayout.LayoutParams(220, 20);
//        params.setMargins(110, 300, 0, 0);
//        ivh5.setLayoutParams(params);
//        rl.addView(ivh5);


        //gif
//        ImageView ivh5=new ImageView(this);
//        params = new RelativeLayout.LayoutParams(200, 200);
//        params.setMargins(110, 100, 0, 0);
//        ivh5.setLayoutParams(params);
//        rl.addView(ivh5);
//        RequestOptions options = new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
//        Glide.with(this).load(R.drawable.timg).apply(options).into(ivh5);

        //测试旋转
        ImageView ivh1=new ImageView(this);
        ivh1.setBackgroundColor(Color.BLUE);
        ivh1.setImageResource(R.drawable.wshw);
        params = new RelativeLayout.LayoutParams(220, 20);
        params.setMargins(110, 100, 0, 0);
        ivh1.setLayoutParams(params);
        ivh1.setRotation(-15);
        rl.addView(ivh1);

        ImageView ivh2=new ImageView(this);
        ivh2.setBackgroundColor(Color.BLUE);
        ivh2.setImageResource(R.drawable.wshw);
        params = new RelativeLayout.LayoutParams(220, 20);
        params.setMargins(110, 130, 0, 0);
        ivh2.setLayoutParams(params);
        ivh2.setRotation(-15);
        rl.addView(ivh2);


    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("近效期图示");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 5, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 0, 5, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 5, 0);
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
            if(p.getLabel().contains("1天-7天")){
                lab="1天-7天";
            }
            if(p.getLabel().contains("7天-15天")){
                lab="7天-15天";
            }
            if(p.getLabel().contains("15天以上")){
                lab="15天以上";
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

}
