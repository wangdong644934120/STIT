package com.st.p2018.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.listener.OnColumnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.st.p2018.entity.Product;
import com.st.p2018.external.SocketClient;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.CacheSick;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccessConLocalActivity extends Activity {
    private TextView tvfh;
    private TextView tvtitle;
    private SmartTable tableSave;
    private SmartTable tableOut;
    private TextView tvSaveCount;
    private TextView tvOutCount;
    private Button btnClose;
    private Logger logger = Logger.getLogger(this.getClass());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_access_con_local);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
    }

    private void initView(){
        try{
            tvfh=(TextView)findViewById(R.id.fh);
            tvfh.setOnClickListener(new onClickListener());
            tvtitle=(TextView)findViewById(R.id.title);
            tvtitle.setText("存取确认");
            tvSaveCount=(TextView)findViewById(R.id.savecount);
            tvOutCount=(TextView)findViewById(R.id.outcount);
            btnClose=(Button)findViewById(R.id.btnclose);
            btnClose.setOnClickListener(new onClickListener());

            initOut();
            initSave();
        }catch (Exception e){
            logger.error("初始化view出错",e);
        }
    }

    /**
     * 初始化存操作耗材
     */
    private void initSave() {
        try {
            tvSaveCount.setText("共存放"+Cache.listOperaSave.size()+"个");
            tableSave = findViewById(R.id.tablesave);
            Column<String> column1 = new Column<>("品牌", "pp");
            Column<String> column2 = new Column<>("名称", "mc");
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            Column<String> column5 = new Column<>("所在位置", "location");

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("", Cache.listOperaSave, column1, column2, column3, column5);
            //设置数据

            tableSave.setTableData(tableData);
            tableSave.getConfig().setShowXSequence(false);
            tableSave.getConfig().setShowYSequence(false);
            tableSave.getConfig().setShowTableTitle(false);
            tableSave.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            tableSave.getConfig().setColumnTitleStyle(new FontStyle(20, Color.WHITE));
            tableSave.getConfig().setContentStyle(new FontStyle(18, Color.BLACK));

        } catch (Exception e) {
            logger.error("初始化耗材存放出错",e);
        }
    }

    /**
     * 初始化取操作耗材
     */
    private void initOut(){
        try{
            tvOutCount.setText("共取出"+Cache.listOperaOut.size()+"个");
            Column<String> column1 = new Column<>("品牌", "pp");
            Column<String> column2 = new Column<>("名称", "mc");
            Column<String> column3 = new Column<>("效期批次", "xqpc");
            Column<String> column5 = new Column<>("所在位置", "location");

            //表格数据 datas是需要填充的数据
            TableData<Product> tableData = new TableData<Product>("", Cache.listOperaOut, column1, column2, column3,  column5);
            //设置数据
            tableOut = findViewById(R.id.tableout);
            tableOut.setTableData(tableData);
            tableOut.getConfig().setShowXSequence(false);
            tableOut.getConfig().setShowYSequence(false);
            tableOut.getConfig().setShowTableTitle(false);
            tableOut.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
            tableOut.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
            tableOut.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));

        }catch (Exception e){
            logger.error("初始化取操作耗材数据出错",e);
        }

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
                case R.id.btnclose:
                    btnClose.setPressed(true);
                    AccessConLocalActivity.this.finish();
                    btnClose.setPressed(false);
                    break;
                case R.id.fh:
                    AccessConLocalActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }


}
