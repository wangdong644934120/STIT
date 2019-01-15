package com.st.p2018.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.Column;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import java.util.List;


/**
 * Created by Administrator on 2018/12/17.
 */

public class RecordActivity extends Activity {


    //关门后自动盘点返回界面
    private Button btnClose;
    private Handler myHandler;
    CloseThread closeThread=null;
    private SmartTable table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        initView();
        this.setTitle("存放记录");

        Column<String> column1 = new Column<>("品牌", "pp");
        Column<String> column2 = new Column<>("种类", "type");
        Column<String> column3 = new Column<>("规格", "gg");
        Column<String> column4 = new Column<>("操作", "cz");
        Column<String> column5 = new Column<>("位置", "wz");

        List<ProductRecord> list =Cache.listPR;
        //表格数据 datas是需要填充的数据
        TableData<ProductRecord> tableData = new TableData<ProductRecord>("", list, column1, column2, column3, column4, column5);
        //设置数据
        table = findViewById(R.id.table);
        table.setTableData(tableData);
        table.getConfig().setShowXSequence(false);
        table.getConfig().setShowYSequence(false);
        table.getConfig().setShowTableTitle(false);
        table.getConfig().setColumnTitleBackgroundColor(Color.BLUE);
        table.getConfig().setColumnTitleStyle(new FontStyle(20,Color.WHITE));
        table.getConfig().setContentStyle(new FontStyle(18,Color.BLACK));
        closeThread =new CloseThread();
        closeThread.start();

    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
        btnClose=(Button)findViewById(R.id.close);
        btnClose.setOnClickListener(new onClickListener());
        myHandler= new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("value") != null) {
                    btnClose.setText(bundle.getString("value"));
                }
            }};
    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.close:
                    closeThread.close();
                    RecordActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }


    class CloseThread extends Thread{
        int i=10;
        public void run(){
            if(Cache.listPR.size()==0){
                i=5;
            }else if(Cache.listPR.size()<=5){
                i=10;
            }else{
                i=Cache.listPR.size()*2;
            }
            for(;i>0;i--){
                Message message = Message.obtain(myHandler);
                Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                data.putString("value","关闭("+i+"s)");
                message.setData(data);
                myHandler.sendMessage(message);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                }
            }
            RecordActivity.this.finish();
        }

        public void close(){
            i=0;
        }
    }


}
