package com.st.p2018.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.st.p2018.entity.OperationAdapter;
import com.st.p2018.entity.ProductAdapter;
import com.st.p2018.entity.ProductBar;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        initView();

        initGridHeader();// 初始表头
        initQueryGrid();// 初始查询结果表格
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
        map.put("operation", "操作");
        map.put("cfwz", "存取位置");

        data.add(map);
        OperationAdapter adapter = new OperationAdapter(this, data);
        listHeaders.setAdapter(adapter);
    }

    /**
     * 初始查询结果表格
     */
    private void initQueryGrid() {
        getdata();
//        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("id", "id");
//        map.put("name", "姓名");
//        map.put("code", "编号");
//        map.put("iszw", "指纹录取");
//        map.put("tzz", "指纹特征值");
//
//        data.add(map);
        OperationAdapter adapter = new OperationAdapter(this, mQueryData);
        listResults.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    /**
     * 从数据库查询数据
     *
     * @return
     */
    private void getdata() {
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
