package com.st.p2018.dao;

import com.st.p2018.database.DataBaseExec;
import com.st.p2018.entity.ProductBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/29.
 */

public class PZDao {

    public void updatePZ(HashMap map){
        String sql="update stit_t_pz set gx=?,dk=?,pl=?,gc1=?,gc2=?,gc3=?,gc4=?,gc5=?,gc6=? where id='1'";
        String[] args = new String[]{map.get("gx").toString(),map.get("dk").toString(),map.get("pl").toString(),
        map.get("gc1").toString(),map.get("gc2").toString(),map.get("gc3").toString(),map.get("gc4").toString(),
        map.get("gc5").toString(),map.get("gc6").toString()};
        DataBaseExec.execOther(sql,args);
    }

    public void updatePZByDevice(HashMap map){
        String sql="update stit_t_pz set gx=?,gc1=?,gc2=?,gc3=?,gc4=?,gc5=?,gc6=? where id='1'";
        String[] args = new String[]{map.get("gx").toString(),
                map.get("gc1").toString(),map.get("gc2").toString(),map.get("gc3").toString(),map.get("gc4").toString(),
                map.get("gc5").toString(),map.get("gc6").toString()};
        DataBaseExec.execOther(sql,args);
    }

    public List<HashMap<String,String>> getPZ(){
        String sql="select gx,dk,pl,gc1,gc2,gc3,gc4,gc5,gc6,appname,appcode,serverip,serverport from stit_t_pz where id='1'";
        return DataBaseExec.execQueryForMap(sql,null);
    }

    public void updateAppName(String name,String code,String serverip,String serverport){
        String sql="update stit_t_pz set appname=? ,appcode=?,serverip=?,serverport=?";
        String[] args = new String[]{name,code,serverip,serverport};
        DataBaseExec.execOther(sql,args);
    }

}
