package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.OperationAdapter;
import com.st.p2018.entity.ProductAdapter;
import com.st.p2018.entity.ProductBar;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class OperationActivity extends Activity {

    private ListView listHeaders;// 表头ListView
    private ListView listResults;// //查询结果listview
    private List<HashMap<String, String>> mQueryData = new ArrayList<HashMap<String, String>>() ;
    private String type;
    private String  time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);

        Intent intent = getIntent();
        String yxqFlag = intent.getStringExtra("yxq");
        String title = intent.getStringExtra("title");
        initView();
        this.setTitle(title);

        initGridHeader();// 初始表头
        initQueryGrid(yxqFlag);// 初始查询结果表格
    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
        listHeaders = (ListView) findViewById(R.id.listHeaders);
        listResults = (ListView) findViewById(R.id.listResults);
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
        map.put("yxq", "有效日期");
        map.put("syts", "剩余天数");
        map.put("wz","位置");
        data.add(map);
        OperationAdapter adapter = new OperationAdapter(this, data);
        listHeaders.setAdapter(adapter);
    }

    /**
     * 初始查询结果表格
     */
    private void initQueryGrid(String yxqFlag) {
        getdata(yxqFlag);
        OperationAdapter adapter = new OperationAdapter(this, mQueryData);
        listResults.setAdapter(adapter);

    }

    /**
     * 从数据库查询数据
     *
     * @return
     */
    private void getdata(String yxqFlag) {
        ProductDao productDao = new ProductDao();
        List<HashMap<String,String>> list=new ArrayList<HashMap<String,String>>();

        long current = System.currentTimeMillis();
        long dt = current/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        long t7=dt+1000*3600*24*7;

        if(yxqFlag.contains("已过期")){
            HashMap map = new HashMap();
            map.put("yxq",dt);
            list = productDao.getYGQProduct(map);
        }else if(yxqFlag.contains("近效期")){
            HashMap map = new HashMap();
            map.put("yxq1",dt);
            map.put("yxq2",t7);
            list=productDao.getJXQProduct(map);

        }else if(yxqFlag.contains("远效期")){
            HashMap map = new HashMap();
            map.put("yxq",t7);
            list=productDao.getYXQProduct(map);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        for(int i=0;i<50;i++)
        for(HashMap mapD : list){

            HashMap<String ,String> map = new HashMap<>();
            map.put("pp",mapD.get("pp").toString());
            map.put("type",mapD.get("zl").toString());
            map.put("gg",mapD.get("gg").toString());
            map.put("yxq",sdf.format(new Date(Long.valueOf(mapD.get("yxq").toString()))));
            int sytsI=(int) (Long.valueOf(mapD.get("yxq").toString())-dt+1)/(24*3600*1000)-1;
            map.put("syts",String.valueOf(sytsI));
            map.put("wz",mapD.get("wz")==null?"":mapD.get("wz").toString());
            mQueryData.add(map);
        }
//        List<ProductBar> list= Cache.getProduct().get("1天-7天").get("导管");
//        for(ProductBar pb : list){
//            HashMap<String ,String> map = new HashMap<>();
//            map.put("pp",pb.getPp());
//            map.put("type",pb.getType());
//            map.put("gg",pb.getGg());
//            map.put("operation","存放");
//            map.put("cfwz","第1层");
//            mQueryData.add(map);
//        }

    }

}
