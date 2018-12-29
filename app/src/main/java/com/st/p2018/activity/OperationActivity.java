package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
    private Button btnClose;
    private LinearLayout mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operation);
        //mContentView = (LinearLayout) findViewById(R.id.contentView);
        Intent intent = getIntent();
        String yxqFlag = intent.getStringExtra("yxq");
        String title = intent.getStringExtra("title");
        initView();
        this.setTitle(title);
        initGridHeader();// 初始表头
        initQueryGrid(yxqFlag);// 初始查询结果表格


        //-------------
//        initDisplayOpinion();
//
//        //构造假数据
//        ArrayList<ArrayList<String>> mTableDatas = new ArrayList<ArrayList<String>>();
//        ArrayList<String> mfristData = new ArrayList<String>();
//        mfristData.add("标题");
//        for (int i = 0; i < 10; i++) {
//            mfristData.add("标题" + i);
//        }
//        mTableDatas.add(mfristData);
//        for (int i = 0; i < 20; i++) {
//            ArrayList<String> mRowDatas = new ArrayList<String>();
//            mRowDatas.add("标题" + i);
//            for (int j = 0; j < 10; j++) {
//                mRowDatas.add("数据" + j);
//            }
//            mTableDatas.add(mRowDatas);
//        }
//        final LockTableView mLockTableView = new LockTableView(this, mContentView, mTableDatas);
//        //Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
//        mLockTableView.setLockFristColumn(true) //是否锁定第一列
//                .setLockFristRow(true) //是否锁定第一行
//                .setMaxColumnWidth(100) //列最大宽度
//                .setMinColumnWidth(60) //列最小宽度
//                .setColumnWidth(1,30) //设置指定列文本宽度
//                .setColumnWidth(2,20)
//                .setMinRowHeight(20)//行最小高度
//                .setMaxRowHeight(60)//行最大高度
//                .setTextViewSize(16) //单元格字体大小
//                .setFristRowBackGroudColor(R.color.table_head)//表头背景色
//                .setTableHeadTextColor(R.color.beijin)//表头字体颜色
//                .setTableContentTextColor(R.color.border_color)//单元格字体颜色
//                .setCellPadding(15)//设置单元格内边距(dp)
//                .setNullableString("N/A") //空值替换值
//                .setTableViewListener(new LockTableView.OnTableViewListener() {
//                    @Override
//                    public void onTableViewScrollChange(int x, int y) {
////                        Log.e("滚动值","["+x+"]"+"["+y+"]");
//                    }
//                })//设置横向滚动回调监听
//                .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
//                    @Override
//                    public void onLeft(HorizontalScrollView view) {
//
//                        //Log.e("滚动边界","滚动到最左边");
//                    }
//
//                    @Override
//                    public void onRight(HorizontalScrollView view) {
//
//                        //Log.e("滚动边界","滚动到最右边");
//                    }
//                })//设置横向滚动边界监听
//                .setOnLoadingListener(new LockTableView.OnLoadingListener() {
//                    @Override
//                    public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
//                        //Log.e("onRefresh",Thread.currentThread().toString());
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
////                                Log.e("现有表格数据", mTableDatas.toString());
//                                //构造假数据
//                                ArrayList<ArrayList<String>> mTableDatas = new ArrayList<ArrayList<String>>();
//                                ArrayList<String> mfristData = new ArrayList<String>();
//                                mfristData.add("标题");
//                                for (int i = 0; i < 10; i++) {
//                                    mfristData.add("标题" + i);
//                                }
//                                mTableDatas.add(mfristData);
//                                for (int i = 0; i < 20; i++) {
//                                    ArrayList<String> mRowDatas = new ArrayList<String>();
//                                    mRowDatas.add("标题" + i);
//                                    for (int j = 0; j < 10; j++) {
//                                        mRowDatas.add("数据" + j);
//                                    }
//                                    mTableDatas.add(mRowDatas);
//                                }
//                                mLockTableView.setTableDatas(mTableDatas);
//                                mXRecyclerView.refreshComplete();
//                            }
//                        }, 1000);
//                    }
//
//                    @Override
//                    public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
//                        // Log.e("onLoadMore",Thread.currentThread().toString());
//                        Handler handler = new Handler();
//                        handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mTableDatas.size() <= 60) {
//                                    for (int i = 0; i < 10; i++) {
//                                        ArrayList<String> mRowDatas = new ArrayList<String>();
//                                        mRowDatas.add("标题" + (mTableDatas.size() - 1));
//                                        for (int j = 0; j < 10; j++) {
//                                            mRowDatas.add("数据" + j);
//                                        }
//                                        mTableDatas.add(mRowDatas);
//                                    }
//                                    mLockTableView.setTableDatas(mTableDatas);
//                                } else {
//                                    mXRecyclerView.setNoMore(true);
//                                }
//                                mXRecyclerView.loadMoreComplete();
//                            }
//                        }, 1000);
//                    }
//                })
//                .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
//                    @Override
//                    public void onItemClick(View item, int position) {
//                        //Log.e("点击事件",position+"");
//                    }
//                })
//                .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
//                    @Override
//                    public void onItemLongClick(View item, int position) {
//                        //Log.e("长按事件",position+"");
//                    }
//                })
//                .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
//                .show(); //显示表格,此方法必须调用
//        mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
//        mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
//        mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.SquareSpin);
//        //属性值获取
//        // Log.e("每列最大宽度(dp)", mLockTableView.getColumnMaxWidths().toString());
//        //Log.e("每行最大高度(dp)", mLockTableView.getRowMaxHeights().toString());
//        //Log.e("表格所有的滚动视图", mLockTableView.getScrollViews().toString());
//        //Log.e("表格头部固定视图(锁列)", mLockTableView.getLockHeadView().toString());
//        // Log.e("表格头部固定视图(不锁列)", mLockTableView.getUnLockHeadView().toString());
//        //---------------

    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
        listHeaders = (ListView) findViewById(R.id.listHeaders);
        listResults = (ListView) findViewById(R.id.listResults);
        btnClose=(Button)findViewById(R.id.close);
        btnClose.setOnClickListener(new onClickListener());
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

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.close:
                    OperationActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

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

//    private void initDisplayOpinion() {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        DisplayUtil.density = dm.density;
//        DisplayUtil.densityDPI = dm.densityDpi;
//        DisplayUtil.screenWidthPx = dm.widthPixels;
//        DisplayUtil.screenhightPx = dm.heightPixels;
//        DisplayUtil.screenWidthDip = DisplayUtil.px2dip(getApplicationContext(), dm.widthPixels);
//        DisplayUtil.screenHightDip = DisplayUtil.px2dip(getApplicationContext(), dm.heightPixels);
//    }
}
