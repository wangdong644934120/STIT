package com.st.p2018.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.entity.PDEntity;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.ExpportDataBeExcel;

import org.apache.log4j.Logger;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

public class PDActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private RelativeLayout rl;
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
            if(Cache.external){

            }else{
                getDataFromLocal();
            }

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
            if(Cache.gcqy6){
                yxcs=6;
            }
            rl=(RelativeLayout)findViewById(R.id.layoutpd);
            RelativeLayout.LayoutParams paramsxq ;
            //操作员图片

            paramsxq = new RelativeLayout.LayoutParams(130, 80);
            paramsxq.setMargins(110, 150, 0, 0);
            TextView tvygq = new TextView(this);
            tvygq.setText("已过期");
            tvygq.setGravity(Gravity.CENTER);
            tvygq.setTextColor(Color.BLACK);
            tvygq.setTextSize(25);
            tvygq.setLayoutParams(paramsxq);

            paramsxq = new RelativeLayout.LayoutParams(130, 80);
            paramsxq.setMargins(240, 150, 0, 0);
            TextView tvjxq = new TextView(this);
            tvjxq.setText("近效期");
            tvjxq.setGravity(Gravity.CENTER);
            tvjxq.setTextColor(Color.BLACK);
            tvjxq.setTextSize(25);
            tvjxq.setLayoutParams(paramsxq);

            paramsxq = new RelativeLayout.LayoutParams(130, 80);
            paramsxq.setMargins(370, 150, 0, 0);
            TextView tvyxq = new TextView(this);
            tvyxq.setText("远效期");
            tvyxq.setGravity(Gravity.CENTER);
            tvyxq.setTextColor(Color.BLACK);
            tvyxq.setTextSize(25);
            tvyxq.setLayoutParams(paramsxq);
            rl.addView(tvygq);
            rl.addView(tvjxq);
            rl.addView(tvyxq);

            //更新界面显示
            int titleShow=0;
            for(int i=1;i<=yxcs;i++){
                rl=(RelativeLayout)findViewById(R.id.layoutpd);
                RelativeLayout.LayoutParams params ;
                //操作员图片

                params = new RelativeLayout.LayoutParams(100, 80);
                params.setMargins(10, 150+i*100, 0, 0);
                TextView tv1 = new TextView(this);
                String wz="第"+i+"层/抽";
                tv1.setText(wz);
                tv1.setGravity(Gravity.CENTER);
                tv1.setTextColor(Color.BLACK);
                tv1.setTextSize(20);
                tv1.setLayoutParams(params);
                rl.addView(tv1);

                params = new RelativeLayout.LayoutParams(130, 80);
                params.setMargins(110, 150+i*100, 0, 0);
                TextView tv1y = new TextView(this);
                tv1y.setBackgroundColor(Color.RED);
                String v1="0";
                if(Cache.mapPD.get(String.valueOf(i))!=null ){
                    v1=String.valueOf(Cache.mapPD.get(String.valueOf(i)).getYgq());
                }
                tv1y.setText(v1);
                tv1y.setGravity(Gravity.CENTER);
                tv1y.setTextColor(Color.BLACK);
                tv1y.setTextSize(20);
                tv1y.setLayoutParams(params);
                rl.addView(tv1y);

                params = new RelativeLayout.LayoutParams(130, 80);
                params.setMargins(240, 150+i*100, 0, 0);
                TextView tv2y = new TextView(this);
                //tv2y.setBackgroundColor(Color.YELLOW);
                tv2y.setBackgroundColor(Color.argb(255,238,242,14));
                String v2="0";
                if(Cache.mapPD.get(String.valueOf(i))!=null){
                    v2=String.valueOf(Cache.mapPD.get(String.valueOf(i)).getJxq());
                }
                tv2y.setText(v2);
                tv2y.setTextColor(Color.BLACK);
                tv2y.setGravity(Gravity.CENTER);
                tv2y.setTextSize(20);
                tv2y.setLayoutParams(params);
                rl.addView(tv2y);

                params = new RelativeLayout.LayoutParams(130, 80);
                params.setMargins(370, 150+i*100, 0, 0);
                TextView tv3y = new TextView(this);
                //tv3y.setBackgroundColor(Color.BLUE);
                tv3y.setBackgroundColor(Color.argb(255,135,162,86));
                tv3y.setGravity(Gravity.CENTER);
                String v3="0";
                if(Cache.mapPD.get(String.valueOf(i))!=null){
                    v3=String.valueOf(Cache.mapPD.get(String.valueOf(i)).getYxq());
                }
                tv3y.setText(v3);
                tv3y.setTextColor(Color.BLACK);
                tv3y.setTextSize(20);
                tv3y.setLayoutParams(params);
                rl.addView(tv3y);

                params = new RelativeLayout.LayoutParams(130, 80);
                params.setMargins(500, 150+i*100, 0, 0);
                TextView tv4y = new TextView(this);
                String all=String.valueOf(Integer.valueOf(v1)+Integer.valueOf(v2)+Integer.valueOf(v3));
                titleShow=titleShow+Integer.valueOf(all);
                tv4y.setText(all);
                tv4y.setGravity(Gravity.CENTER);
                tv4y.setTextColor(Color.BLACK);
                tv4y.setTextSize(20);
                tv4y.setLayoutParams(params);
                rl.addView(tv4y);
            }
            tvtitle.setText("盘点结果 ("+titleShow+"个)");

        }catch(Exception e){
            logger.error("初始化界面出错",e);
        }
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
        Cache.mapPD.clear();
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
                //计算效期
                initXQ(Cache.HCCSMap.get(map.get("card").toString()).toString(),map.get("yxq").toString());
            }
            //标签在柜子中
            if(dealKeys.contains(map.get("card").toString())){
                if(!map.get("wz").toString().equals("0") && !Cache.HCCSMap.get(map.get("card").toString()).equals(map.get("wz").toString())){
                    //标签位置更换
                    mapSave.put(map.get("card").toString(),Cache.HCCSMap.get(map.get("card").toString()));
                    //计算效期
                    initXQ(Cache.HCCSMap.get(map.get("card").toString()).toString(),map.get("yxq").toString());
                }else if(Cache.HCCSMap.get(map.get("card").toString()).equals(map.get("wz").toString())){
                    //标签未动
                    initXQ(Cache.HCCSMap.get(map.get("card").toString()).toString(),map.get("yxq").toString());
                }
            }
        }
        //数据库更新内容
        Set<String> updatesKey=mapSave.keySet();
        for(String key : updatesKey){
            productDao.updateProductWZ(mapSave.get(key).toString(),key);
        }
        //初始化近效期图示
        sendJXQ();
    }
    private void initXQ(String wz,String xq){
        try{
            int ygq=0;
            int jxq=0;
            int yxq=0;
            long current = System.currentTimeMillis();
            long dt = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
            long t7=dt+1000*3600*24*7;
            if(Cache.mapPD.get(wz)==null){
                PDEntity pdEntity=new PDEntity();
                pdEntity.setJxq(0);
                pdEntity.setYgq(0);
                pdEntity.setYxq(0);
                Cache.mapPD.put(wz,pdEntity);
            }
            //已过期  小于当前时间
            //近效期  当天0点到7天后24点
            //远效期  8天后0点后
            if(Long.valueOf(xq)<dt){
                Cache.mapPD.get(wz).setYgq(Cache.mapPD.get(wz).getYgq()+1);
            }else if(Long.valueOf(xq)>=dt && Long.valueOf(xq)<t7){
                Cache.mapPD.get(wz).setJxq(Cache.mapPD.get(wz).getJxq()+1);
            }else if(Long.valueOf(xq)>=t7){
                Cache.mapPD.get(wz).setYxq(Cache.mapPD.get(wz).getYxq()+1);
            }

        }catch (Exception e){
            logger.error("初始化静效期出错",e);
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
     * @author dinghaoyang
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {

                case R.id.fh:
                    PDActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

}
