package com.st.p2018.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.st.p2018.dao.PersonDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.entity.PersonAdapter;
import com.st.p2018.entity.PersonInfo;
import com.st.p2018.stit.R;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class PersonActivity extends Activity {

    private Logger logger = Logger.getLogger(PersonActivity.class);
    private ListView listHeaders;// 表头ListView
    private ListView listResults;// //查询结果listview
    private List<HashMap<String, String>> mQueryData = new ArrayList<HashMap<String, String>>() ;
    private EditText code;
    private EditText name;
    private Button btntzz;
    private Button btnkh;
    private EditText tzz;
    private EditText kh;
    private Button add;
    private Button modify;
    private Button delete;
    private int selecItem=-1;
    private PersonDao pd = new PersonDao();
    private TextView tvfh;
    private TextView tvtitle;
    private KHThread khThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_person);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        //使用布局文件来定义标题栏
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.othertitle);
        initView();
        initGridHeader();// 初始表头
        initQueryGrid();// 初始查询结果表格
    }

    @Override
    protected void onDestroy(){
        Cache.getPersonCard=false;
        super.onDestroy();
    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
        try{
            tvfh=(TextView)findViewById(R.id.fh);
            tvfh.setOnClickListener(new onClickListener());
            tvtitle=(TextView)findViewById(R.id.title);
            tvtitle.setText("人员管理");
            code=(EditText)findViewById(R.id.code);
            name=(EditText)findViewById(R.id.name);
            btntzz=(Button)findViewById(R.id.btntzz);
            btntzz.setOnClickListener(new onClickListener());
            btnkh=(Button)findViewById(R.id.btnkh);
            btnkh.setOnClickListener(new onClickListener());
            tzz=(EditText)findViewById(R.id.tzz);
            kh=(EditText)findViewById(R.id.kh);
            listHeaders = (ListView) findViewById(R.id.listHeaders);
            listResults = (ListView) findViewById(R.id.listResults);
            listResults.setOnItemClickListener(new OnLvItemClickListener());

            add=(Button)findViewById(R.id.add);
            modify=(Button)findViewById(R.id.modify);
            delete=(Button)findViewById(R.id.delete);
            add.setOnClickListener(new onClickListener());
            modify.setOnClickListener(new onClickListener());
            delete.setOnClickListener(new onClickListener());

            Cache.myHandlePerson = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
                    //提示信息
                    if (bundle.getString("kh") != null) {
                        kh.setText(bundle.getString("kh"));
                        Cache.getPersonCard=false;
                        if(khThread!=null){
                            khThread.closeKHThread();
                        }
                        MyTextToSpeech.getInstance().speak("刷卡完成");
                        Toast.makeText(PersonActivity.this, "刷卡完成", Toast.LENGTH_SHORT).show();
                    }
                    if (bundle.getString("zw") != null) {
                        if(bundle.getString("zw").toString().equals("ok")){
                            Cache.zwlrNow=false;
                            tzz.setText(UUID.randomUUID().toString());
                            MyTextToSpeech.getInstance().speak("指纹录入完成");
                            Toast.makeText(PersonActivity.this, "指纹录入完成", Toast.LENGTH_SHORT).show();
                        }else if(bundle.getString("zw").toString().equals("fail")){
                            tzz.setText("");
                            Cache.zwlrNow=false;
                            MyTextToSpeech.getInstance().speak("指纹录入失败");
                            Toast.makeText(PersonActivity.this, "指纹录入失败", Toast.LENGTH_SHORT).show();
                        }else if(bundle.getString("zw").toString().equals("progress")){
                            MyTextToSpeech.getInstance().speak("指纹录入中请勿移动手指");
                            Toast.makeText(PersonActivity.this, "指纹录入中请勿移动手指", Toast.LENGTH_SHORT).show();
                        }

                    }

                }
            };
        }catch (Exception e){
            logger.error("初始化view出错",e);
        }

    }

    /**
     * 初始化表头
     */
    private void initGridHeader() {
        try{
            List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", "id");
            map.put("name", "姓名");
            map.put("code", "工号");
            map.put("card","卡号");
            map.put("tzz", "指纹特征值");

            data.add(map);
            PersonAdapter adapter = new PersonAdapter(PersonActivity.this, data);
            listHeaders.setAdapter(adapter);
        }catch (Exception e){
            logger.error("初始化表头出错",e);
        }

    }

    /**
     * 初始查询结果表格
     */
    private void initQueryGrid() {
        try{
            getdata();
            PersonAdapter adapter = new PersonAdapter(PersonActivity.this, mQueryData);
            listResults.setAdapter(adapter);
        }catch (Exception e){
            logger.error("初始化表格结果出错",e);
        }


    }

    /**
     * 从数据库查询数据
     *
     * @return
     */
    private void getdata() {

        try{
            List<HashMap<String,String>> list = pd.getPerson();
           /* if(list==null || list.isEmpty()){
                //删除所有指纹
                boolean bl=HCProtocol.ST_DeleteZW(1,0);
                if(bl){
                    logger.info("表格中查询人员为空，清空设备所有指纹成功");
                }else{
                    logger.info("表格中查询人员为空，清空设备所有指纹失败");
                }
            }*/
            mQueryData.clear();
            for(HashMap map : list){
                map.put("id",map.get("id").toString());
                map.put("code",map.get("code").toString());
                map.put("name",map.get("name").toString());
                map.put("card",map.get("card")==null?"":map.get("card").toString());
                map.put("tzz",map.get("tzz")==null?"":map.get("tzz").toString());
                mQueryData.add(map);
            }
        }catch (Exception e){
            logger.error("从数据库查询数据出错",e);
        }

    }

    private List<String> initSpinner(){
        List<String> list = new ArrayList<String>();
        list.add("是");
        list.add("否");
        return list;
    }

    public class OnLvItemClickListener implements AdapterView.OnItemClickListener {
        private View lastView = null;

        @Override
        public void onItemClick(AdapterView<?> adapter, View v, int position, long rowID) {
            if (null != lastView)
                lastView.setBackgroundColor(Color.WHITE);
            v.setBackgroundColor(Color.parseColor("#FEE7B3"));
            lastView = v;
            selecItem=position;
            add.setEnabled(false);
            add.setBackgroundColor(Color.GRAY);
            code.setEnabled(false);
            showMessage();
        }

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
                case R.id.btntzz:
                    btntzz.setPressed(true);
                    getZW();
                    btntzz.setPressed(false);
                    break;
                case R.id.btnkh:
                    btnkh.setPressed(true);
                    getKH();
                    btnkh.setPressed(false);
                    break;
                case R.id.add:
                    add.setPressed(true);
                   add();
                   add.setPressed(false);
                    break;
                case R.id.modify:
                    modify.setPressed(true);
                    modify();
                    modify.setPressed(false);
                    break;
                case R.id.delete:
                    delete.setPressed(true);
                    final AlertDialog alertDialog = new AlertDialog.Builder(PersonActivity.this)
                            .setTitle("确认提示框")
                            .setMessage("确认删除该人员？")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    delete();
                                }
                            })

                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }) .create();
                    alertDialog.show();

                    delete.setPressed(false);
                    break;
                case R.id.fh:
                    if(Cache.zwlrNow){
                        return;
                    }
                    PersonActivity.this.finish();
                    break;
                default:
                    break;
            }
        }

    }

    private void add(){

        try{
            String codep = code.getText().toString();
            String namep=name.getText().toString();
            String tzzp=tzz.getText().toString();
            String cardp=kh.getText().toString();
            //根据工号判断是否能够添加

            if(check(codep,namep)){
                Toast.makeText(this, "工号或姓名不能为空", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("工号或姓名不能为空");
                return;
            }
            //若编号前有0，则将0删除
            int codei=Integer.valueOf(codep);
            codep=String.valueOf(codei);
            System.out.println(codep);
            List<HashMap<String,String>> listCF = pd.getSameCodeForAdd(codep);
            if(!listCF.isEmpty()){
                Toast.makeText(this, "工号重复", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("工号重复");
                return;
            }
            if(!cardp.equals("")){
                List<HashMap<String,String>> listCA = pd.getSameCardForAdd(cardp);
                if(!listCA.isEmpty()){
                    Toast.makeText(this, "卡号重复", Toast.LENGTH_SHORT).show();
                    MyTextToSpeech.getInstance().speak("卡号重复");
                    return;
                }
            }
            PersonInfo pi = new PersonInfo();
            pi.setId(UUID.randomUUID().toString());
            pi.setCode(codep);
            pi.setName(namep);
            pi.setCard(cardp);
            pi.setTzz(tzzp);
            if(pd.addPerson(pi)){
                initQueryGrid();
                code.setText("");
                name.setText("");
                tzz.setText("");
                kh.setText("");
                Toast.makeText(this, "添加完成", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("添加完成");
            }
        }catch (Exception e){
            logger.error("添加人员数据出错",e);
        }


    }

    private void modify(){
        try{
            if(selecItem<0){
                Toast.makeText(this, "请选择一条记录", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("请选择一条记录");
                return;
            }
            if(Integer.valueOf(code.getText().toString())>710){
                MyTextToSpeech.getInstance().speak("工号不能超过710");
                Toast.makeText(this, "工号不能超过710", Toast.LENGTH_SHORT).show();
                return;
            }
            String id=mQueryData.get(selecItem).get("id").toString();
            String codep = code.getText().toString();
            //若编号前有0，则将0删除
            int codei=Integer.valueOf(codep);
            codep=String.valueOf(codei);
            System.out.println(codep);
            String namep=name.getText().toString();
            String cardp=kh.getText().toString();
            String tzzp=tzz.getText().toString();
//        String iszwp="否";
//        if(!tzz.equals("")){
//            iszwp="是";
//        }
            if(check(codep,namep)){
                Toast.makeText(this, "工号或姓名不能为空", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("工号或姓名不能为空");
                return;
            }
            List<HashMap<String,String>> listCF = pd.getSameCodeForModify(id,codep);
            if(!listCF.isEmpty()){
                Toast.makeText(this, "工号重复", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("工号重复");
                return;
            }
            if(!cardp.equals("")){
                List<HashMap<String,String>> listCA = pd.getSameCardForModify(id,cardp);
                if(!listCA.isEmpty()){
                    Toast.makeText(this, "卡号重复", Toast.LENGTH_SHORT).show();
                    MyTextToSpeech.getInstance().speak("卡号重复");
                    return;
                }
            }
            PersonInfo pi = new PersonInfo();
            pi.setId(id);
            pi.setCode(codep);
            pi.setName(namep);
            pi.setCard(cardp);
            pi.setTzz(tzzp);
            if(pd.modifyPerson(pi)){
                initQueryGrid();
                Toast.makeText(this, "修改完成", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("修改完成");
            }
        }catch (Exception e){
            logger.error("修改人员数据出错",e);
        }


    }

    private void delete(){
        try{
            if(selecItem<0){
                Toast.makeText(this, "请选择一条记录", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("请选择一条记录");
                return;
            }
            String id=mQueryData.get(selecItem).get("id").toString();
            HCProtocol.ST_DeleteZW(0,Integer.valueOf(mQueryData.get(selecItem).get("code").toString()));
            if(pd.deletePerson(id)){
                initQueryGrid();
                Toast.makeText(this, "删除完成", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("删除完成");
                code.setText("");
                name.setText("");
                tzz.setText("");
                kh.setText("");
                selecItem=-1;
                add.setEnabled(true);
                add.setBackgroundColor(Color.parseColor("#0011ff"));
                btntzz.setEnabled(true);
                code.setEnabled(true);
//            if(mQueryData.isEmpty()){
//                HCProtocol.ST_DeleteZW(1,0);
//            }
            }
        }catch (Exception e){
            logger.error("删除人员数据出错",e);
        }

    }

    private void showMessage(){
        try{
            HashMap<String,String> map = mQueryData.get(selecItem);
            code.setText(map.get("code").toString());
            name.setText(map.get("name").toString());
            tzz.setText(map.get("tzz")==null?"":map.get("tzz").toString());
            kh.setText(map.get("card")==null?"":map.get("card").toString());
            if(map.get("tzz")!=null && !map.get("tzz").equals("")){
                btntzz.setEnabled(false);
            }else{
                btntzz.setEnabled(true);
            }
        }catch (Exception e){
            logger.error("人员管理显示信息出错",e);
        }

    }

    private void getZW(){
        try{
            if(Cache.zwlrNow){
                return;
            }
            if(code.getText().toString().trim().equals("")){
                MyTextToSpeech.getInstance().speak("请先输入工号");
                Toast.makeText(this, "请先输入工号", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                int codeNum=Integer.valueOf(code.getText().toString());
            }catch (Exception e){
                MyTextToSpeech.getInstance().speak("工号必须为数字");
                Toast.makeText(this, "工号必须为数字", Toast.LENGTH_SHORT).show();
                return;
            }
            if(Integer.valueOf(code.getText().toString())>710){
                MyTextToSpeech.getInstance().speak("工号不能超过710");
                Toast.makeText(this, "工号不能超过710", Toast.LENGTH_SHORT).show();
                return;
            }
            if(Integer.valueOf(code.getText().toString())<=0){
                MyTextToSpeech.getInstance().speak("工号不能小于等于0");
                Toast.makeText(this, "工号不能小于等于0", Toast.LENGTH_SHORT).show();
                return;
            }
            //根据工号判断用户是添加还是修改指纹
            if(selecItem==-1){
                //添加指纹，判断工号是否重复，不重复则删除原有指纹
                if(!addZW()){
                    return;
                }
            }else{
                String id=mQueryData.get(selecItem).get("id").toString();
                String codeS=mQueryData.get(selecItem).get("code").toString();
                String zwS=mQueryData.get(selecItem).get("tzz").toString();
                if(codeS.equals(code.getText().toString().trim())){
                    if(!zwS.equals("")){
                        //修改指纹，先将原有指纹删除
                        boolean blD=HCProtocol.ST_DeleteZW(0,Integer.valueOf(code.getText().toString()));
                        if(!blD){
                            MyTextToSpeech.getInstance().speak("删除原有指纹失败");
                            Toast.makeText(this, "删除原有指纹失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                }else{
                    //添加指纹，判断报工号是否重复，不重复则删除原有指纹
                    if(!addZW()){
                        return;
                    }
                }
            }
            //将数据库中该人员的指纹信息删除
            tzz.setText("");
            pd.deleteZW(code.getText().toString().trim());
            //先将指纹模块中该人员编号指纹删除
            MyTextToSpeech.getInstance().speak("请录入指纹");
            Toast.makeText(this, "请录入指纹", Toast.LENGTH_SHORT).show();
            boolean bl=HCProtocol.ST_AddSaveZW(Integer.valueOf(code.getText().toString()));
            if(bl){
                Cache.zwlrNow=true;
                new ZWLR().start();
            }else{
                MyTextToSpeech.getInstance().speak("指纹录入失败");
                Toast.makeText(this, "指纹录入失败", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            logger.error("获取指纹出错",e);
        }


    }

    private boolean addZW(){
        boolean bl=false;
        try{
            //若编号前有0，则将0删除
            int codei=Integer.valueOf(code.getText().toString().trim());
            String codep=String.valueOf(codei);
            System.out.println(codep);
            List<HashMap<String,String>> listCF = pd.getSameCodeForAdd(codep);
            if(!listCF.isEmpty()){
                Toast.makeText(this, "工号重复", Toast.LENGTH_SHORT).show();
                MyTextToSpeech.getInstance().speak("工号重复");
                bl=false;
                return false;
            }
            bl=true;
            return true;
        }catch (Exception e){
            logger.error("",e);
        }
       return  bl;
    }

    private void getKH(){
        try{
            Cache.getPersonCard=true;
            khThread=new KHThread();
            khThread.start();
            MyTextToSpeech.getInstance().speak("请刷卡");
            Toast.makeText(this, "请刷卡", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            logger.error("获取卡号出错",e);
        }


    }

    private boolean check(String code,String name){
        boolean bl=false;
        try{
            if(code.trim().equals("") || name.trim().equals("")){
                bl=true;
                return  true;
            }
            bl= false;
            return bl;
        }catch (Exception e){
            logger.error("编号姓名检测出错",e);
        }
       return bl;
    }

    class ZWLR extends Thread{
        public void run(){
            try{
                HCProtocol.ST_GetZWZT();
            }catch (Exception e){
                logger.error("指纹录入出错",e);
            }

        }
    }

    class KHThread extends Thread{
        boolean bl=true;
        int i=0;
        public void run(){
            while(bl){
                logger.info("刷卡线程运行中");
                i=i+1;
                if(i>=20){
                    Cache.getPersonCard=false;
                    MyTextToSpeech.getInstance().speak("刷卡失败");

                    break;
                }
                try{
                    Thread.sleep(500);
                }catch (Exception e){

                }
            }
        }

        public void closeKHThread(){
            i=0;
            bl=false;
        }
    }

}
