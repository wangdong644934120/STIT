package com.st.p2018.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.st.p2018.dao.PZDao;
import com.st.p2018.device.DeviceCom;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;
import com.st.p2018.view.PercentCircle;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DeviceActivity extends Activity {

    private TextView gx;
    private CheckBox gc1;
    private CheckBox gc2;
    private CheckBox gc3;
    private CheckBox gc4;
    private CheckBox gc5;
    private CheckBox gc6;
    private TextView tvfh;
    private TextView tvtitle;
    private TextView tvcpxlh;
    private TextView tvyjbbh;
    private  TextView tvgjbbh;
    private TextView tvrjbbh;
    private Button btnChooseFile;
    private EditText etSJ;
    private String pathSJ="";
    private Button btnSJ;
    private byte[] bySJ;
    private TextView tvSJJD;
    private ProgressBar progressBar;
    private LinearLayout layoutSJProgress;
    private Logger logger = Logger.getLogger(this.getClass());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_device);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
        initData();

    }

    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("设备信息");
        gx=(TextView)findViewById(R.id.gx);
        gc1=(CheckBox)findViewById(R.id.cb1);
        gc1.setEnabled(false);
        gc2=(CheckBox)findViewById(R.id.cb2);
        gc2.setEnabled(false);
        gc3=(CheckBox)findViewById(R.id.cb3);
        gc3.setEnabled(false);
        gc4=(CheckBox)findViewById(R.id.cb4);
        gc4.setEnabled(false);
        gc5=(CheckBox)findViewById(R.id.cb5);
        gc5.setEnabled(false);
        gc6=(CheckBox)findViewById(R.id.cb6);
        gc6.setEnabled(false);

        if(gx.getText().equals("Ⅰ型")){
            gc6.setVisibility(View.VISIBLE);
        }else{
            gc6.setVisibility(View.INVISIBLE);
        }
        tvcpxlh=(TextView)findViewById(R.id.cpxlh);
        tvyjbbh=(TextView)findViewById(R.id.yjbbh);
        tvgjbbh=(TextView)findViewById(R.id.gjbbh);
        btnChooseFile=(Button)findViewById(R.id.btnchoosefile);
        btnChooseFile.setOnClickListener(new onClickListener());
        tvrjbbh=(TextView)findViewById(R.id.rjbbh);
        etSJ=(EditText)findViewById(R.id.etSJ);
        btnSJ=(Button)findViewById(R.id.btnsj);
        btnSJ.setOnClickListener(new onClickListener());
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        tvSJJD=(TextView)findViewById(R.id.tvsjjd);
        layoutSJProgress=(LinearLayout)findViewById(R.id.sjprogress);
        Cache.myHandleDevice = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                //提示信息
                if (bundle.getString("show") != null) {
                    initData();
                }
                if(bundle.getString("enable")!=null){
                    btnSJ.setEnabled(true);
                    btnChooseFile.setEnabled(true);
                }
                if(bundle.getString("jd")!=null){
                    tvSJJD.setText(bundle.getString("jd").toString()+"%");
                }
            }
        };

    }

    private void initData(){
        gx.setText(Cache.gx);
        gc1.setChecked(Cache.gcqy1);
        gc2.setChecked(Cache.gcqy2);
        gc3.setChecked(Cache.gcqy3);
        gc4.setChecked(Cache.gcqy4);
        gc5.setChecked(Cache.gcqy5);
        gc6.setChecked(Cache.gcqy6);
        tvcpxlh.setText(Cache.cpxlh);
        tvyjbbh.setText(Cache.yjbbh);
        tvgjbbh.setText(Cache.gjbbh);
        tvrjbbh.setText(Cache.apkversion);


    }

    public class onClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.isEnabled() == false)
                return;
            switch (v.getId()) {
                case R.id.btnchoosefile:
                    showBar();
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent,1);
                    break;
                case R.id.fh:
                    Cache.myHandleDevice=null;
                    DeviceActivity.this.finish();
                    break;
                case R.id.btnsj:
                    checkSJ();
                    break;
                default:
                    break;
            }
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        closeBar();
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    String path = getPath(DeviceActivity.this, uri);
                    if (path != null) {
                        File file = new File(path);
                        if (file.exists()) {
                            String upLoadFilePath = file.toString();
                            String upLoadFileName = file.getName();
                            pathSJ=upLoadFilePath;
                            logger.info("固件升级地址："+upLoadFilePath);
                            etSJ.setText(upLoadFileName);
                        }
                    }
                }
            }
        }
    }

    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");
                final String type = split[0];

                /*if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }*/

                return Environment.getExternalStorageDirectory() + "/" + split[1];

            }

            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }

        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 打开状态栏
     */
    private void showBar() {
        try {
            Process proc = Runtime.getRuntime().exec(new String[]{
                    "am", "startservice", "-n", "com.android.systemui/.SystemUIService"});
            proc.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭底部状态栏
     */
    private void closeBar() {
        try {
            //需要root 权限
            Build.VERSION_CODES vc = new Build.VERSION_CODES();
            Build.VERSION vr = new Build.VERSION();
            String ProcID = "79";

            if (vr.SDK_INT >= vc.ICE_CREAM_SANDWICH) {
                ProcID = "42"; //ICS AND NEWER
            }
            //需要root 权限
            Process proc = Runtime.getRuntime().exec(new String[]{"su", "-c", "service call activity " + ProcID + " s16 com.android.systemui"}); //WAS 79
            proc.waitFor();

        } catch (Exception ex) {

        }
    }

    /**
     * 开始升级检测
     */
    private void checkSJ(){
        if(pathSJ.equals("")){
            MyTextToSpeech.getInstance().speak("请选择升级文件");
            Toast.makeText(DeviceActivity.this, "请选择升级文件", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!pathSJ.toLowerCase().endsWith(".bin")){
            MyTextToSpeech.getInstance().speak("请选择正确的升级文件");
            Toast.makeText(DeviceActivity.this, "请选择正确的升级文件", Toast.LENGTH_SHORT).show();
            return;
        }
        //读取升级文件
        bySJ=InputStream2ByteArray(pathSJ);
        if(bySJ.length<=0){
            MyTextToSpeech.getInstance().speak("请选择正确的升级文件");
            Toast.makeText(DeviceActivity.this, "请选择正确的升级文件", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog alertDialog = new AlertDialog.Builder(DeviceActivity.this)
                .setTitle("确认提示框")
                .setMessage("确认固件升级？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        btnSJ.setEnabled(false);
                        btnChooseFile.setEnabled(false);
                        MyTextToSpeech.getInstance().speak("升级中请勿进行其他操作");
                        layoutSJProgress.setVisibility(View.VISIBLE);
                        new StartSJ().start();
                    }
                })

                .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }) .create();
        alertDialog.show();
    }

    class StartSJ extends  Thread{
        public void run(){

                try{
                    Cache.deviceCommunication=false;
                    boolean bl=false;
                    while(true){
                        bl = HCProtocol.ST_STARTSJ();
                        logger.info("下发升级开始指令返回："+bl);
                        if(bl){
                            progressBar.setProgress(1);
                            break;
                        }
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){
                        }
                    }

                    int time=0;
                    int alltime=0;
                    if(bySJ.length<=32){
                        alltime=1;
                    }else if(bySJ.length%32==0){
                        alltime=bySJ.length/32;
                    }else{
                        alltime=bySJ.length/32+1;
                    }
                    logger.info("共需要发送次数："+alltime);
                    //logger.info("升级文件内容："+Arrays.toString(bySJ));

                    for(int i=0;i<alltime;i++){
                        time=time+1;
                        //需要下发32字节
                        byte[] bydata=new byte[33];
                        //不够32字节需要0xff补充
                        for(int j=0;j<bydata.length;j++){
                            bydata[j]=(byte)0xff;
                        }
                        bydata[0]=(byte)time;
                        if(i==alltime-1){
                            System.arraycopy(bySJ,i*32,bydata,1,bySJ.length-i*32);
                        }else{
                            System.arraycopy(bySJ,i*32,bydata,1,32);
                        }
                        while(true){
                            //下发升级
                            //logger.info("第"+i+"次发送数据"+ Arrays.toString(bydata));
                            bl=HCProtocol.ST_NOWSJ(bydata);
                            if(bl){
                                double di=i;
                                double bfd=di*100/alltime;
                                int bf=(int)bfd;
                                if(bf>=100){
                                    bf=99;
                                }
                                progressBar.setProgress(bf);
                                sendJD(bf);
                                break;
                            }else{
                                logger.info("第"+(i+1)+"次发送数据无应答，重新发送"+ Arrays.toString(bydata));

                            }
                            try{
                                Thread.sleep(2000);
                            }catch (Exception e){

                            }

                        }

                    }
                    while(true){
                        bl = HCProtocol.ST_ENDSJ();
                        logger.info("下发升级结束指令返回："+bl);
                        if(bl){
                            Thread.sleep(3000);
                            progressBar.setProgress(100);
                            sendJD(100);
                            /*percentCircle.setTargetPercent(100);
                            percentCircle.update();*/
                            MyTextToSpeech.getInstance().speak("升级成功");

                            reStartDataThread();
                            break;
                        }
                        try{
                            Thread.sleep(1000);
                        }catch (Exception e){

                        }
                    }
                }catch (Exception e){
                    logger.error("执行固件升级出错",e);
                }

        }
    }

    private byte[] InputStream2ByteArray(String filePath)  {
        byte[] data;
        try{
            InputStream in = new FileInputStream(filePath);
            data = toByteArray(in);
            in.close();
        }catch (Exception e){
            logger.error("读取升级文件时出错",e);
            return null;
        }

        return data;
    }

    private byte[] toByteArray(InputStream in)  {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try{
            byte[] buffer = new byte[1024 * 4];
            int n = 0;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
        }catch (Exception e){
            logger.error("读取升级文件时出错",e);
        }

        return out.toByteArray();
    }

    /**
     * 重新启动通信线程
     */
    private void reStartDataThread(){
        Message message = Message.obtain(Cache.myHandleDevice);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("enable","1");
        message.setData(data);
        Cache.myHandleDevice.sendMessage(message);

        Cache.deviceCommunication=true;
        new DeviceCom().start();
    }

    private void sendJD(int jd){
        Message message = Message.obtain(Cache.myHandleDevice);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("jd",String.valueOf(jd));
        message.setData(data);
        Cache.myHandleDevice.sendMessage(message);
    }
}
