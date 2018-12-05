package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.st.p2018.entity.PersonAdapter;
import com.st.p2018.entity.PersonInfo;
import com.st.p2018.entity.ProductAdapter;
import com.st.p2018.entity.ProductBar;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProductActivity extends Activity {

    private ListView listHeaders;// 表头ListView
    private ListView listResults;// //查询结果listview
    private List<HashMap<String, String>> mQueryData = new ArrayList<HashMap<String, String>>() ;
    private String type;
    private String  time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        Intent intent = getIntent();
        type=intent.getStringExtra("type");
        time=intent.getStringExtra("time");
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
        map.put("gg", "规格");
        map.put("yxq", "有效期");
        map.put("yxts", "有效天数");
        map.put("cfwz", "存放位置");

        data.add(map);
        ProductAdapter adapter = new ProductAdapter(this, data);
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
        ProductAdapter adapter = new ProductAdapter(this, mQueryData);
        listResults.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    /**
     * 从数据库查询数据
     *
     * @return
     */
    private void getdata() {
//        List<ProductBar> list=Cache.getProduct().get(time).get(type);
//        for(ProductBar pb : list){
//            HashMap<String ,String> map = new HashMap<>();
//            map.put("pp",pb.getPp());
//            map.put("gg",pb.getGg());
//            map.put("yxq",pb.getYxq());
//            map.put("yxts",pb.getYxts());
//            map.put("cfwz","第1层");
//            mQueryData.add(map);
//        }

    }







}
