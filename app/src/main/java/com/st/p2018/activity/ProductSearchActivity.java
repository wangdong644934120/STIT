package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.st.p2018.entity.Product;

import com.st.p2018.stit.R;
import com.st.p2018.util.CacheSick;
import org.apache.log4j.Logger;


public class ProductSearchActivity extends Activity {

    private SmartTable table;
    private TextView tvfh;
    private TextView tvtitle;
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_product_search);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
       /* try{
            initView();
            tvtitle.setText("关联耗材");
            Column<String> column1 = new Column<>("品牌", "pp");
            Column<String> column2 = new Column<>("名称", "mc");
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            Column<String> column4 = new Column<>("耗材EPC", "epc");

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("", CacheSick.listEP, column1, column2, column3,column4);
            //设置数据
            table = findViewById(R.id.table);
            table.setTableData(tableData);
            table.getConfig().setShowXSequence(false);
            table.getConfig().setShowYSequence(false);
            table.getConfig().setShowTableTitle(false);
            table.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            table.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
            table.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        }catch (Exception e){
            logger.error("初始化时出错",e);
        }*/

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
                    ProductSearchActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    }

}
