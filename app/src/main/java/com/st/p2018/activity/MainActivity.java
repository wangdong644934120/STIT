package com.st.p2018.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView tvTS;
    private PieChart mChart;
    private BarChart bChart;
    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
    private List<String> listTS = new ArrayList<String>();
    HashMap<Float,String> fs=new HashMap<Float,String>();
    private String yxqjx;


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
        new DeviceCom().start();


    }


    private void initView() {
//        btnPerson = (Button) findViewById(R.id.person);// 查询按钮
//        btnPD = (Button) findViewById(R.id.pd);
//        btnKM=(Button) findViewById(R.id.km);
//        btnPerson.setOnClickListener(new onClickListener());
//        btnPD.setOnClickListener(new onClickListener());
//        btnKM.setOnClickListener(new onClickListener());
        mChart = (PieChart) findViewById(R.id.chart);
        bChart = (BarChart) findViewById(R.id.bar);
        tvTS = (TextView) findViewById(R.id.ts);

        //tvTS=(TextView)findViewById(R.id.ts);
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
//                case R.id.person:
//                    Intent intent = new Intent(MainActivity.this, PersonActivity.class);
//                    startActivity(intent);
//                    break;
//                case R.id.pd:
//                    sound("开始盘点");
//                    break;
//                case R.id.km:

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

    private void initBarChart(HashMap<String,List<ProductBar> > map,String yxqjx,String yxq) {
        bChart.notifyDataSetChanged(); // let the chart know it's data changed
        bChart.invalidate();

        this.yxqjx=yxqjx;

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int count=1;
        Set<String> keys = map.keySet();
        for(String key : keys){
            yVals1.add(new BarEntry(count,map.get(key).size()));
            float f=count;
            fs.put(f,key);
            count++;
        }

        bChart.setDrawBarShadow(false);
        bChart.setDrawValueAboveBar(true);

        bChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        bChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        bChart.setPinchZoom(false);

        bChart.setDrawGridBackground(false);

        bChart.setDragEnabled(false);// 是否可以拖拽
        bChart.setScaleEnabled(false);// 是否可以缩
        // mChart.setDrawYLabels(false);
        bChart.setOnChartValueSelectedListener(new BarCharLinster());
        IAxisValueFormatter xAxisFormatter = new Day1AxisValueFormatter(fs);

        XAxis xAxis = bChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // xAxis.setTypeface(mTfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextSize(15f);
        xAxis.setValueFormatter(xAxisFormatter);

        IAxisValueFormatter custom = new MyAxisValueFormatter();

        YAxis leftAxis = bChart.getAxisLeft();
        //leftAxis.setTypeface(mTfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextSize(10f);

        YAxis rightAxis = bChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(mTfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setTextSize(10f);

        Legend l = bChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(20f);
        l.setXEntrySpace(4f);

        setbData(yVals1,yxq);
    }

    private void setbData(ArrayList<BarEntry> yVals1,String pp) {

        BarDataSet set1;

        set1 = new BarDataSet(yVals1," 近效期统计 --- "+pp);

        set1.setDrawIcons(false);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        set1.setColors(colors);

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(dataSets);
        data.setValueTextSize(20f);
        data.setBarWidth(0.9f);
        data.setValueFormatter(new MyBValueFormatter());

        bChart.setData(data);
        bChart.notifyDataSetChanged(); // let the chart know it's data changed
        bChart.invalidate();
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
            initBarChart(Cache.getProduct().get(lab),lab,p.getLabel());
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
