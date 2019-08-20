package com.st.p2018.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.st.p2018.dao.PZDao;
import com.st.p2018.device.DataTypeChange;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.InputFilterMinMa;
import com.st.p2018.util.MyTextToSpeech;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class PZActivity extends Activity {

    private CheckBox gc1;
    private CheckBox gc2;
    private CheckBox gc3;
    private CheckBox gc4;
    private CheckBox gc5;
    private CheckBox gc6;
    private SeekBar sb;
    private Spinner spDK;
    private Button btnOK;
    private Button btnSave;
    private int dl;
    private Spinner spPD;
    private Spinner spGX;
    private EditText edpdcs;
    private TextView tvfh;
    private TextView tvtitle;
    private TextView tvxtmc;
    private TextView tvxtbh;
    private EditText tvpdjg;
    private EditText edfwqdz;
    private EditText edfwqdkh;
    private Spinner spLockScreen;
    private Spinner spChooseSick;
    private byte[] bysblx=new byte[1]; //设备类型
    private byte[] bycpxlh=new byte[6]; //产品序列号
    private byte[] byyjbbh=new byte[1]; //硬件版本号
    private byte[] bygjbbh=new byte[1];//固件版本号

    private Logger logger = Logger.getLogger(this.getClass());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        getDataFromDevice();
        initView();
        initData();
    }

    private void getDataFromDevice() {
        try{
            byte[] byDevice = HCProtocol.ST_GetDeviceInfo();
            if (byDevice.length == 0) {
                logger.info("打开配置界面时获取设备信息无返回数据");
                Toast.makeText(this, "获取设备信息失败", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("获取设备信息失败");
                return;
            }
            JXDevice(byDevice);
            boolean bl = HCProtocol.ST_GetWorkModel();
            if (bl) {
                if(bl){
                    String zmd="";
                    if(Cache.zmd==0){
                        zmd="灯自动";
                    }else if(Cache.zmd==1){
                        zmd="灯常开";
                    }else if(Cache.zmd==2){
                        zmd="灯常关";
                    }
                    logger.info("状态:照明灯:"+zmd);
                    logger.info("状态:盘存方式:"+(Cache.pc==0?"全部盘存":"触发盘存"));
                    logger.info("状态:盘存次数:"+Cache.pccs);
                }else{
                    logger.info("报警:获取工作模式失败");
                }
            }else{
                Toast.makeText(this, "获取工作模式失败", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("获取工作模式失败");
            }
        }catch (Exception e){
            logger.error("从设备获取数据出错",e);
        }

    }

    private void JXDevice(byte[] data){
        if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x11
                && data[3] == (byte) 0x05  ) {
            bysblx[0]=data[4];
            bycpxlh[0]=data[5];
            bycpxlh[1]=data[6];
            bycpxlh[2]=data[7];
            bycpxlh[3]=data[8];
            bycpxlh[4]=data[9];
            bycpxlh[5]=data[10];
            byyjbbh[0]=data[11];
            bygjbbh[0]=data[12];

            String gx="Ⅰ型";
            if(data[4]==0x01){
                //1型柜
                gx="Ⅰ型";
            }else if(data[4]==0x02){
                //11型柜
                gx="Ⅱ型";
                Cache.gx=gx;
            }

//            00000111
            String qygc= DataTypeChange.getBit(data[13]);
            Cache.gcqy1=(qygc.substring(7,8).equals("1"))?true:false;
            Cache.gcqy2=(qygc.substring(6,7).equals("1"))?true:false;
            Cache.gcqy3=(qygc.substring(5,6).equals("1"))?true:false;
            Cache.gcqy4=(qygc.substring(4,5).equals("1"))?true:false;
            Cache.gcqy5=(qygc.substring(3,4).equals("1"))?true:false;
            Cache.gcqy6=(qygc.substring(2,3).equals("1"))?true:false;
            logger.info("状态:柜体型号："+gx);
            logger.info("状态:柜层启用(最后为第一层抽)："+qygc);

        }else{
            Toast.makeText(this, "获取设备信息失败", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("获取设备信息失败");
        }

    }

    private void initView(){
        tvfh=(TextView)findViewById(R.id.fh);
        tvfh.setOnClickListener(new onClickListener());
        tvtitle=(TextView)findViewById(R.id.title);
        tvtitle.setText("配置管理");
        sb=(SeekBar) findViewById(R.id.sb);
        tvxtmc=(TextView)findViewById(R.id.xtmc);
        tvxtbh=(TextView)findViewById(R.id.xtbh);
        tvpdjg=(EditText)findViewById(R.id.pdjg);
        edfwqdz=(EditText)findViewById(R.id.fwqdz);
        edfwqdkh=(EditText)findViewById(R.id.fwqdkh);
        spLockScreen=(Spinner)findViewById(R.id.splockscreen);
        spChooseSick=(Spinner)findViewById(R.id.choosesick);
        //tvpdjg.setFilters(new InputFilter[]{ new InputFilterMinMa("5", "255")});
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //设置屏幕的亮度
                setScreenBrightness(seekBar.getProgress());
                dl=seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        spDK=(Spinner)findViewById(R.id.spdk);
        btnOK=(Button)findViewById(R.id.btnok);
        btnOK.setOnClickListener(new onClickListener());
        btnSave=(Button)findViewById(R.id.btnsave);
        btnSave.setOnClickListener(new onClickListener());
        spPD=(Spinner)findViewById(R.id.sppd);
        edpdcs=(EditText)findViewById(R.id.pdcs);

        gc1=(CheckBox)findViewById(R.id.cb1);
        gc2=(CheckBox)findViewById(R.id.cb2);
        gc3=(CheckBox)findViewById(R.id.cb3);
        gc4=(CheckBox)findViewById(R.id.cb4);
        gc5=(CheckBox)findViewById(R.id.cb5);
        gc6=(CheckBox)findViewById(R.id.cb6);
        if(Cache.gx.equals("Ⅰ型")){
            gc6.setVisibility(View.INVISIBLE);
        }else{
            gc6.setVisibility(View.VISIBLE);
        }
        spGX=(Spinner)findViewById(R.id.spgx);
        spGX.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spGX.getSelectedItem().toString().equals("Ⅰ型")){
                    gc6.setVisibility(View.INVISIBLE);
                    gc6.setChecked(false);
                }else{
                    gc6.setVisibility(View.VISIBLE);
                    gc6.setChecked(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * 初始化界面显示
     */
    private void initData(){
        spDK.setSelection(Cache.zmd);
        spPD.setSelection(Cache.pc);
        if(Cache.gx.equals("Ⅰ型")){
            spGX.setSelection(0);
        }else{
            spGX.setSelection(1);
        }
        edpdcs.setText(String.valueOf(Cache.pccs));
        tvpdjg.setText(String.valueOf(Cache.pcjg));
        gc1.setChecked(Cache.gcqy1);
        gc2.setChecked(Cache.gcqy2);
        gc3.setChecked(Cache.gcqy3);
        gc4.setChecked(Cache.gcqy4);
        gc5.setChecked(Cache.gcqy5);
        gc6.setChecked(Cache.gcqy6);

        PZDao pzDao= new PZDao();
        List<HashMap<String,String>> listPZ = pzDao.getPZ();
        if(listPZ==null || listPZ.isEmpty()){
            tvxtmc.setText("");
            tvxtbh.setText("");
            edfwqdz.setText("");
            edfwqdkh.setText("");
            spLockScreen.setSelection(0);
            spChooseSick.setSelection(0);
            Cache.lockScreen="0";
            Cache.chooseSick="0";
        }else{
            tvxtmc.setText(listPZ.get(0).get("appname")==null?"":listPZ.get(0).get("appname").toString());
            tvxtbh.setText(listPZ.get(0).get("appcode")==null?"":listPZ.get(0).get("appcode").toString());
            edfwqdz.setText(listPZ.get(0).get("serverip")==null?"":listPZ.get(0).get("serverip").toString());
            edfwqdkh.setText(listPZ.get(0).get("serverport")==null?"":listPZ.get(0).get("serverport").toString());
            if(listPZ.get(0).get("lockscreen")==null || listPZ.get(0).get("lockscreen").toString().equals("0")){
                spLockScreen.setSelection(0);
                Cache.lockScreen="0";
            }else{
                spLockScreen.setSelection(1);
                Cache.lockScreen="1";
            }
            if(listPZ.get(0).get("choosesick")==null || listPZ.get(0).get("choosesick").toString().equals("0")){
                spChooseSick.setSelection(0);
                Cache.chooseSick="0";
            }else{
                spChooseSick.setSelection(1);
                Cache.chooseSick="1";
            }

        }
    }
    /**
     * 设置屏幕的亮度
     */
    private void setScreenBrightness(int process) {

        //设置当前窗口的亮度值.这种方法需要权限android.permission.WRITE_EXTERNAL_STORAGE
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        float f = process / 255.0F;
        localLayoutParams.screenBrightness = f;
        getWindow().setAttributes(localLayoutParams);
        //修改系统的亮度值,以至于退出应用程序亮度保持
        saveBrightness(getContentResolver(),process);

    }

    public  void saveBrightness(ContentResolver resolver, int brightness) {
        //改变系统的亮度值
        //这里需要权限android.permission.WRITE_SETTINGS
        //设置为手动调节模式
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        //保存到系统中
        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
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
                case R.id.btnok:
                    btnOK.setPressed(true);
                    try{
                        int lightModel=0;
                        if(spDK.getSelectedItem().toString().equals("灯自动")){
                            lightModel=0;
                        }else if(spDK.getSelectedItem().toString().equals("灯常开")){
                            lightModel=1;
                        }else if(spDK.getSelectedItem().toString().equals("灯常关")){
                            lightModel=2;
                        }
                        int pc=0;
                        if(spPD.getSelectedItem().toString().equals("全部盘存")){
                            pc=0;
                        }else if(spPD.getSelectedItem().toString().equals("触发盘存")){
                            pc=1;
                        }
                        int gx=1;
                        if(spGX.getSelectedItem().toString().equals("Ⅰ型")){
                            gx=1;
                        }else{
                            gx=2;
                        }
                        int pccs=1;
                        try{
                            pccs=Integer.valueOf(edpdcs.getText().toString());
                        }catch (Exception e){
                        }
                        int pdjg=5;
                        try{
                            pdjg=Integer.valueOf(tvpdjg.getText().toString());
                        }catch (Exception e){

                        }
                        if(pdjg<5 || pdjg>255){
                            Toast.makeText(PZActivity.this, "盘点间隔为5到255", Toast.LENGTH_LONG).show();
                            MyTextToSpeech.getInstance().speak("盘点间隔为5到255");
                            return;
                        }
                        boolean bl1=HCProtocol.ST_SetWorkModel(lightModel,pc,pccs,pdjg);
                        if(bl1){
                            logger.info("下发工作模式成功");
                        }else{
                            logger.info("下发工作模式失败");
                        }
                        byte[] bydata=new byte[14];
                        if(gx==1){
                            bydata[0]=0x01;
                        }else{
                            bydata[0]=0x02;
                        }
                        bydata[1]=bycpxlh[0];
                        bydata[2]=bycpxlh[1];
                        bydata[3]=bycpxlh[2];
                        bydata[4]=bycpxlh[3];
                        bydata[5]=bycpxlh[4];
                        bydata[6]=bycpxlh[5];
                        bydata[7]=byyjbbh[0];
                        bydata[8]=bygjbbh[0];
                        String str="00"+(gc6.isChecked()?"1":"0")+(gc5.isChecked()?"1":"0")+(gc4.isChecked()?"1":"0")+(gc3.isChecked()?"1":"0")+(gc2.isChecked()?"1":"0")+(gc1.isChecked()?"1":"0");
                        int da=Integer.parseInt(str,2);
                        bydata[9]=(byte)da;
                        boolean bl2=HCProtocol.ST_SetDeviceInfo(bydata);
                        if(bl2){
                            logger.info("下发设备信息成功");
                        }else{
                            logger.info("下发设备信息失败");
                        }
                        btnOK.setPressed(false);
                        if(bl1 && bl2){
                            Cache.zmd=lightModel;
                            Cache.pc=pc;
                            Cache.pccs=pccs;
                            Toast.makeText(PZActivity.this, "下传配置完成", Toast.LENGTH_LONG).show();
                            MyTextToSpeech.getInstance().speak("下传配置完成");

                            logger.info("下传配置成功："+spDK.getSelectedItem().toString()+","+spPD.getSelectedItem().toString()+"盘存次数:"+edpdcs.getText().toString());
                        }else{
                            Toast.makeText(PZActivity.this, "下传配置失败", Toast.LENGTH_LONG).show();
                            MyTextToSpeech.getInstance().speak("下传配置失败");
                            logger.info("下传配置失败："+spDK.getSelectedItem().toString()+","+spPD.getSelectedItem().toString()+"盘存次数:"+edpdcs.getText().toString());
                        }

                    }catch (Exception e){
                        logger.error("保存出错",e);
                    }

                    break;
                case R.id.btnsave:
                    try{
                        if(!edfwqdz.getText().toString().equals("") && !isIP(edfwqdz.getText().toString())){
                            Toast.makeText(PZActivity.this, "IP地址不合法", Toast.LENGTH_LONG).show();
                            MyTextToSpeech.getInstance().speak("IP地址不合法");
                            return;
                        }
                        if(!edfwqdkh.getText().toString().equals("") && !isPort(edfwqdkh.getText().toString())){
                            Toast.makeText(PZActivity.this, "端口号不合法", Toast.LENGTH_LONG).show();
                            MyTextToSpeech.getInstance().speak("端口号不合法");
                            return;
                        }
                        PZDao pzDao= new PZDao();

                        if(spLockScreen.getSelectedItem().toString().equals("否")){
                            Cache.lockScreen="0";
                        }else{
                            Cache.lockScreen="1";
                        }

                        if(spChooseSick.getSelectedItem().toString().equals("否")){
                            Cache.chooseSick="0";
                        }else{
                            Cache.chooseSick="1";
                        }
                        pzDao.updateAppName(tvxtmc.getText().toString(),tvxtbh.getText().toString(),edfwqdz.getText().toString(),edfwqdkh.getText().toString(),Cache.lockScreen,Cache.chooseSick);
                        sendAPPName(tvxtmc.getText().toString());
                        Cache.appname=tvxtmc.getText().toString();
                        Cache.appcode=tvxtbh.getText().toString();
                        Cache.ServerIP=edfwqdz.getText().toString();
                        Cache.ServerPort=Integer.valueOf(edfwqdkh.getText().toString().equals("")?"0":edfwqdkh.getText().toString());
                        Toast.makeText(PZActivity.this, "保存配置完成", Toast.LENGTH_LONG).show();
                        MyTextToSpeech.getInstance().speak("保存配置完成");
                    }catch (Exception e){
                        logger.error("保存APP名称等信息出错",e);
                    }
                    break;
                case R.id.fh:
                    PZActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    private  void sendAPPName(String appname){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("appname",appname);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    public boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
    * 判断IP格式和范围
    */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        boolean ipAddress = mat.find();

        //============对之前的ip判断的bug在进行判断
        if (ipAddress == true) {
            String ips[] = addr.split("\\.");

            if (ips.length == 4) {
                try {
                    for (String ip : ips) {
                        if (Integer.parseInt(ip) < 0 || Integer.parseInt(ip) > 255) {
                            return false;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                return true;
            } else {
                return false;
            }
        }

        return ipAddress;
    }

    public static boolean isPort(String portStr) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(portStr);
        if (isNum.matches() && portStr.length() < 6 && Integer.valueOf(portStr) >= 1
                && Integer.valueOf(portStr) <= 65535) {
            return true;
        }
        return false;
    }


}
