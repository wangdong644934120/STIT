package com.st.p2018.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.Product;
import com.st.p2018.externalentity.TotalMessage;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import org.apache.log4j.Logger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class PDActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private BarChart barChart;
    private TextView tvjxq;
    private TextView tvzkc;
    private SmartTable tableEPC;
    private TextView tvceng;
    private Logger logger = Logger.getLogger(this.getClass());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pd);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        Cache.getHCCS=0;
        initView();
    }

    private void initView(){
        try{
            tvfh=(TextView)findViewById(R.id.fh);
            tvfh.setOnClickListener(new onClickListener());
            tvtitle=(TextView)findViewById(R.id.title);
            tvtitle.setText("盘点结果");
            tvceng=(TextView)findViewById(R.id.tvceng);
            tvjxq=(TextView)findViewById(R.id.jxqtj);
            tvzkc=(TextView)findViewById(R.id.kctj);
            barChart=(BarChart)findViewById(R.id.barchart);
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
            rightAxis.setAxisMinimum(0f);

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
            show();
        }catch(Exception e){
            logger.error("初始化界面出错",e);
        }
    }

    private void initTable(String ceng){
        Column<String> column1 = new Column<>("序号", "xuhao");
        Column<String> column2 = new Column<>("EPC", "epc");
        Column<String> column3 = new Column<>("位置(层)", "location");
        List<TableColumn> list=new ArrayList<TableColumn>();
        Set<String> dealKeys=Cache.HCCSMap.keySet();
        int xuhao=0;
        for(String epc : dealKeys){
            if(Cache.HCCSMap.get(epc).equals(ceng)){
                TableColumn tc = new TableColumn();
                xuhao=xuhao+1;
                tc.setXuhao(String.valueOf(xuhao));
                tc.setEpc(epc);
                tc.setLocation(Cache.HCCSMap.get(epc));
                tc.setBz("");
                list.add(tc);
            }
        }

        tvceng.setText("第"+ceng+"层    EPC数量:"+list.size());
        TableData<TableColumn> tableData = new TableData<TableColumn>("",list, column1, column2,column3);
        //设置数据
        tableEPC = (SmartTable)findViewById(R.id.tableEPC);
        tableEPC.setTableData(tableData);
        tableEPC.getConfig().setShowXSequence(false);
        tableEPC.getConfig().setShowYSequence(false);
        tableEPC.getConfig().setShowTableTitle(false);
        tableEPC.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        tableEPC.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        tableEPC.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        tableEPC.getConfig().setMinTableWidth(200);
        tableEPC.getConfig().setFixedTitle(true);
        tableEPC.getConfig().setColumnTitleHorizontalPadding(59);
    }

    public class BarCharLinster implements OnChartValueSelectedListener {

        @Override
        public void onValueSelected(Entry e, Highlight h) {

            //BarEntry p = (BarEntry)e;
            /*Intent intent = new Intent(PDActivity.this, OperationActivity.class);
            intent.putExtra("ceng",String.valueOf((int)e.getX()));
            startActivity(intent);*/
            initTable(String.valueOf((int)e.getX()));

        }

        @Override
        public void onNothingSelected() {

        }
    }
    private void show(){
        getDataFromLocal();
        setData();
        initTable("1");
    }

    /**
     * 从本地数据库获取耗材信息
     */
    private void getDataFromLocal(){
        Set<String> dealKeys=Cache.HCCSMap.keySet();
        HashMap<String,String> mapSave=new HashMap<String,String>();
        List<HashMap<String,String>> list =new ArrayList<HashMap<String,String>>();
        ProductDao productDao=new ProductDao();
        list=productDao.getAllProduct();
        for(HashMap map : list){
            //取出标签
            if(!map.get("wz").toString().equals("0") && !dealKeys.contains(map.get("card").toString())){
                //标签被取出
                mapSave.put(map.get("card").toString(),"0");
            }
            //存放标签
            if(map.get("wz").toString().equals("0") && dealKeys.contains(map.get("card").toString())){
                //标签被存放
                mapSave.put(map.get("card").toString(),Cache.HCCSMap.get(map.get("card").toString()).toString());
            }
            //标签在柜子中
            if(dealKeys.contains(map.get("card").toString())){
                if(!map.get("wz").toString().equals("0") && !Cache.HCCSMap.get(map.get("card").toString()).equals(map.get("wz").toString())){
                    //标签位置更换
                    mapSave.put(map.get("card").toString(),Cache.HCCSMap.get(map.get("card").toString()));
                }
            }
        }
        //数据库更新内容
        Set<String> updatesKey=mapSave.keySet();
        for(String key : updatesKey){
            productDao.updateProductWZ(mapSave.get(key).toString(),key);
        }
        //更新效期统计
        Cache.mapTotal.clear();
        list=productDao.getProductByJXQ();
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
        sendJXQ();
    }

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

    private  void sendJXQ(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("initJXQ","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
    /**
     * 单击事件监听
     *
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {

                case R.id.fh:
                    Cache.myHandlePD=null;
                    PDActivity.this.finish();
                    break;
                default:
                    break;
            }
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
        return "第"+String.valueOf((int)value)+"层";
    }
}

    private void setData() {

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
                yValsjxq.add(new BarEntry(5,Cache.mapTotal.get("5").getJxq().size()));
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

    public class MyBValueFormatter implements IValueFormatter {

        public MyBValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.valueOf((int) entry.getY());
        }
    }

    public class TableColumn {
        private String xuhao;
        private String location;
        private String epc;
        private String bz;

        public String getBz() {
            return bz;
        }

        public void setBz(String bz) {
            this.bz = bz;
        }

        public String getXuhao() {
            return xuhao;
        }

        public void setXuhao(String xuhao) {
            this.xuhao = xuhao;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getEpc() {
            return epc;
        }

        public void setEpc(String epc) {
            this.epc = epc;
        }
    }
}
