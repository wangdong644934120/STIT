package com.st.p2018.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.st.p2018.dao.PersonDao;
import com.st.p2018.entity.PersonAdapter;
import com.st.p2018.entity.PersonInfo;
import com.st.p2018.stit.R;
import com.st.p2018.util.MyTextToSpeech;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class PersonActivity extends Activity {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        initView();

        initGridHeader();// 初始表头
        initQueryGrid();// 初始查询结果表格
    }

    /**
     * 初始化view，设置event listener
     */
    public void initView() {
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

    }

    /**
     * 初始化表头
     */
    private void initGridHeader() {
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
    }

    /**
     * 初始查询结果表格
     */
    private void initQueryGrid() {
        getdata();
//        List<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put("id", "id");
//        map.put("name", "姓名");
//        map.put("code", "编号");
//        map.put("iszw", "指纹录取");
//        map.put("tzz", "指纹特征值");
//
//        data.add(map);
        PersonAdapter adapter = new PersonAdapter(PersonActivity.this, mQueryData);
        listResults.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    /**
     * 从数据库查询数据
     *
     * @return
     */
    private void getdata() {

        List<HashMap<String,String>> list = pd.getPerson();
        mQueryData.clear();
        for(HashMap map : list){
            map.put("id",map.get("id").toString());
            map.put("code",map.get("code").toString());
            map.put("name",map.get("name").toString());
            map.put("card",map.get("card")==null?"":map.get("card").toString());
            map.put("tzz",map.get("tzz")==null?"":map.get("tzz").toString());
            mQueryData.add(map);
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
                    getZW();
                    break;
                case R.id.btnkh:
                    getKH();
                    break;
                case R.id.add:
                   add();
                    break;
                case R.id.modify:
                    modify();
                    break;
                case R.id.delete:
                    delete();
                    break;
                default:
                    break;
            }
        }

    }

    private void add(){
        String codep = code.getText().toString();
        String namep=name.getText().toString();
        String tzzp=tzz.getText().toString();
        String cardp=kh.getText().toString();
        if(check(codep,namep)){
            Toast.makeText(this, "工号或姓名不能为空", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("工号或姓名不能为空");
            return;
        }
        List<HashMap<String,String>> listCF = pd.getSameCodeForAdd(codep);
        if(!listCF.isEmpty()){
            Toast.makeText(this, "工号重复", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("工号重复");
            return;
        }
        PersonInfo pi = new PersonInfo();
        pi.setId(UUID.randomUUID().toString());
        pi.setCode(codep);
        pi.setName(namep);
        pi.setCard(cardp);
        pi.setTzz(tzzp);
        if(pd.addPerson(pi)){
            initQueryGrid();
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("添加成功");
        }

    }
    private void modify(){
        if(selecItem<0){
            Toast.makeText(this, "请选择一条记录", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("请选择一条记录");
            return;
        }
        String id=mQueryData.get(selecItem).get("id").toString();
        String codep = code.getText().toString();
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
        PersonInfo pi = new PersonInfo();
        pi.setId(id);
        pi.setCode(codep);
        pi.setName(namep);
        pi.setCard(cardp);
        pi.setTzz(tzzp);
        if(pd.modifyPerson(pi)){
            initQueryGrid();
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("修改成功");
        }

    }
    private void delete(){
        if(selecItem<0){
            Toast.makeText(this, "请选择一条记录", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("请选择一条记录");
            return;
        }
        String id=mQueryData.get(selecItem).get("id").toString();
        if(pd.deletePerson(id)){
            initQueryGrid();
            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
            MyTextToSpeech.getInstance().speak("删除成功");
        }
    }

    private void showMessage(){
        HashMap<String,String> map = mQueryData.get(selecItem);
        code.setText(map.get("code").toString());
        name.setText(map.get("name").toString());
        tzz.setText(map.get("tzz")==null?"":map.get("tzz").toString());
        kh.setText(map.get("card")==null?"":map.get("card").toString());
    }

    private void getZW(){
        tzz.setText("zw11111111111111111111111111111111111111111111111111111111111");
    }
    private void getKH(){
        kh.setText("A1234321");
    }
    private boolean check(String code,String name){
        if(code.trim().equals("") || name.trim().equals("")){
            return  true;
        }
        return false;
    }


}
