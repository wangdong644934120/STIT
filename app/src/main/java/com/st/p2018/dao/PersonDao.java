package com.st.p2018.dao;

import android.provider.ContactsContract;

import com.st.p2018.database.DataBaseExec;
import com.st.p2018.database.DatabaseManager;
import com.st.p2018.entity.PersonInfo;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */

public class PersonDao {
    /**
     * 添加人员
     * @param pi
     */
    public boolean addPerson(PersonInfo pi) {
        String sql = " insert into stit_t_person (id ,code,name,card,tzz) "
                + "values (? ,? , ? , ? ,? ) ";
        String[] args = new String[] { pi.getId(),pi.getCode(),pi.getName(),pi.getCard(),pi.getTzz()};
        return DataBaseExec.execOther(sql, args);
    }

    public List<HashMap<String,String>> getPerson(){
        String sql = "select id,code,name,card,tzz from stit_t_person";
        List<HashMap<String,String>> list=DataBaseExec.execQueryForMap(sql,null);
        return list;
    }

    public List<HashMap<String,String>>  getSameCodeForAdd(String code){
        String sql = "select id,code,name,card,tzz from stit_t_person where code=?";
        String[] args = new String[]{code};
        List<HashMap<String,String>> list=DataBaseExec.execQueryForMap(sql,args);
        return list;
    }

    public List<HashMap<String,String>>  getSameCodeForModify(String id,String code){
        String sql = "select id,code,name,card,tzz from stit_t_person where id!=? and code=?";
        String[] args = new String[]{id,code};
        List<HashMap<String,String>> list=DataBaseExec.execQueryForMap(sql,args);
        return list;
    }
    public boolean modifyPerson(PersonInfo pi){
        String sql="update stit_t_person set code=?,name=?,card=?,tzz=? where id=?";
        String[] args = new String[]{pi.getCode(),pi.getName(),pi.getCard(),pi.getTzz(),pi.getId()};
        return DataBaseExec.execOther(sql,args);
    }
    public boolean deletePerson(String id){
        String sql="delete from stit_t_person where id=?";
        String[] args =new String[]{id};
        return DataBaseExec.execOther(sql,args);
    }
    public List<HashMap<String,String>> getPersonByCardOrZW(String cardZW){
        String sql="select id,code,name from stit_t_person where card=? or code=?";
        String[] args=new String[]{cardZW,cardZW};
        List<HashMap<String,String>> list = DataBaseExec.execQueryForMap(sql,args);
        return list;
    }
}
