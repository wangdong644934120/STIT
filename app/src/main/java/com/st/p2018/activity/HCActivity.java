package com.st.p2018.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.st.p2018.dao.ProductDao;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.ExpportDataBeExcel;


import java.io.File;
import java.util.HashMap;
import java.util.List;

public class HCActivity extends Activity {

    private Button btnUP;
    private Button btnCS;
    private Button btnDown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hc);
        initView();
    }
    private void initView(){
        btnUP=(Button)findViewById(R.id.up);
        btnUP.setOnClickListener(new onClickListener());
        btnCS=(Button)findViewById(R.id.cs);
        btnCS.setOnClickListener(new onClickListener());
        btnDown=(Button)findViewById(R.id.down);
        btnDown.setOnClickListener(new onClickListener());
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
                    btnCS.setPressed(true);
                    ProductDao productDao = new ProductDao();
                    productDao.clearAllProduct();
                    productDao.addMutilAllProduct();
                    //todo替换该方法
                    productDao.updateAllProductWZ();
                    Toast.makeText(HCActivity.this, "初始柜内耗材完成", Toast.LENGTH_SHORT).show();
                    btnCS.setPressed(false);
                    break;
                default:
                    break;
            }
        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            if (requestCode == 1) {
//                Uri uri = data.getData();
//                String docId = DocumentsContract.getDocumentId(uri);
//              //591c-1504
////                String path=uri.getPath().toString();
//                String path=FileUtils.getFilePathByUri(HCActivity.this,uri);
//                Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
//                System.out.println(path);
//                File file = new File(path);
//                if(file.exists()){
//                    System.out.println("cunzai");
//                }else{
//                    System.out.println("bucunzai");
//                }
//            }
//        }
//
//    }

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



}
