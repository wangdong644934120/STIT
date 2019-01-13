package com.st.p2018.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.st.p2018.dao.ProductDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.ExpportDataBeExcel;


import org.apache.log4j.Logger;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class HCActivity extends Activity {

    private Button btnUP;
    private Button btnCS;
    private Button btnDown;
    private TextView tvfh;
    private TextView tvtitle;
    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_hc);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
    }
    @Override
    protected void onDestroy(){
        Cache.getHCCS=0;
        super.onDestroy();
    }
    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("耗材管理");
        btnUP=(Button)findViewById(R.id.up);
        btnUP.setOnClickListener(new onClickListener());
        btnCS=(Button)findViewById(R.id.cs);
        btnCS.setOnClickListener(new onClickListener());
        btnDown=(Button)findViewById(R.id.down);
        btnDown.setOnClickListener(new onClickListener());
        Cache.myHandleHCCS = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("cshc") != null) {
                    logger.info("初始耗材获取标签原始个数："+Cache.HCCSMap.size());
                    ProductDao productDao = new ProductDao();
                    productDao.clearAllProduct();
                    if(!Cache.HCCSMap.isEmpty()){
                        List<HashMap<String,String>> list=productDao.getAllProductByHCCS();
                        if(list!=null && !list.isEmpty()){
                            Set<String> cards = Cache.HCCSMap.keySet();
                            for(String card : cards){
                                for(HashMap<String,String> map : list){
                                    if(card.toUpperCase().equals(map.get("card").toString().toUpperCase())){
                                        map.put("wz",Cache.HCCSMap.get(card).toString());
                                        break;
                                    }
                                }
                            }
                            productDao.updateAllProductWZ(list);
                        }

                    }
                    sendJXQ();
                    Toast.makeText(HCActivity.this, "初始柜内耗材完成,个数："+Cache.HCCSMap.size(), Toast.LENGTH_SHORT).show();
                    Cache.HCCSMap.clear();
                    Cache.getHCCS=0;
                }

            }
        };
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
                case R.id.up:
                    btnUP.setPressed(true);
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator+"STIT"+File.separator+"1.xls");
                    if(!file.exists()){
                        Toast.makeText(HCActivity.this, "未找到1.xls", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        boolean bl=ExpportDataBeExcel.ImportExcelData(file);
                        if(bl){
                            Toast.makeText(HCActivity.this, "上传耗材库成功", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(HCActivity.this, "上传耗材库失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    btnUP.setPressed(false);
                    break;

                case R.id.down:
                    btnDown.setPressed(true);
                    File fileout = new File(Environment.getExternalStorageDirectory()+File.separator+"STIT"+File.separator+"down.xls");
                    boolean bl=ExpportDataBeExcel.saveExcel(fileout);
                    if(bl){
                        Toast.makeText(HCActivity.this, "下载耗材库成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(HCActivity.this, "下载耗材库失败", Toast.LENGTH_SHORT).show();
                    }
                    btnDown.setPressed(false);
                    break;
                case R.id.cs:
                    Cache.getHCCS=1;
                    btnCS.setPressed(true);
                    btnCS.setPressed(false);

                    //下发盘点指令
                    if(HCProtocol.ST_GetAllCard()){

                        logger.info("下发指令盘存所有成功");
                    }else{
                        Cache.getHCCS=0;
                        logger.info("下发指令盘存所有失败");
                    }
                    break;
                case R.id.fh:
                    HCActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }


    /**
     * 根据Uri获取真实图片路径
     * <p/>
     * 一个android文件的Uri地址一般如下：
     * content://media/external/images/media/62026
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
//            MediaStore.Images.ImageColumns.DATA
            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
            Cursor cursor = context.getContentResolver().query( uri, filePathColumn, null, null, null );
//            if ( null != cursor ) {
//                if ( cursor.moveToFirst() ) {
//                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
//                    if ( index > -1 ) {
//                        data = cursor.getString( index );
//                    }
//                }
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            data = cursor.getString(columnIndex);
             cursor.close();
            }


        return data;
    }

    private  void sendJXQ(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("initJXQ","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

}
