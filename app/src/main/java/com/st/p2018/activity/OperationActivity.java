package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.Product;
import com.st.p2018.entity.ProductQuery;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class OperationActivity extends Activity {

    //点击柱状图打开界面
    private SmartTable table;
    private TextView tvfh;
    private TextView tvtitle;
    private Logger logger = Logger.getLogger(this.getClass());
    private String ceng="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_operation);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        try{
            Intent intent = getIntent();
            ceng = intent.getStringExtra("ceng");
            initView();
            tvtitle.setText(ceng+"层");
            Column<String> column1 = new Column<>("品牌", "pp");
            Column<String> column2 = new Column<>("名称", "mc");
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            //Column<String> column4 = new Column<>("剩余天数", "yxrq");
            Column<String> column5 = new Column<>("位置(层)", "location");
            //Column<String> column6=new Column<String>("近效期","")

            List<Product> list=new ArrayList<>();
            if(Cache.mapTotal.get(ceng)!=null && Cache.mapTotal.get(ceng).getJxq()!=null){
                list.addAll(Cache.mapTotal.get(ceng).getJxq());
            }
            if(Cache.mapTotal.get(ceng)!=null && Cache.mapTotal.get(ceng).getQt()!=null){
                list.addAll(Cache.mapTotal.get(ceng).getQt());
            }

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("",list, column1, column2, column3,  column5);
            //设置数据
            table = findViewById(R.id.table);
            table.setTableData(tableData);

            table.getConfig().setShowXSequence(false);
            table.getConfig().setShowYSequence(false);
            table.getConfig().setShowTableTitle(false);
            table.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            table.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
            table.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
            table.getConfig().setContentBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {     //设置隔行变色
                @Override
                public int getBackGroundColor(CellInfo cellInfo) {
                    if (cellInfo.position<Cache.mapTotal.get(ceng).getJxq().size()) {
                        return ContextCompat.getColor(OperationActivity.this, R.color.jxqyellow);
                    } else {
                        return TableConfig.INVALID_COLOR;
                    }
                }
            });
        }catch (Exception e){
            logger.error("初始化时出错",e);
        }

    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);

    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.fh:
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
    private List<ProductQuery>  getdata(String yxqFlag) {
        List<ProductQuery> listRe = new ArrayList<ProductQuery>();
        try{
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

            for(HashMap mapD : list){
                ProductQuery pq=new ProductQuery();
                pq.setPp(mapD.get("pp").toString());
                pq.setType(mapD.get("zl").toString());
                pq.setGg(mapD.get("gg").toString());
                pq.setYxrq(sdf.format(new Date(Long.valueOf(mapD.get("yxq").toString()))));
                int sytsI=(int) (Long.valueOf(mapD.get("yxq").toString())-dt+1)/(24*3600*1000)-1;
                pq.setSyts(String.valueOf(sytsI));
                pq.setWz(mapD.get("wz")==null?"":mapD.get("wz").toString());
                listRe.add(pq);
            }
        }catch (Exception e){
            logger.error("从数据库查询数据出错",e);
        }

        return listRe;

    }

}
