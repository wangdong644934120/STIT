package com.st.p2018.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.st.p2018.entity.Product;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AccessConActivity extends Activity {

    private TextView tvfh;
    private TextView tvtitle;
    private SmartTable tableSave;
    private SmartTable tableOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_access_con);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();

    }

    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("存取确认");
        initSave();
        initOut();
  /*      Cache.myHandleAccess= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("opera") != null) {
                    initSave();
                    initOut();
                }


            }
        };*/

    }

    private void initSave(){
        Column<String> column1 = new Column<>("品牌", "pp");
        Column<String> column2 = new Column<>("名称", "mc");
        Column<String> column3 = new Column<>("效期批次", "xqpc");
        Column<String> column4 = new Column<>("剩余天数", "yxrq");
        Column<String> column5 = new Column<>("所在位置", "szwz");

        /*List<Product> list =new ArrayList<Product>();
        for(int i=0;i<3;i++){
            Product product=new Product();
            product.setMc("导管");
            product.setPp("波士顿");
            product.setYxrq("2020-10-10");
            product.setXqpc("xiqoqipici123456");
            product.setSzwz("1");
            list.add(product);

        }*/
        //表格数据 datas是需要填充的数据
        TableData<Product> tableData = new TableData<Product>("", Cache.listOperaSave, column1, column2, column3, column4, column5);
        //设置数据
        tableSave = findViewById(R.id.tablesave);
        tableSave.setTableData(tableData);
        tableSave.getConfig().setShowXSequence(false);
        tableSave.getConfig().setShowYSequence(false);
        tableSave.getConfig().setShowTableTitle(false);
        tableSave.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        tableSave.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        tableSave.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
    }

    private void initOut(){
        Column<String> column1 = new Column<>("品牌", "pp");
        Column<String> column2 = new Column<>("名称", "mc");
        Column<String> column3 = new Column<>("效期批次", "xqpc");
        Column<String> column4 = new Column<>("剩余天数", "yxrq");
        Column<String> column5 = new Column<>("所在位置", "szwz");

      /*  List<Product> list =new ArrayList<Product>();
        for(int i=0;i<150;i++){
            Product product=new Product();
            product.setMc("导管");
            product.setPp("波士顿");
            product.setYxrq("2020-10-10");
            product.setXqpc("xiqoqipici123456");
            product.setSzwz("1");
            list.add(product);

        }*/
        //表格数据 datas是需要填充的数据
        TableData<Product> tableData = new TableData<Product>("", Cache.listOperaOut, column1, column2, column3, column4, column5);
        //设置数据
        tableOut = findViewById(R.id.tableout);
        tableOut.setTableData(tableData);
        tableOut.getConfig().setShowXSequence(false);
        tableOut.getConfig().setShowYSequence(false);
        tableOut.getConfig().setShowTableTitle(false);
        tableOut.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        tableOut.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        tableOut.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
    }

    /**
     * 单击事件监听
     *
     * @author dinghaoyang
     *
     */
    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {

                case R.id.fh:
                    AccessConActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }
}
