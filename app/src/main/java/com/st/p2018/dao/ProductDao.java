package com.st.p2018.dao;

import com.st.p2018.database.DataBaseExec;
import com.st.p2018.entity.ProductBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/29.
 */

public class ProductDao {
    public void addMutil_AllProduct(List<ProductBar> list){
        if (!list.isEmpty()) {
            String sql = "insert into stit_t_allproduct (id , pp , zl , gg , yxq,card ) values (?,?,?,?,?,?) ";
            List<String[]> listHM = new ArrayList<String[]>();
            for (ProductBar pb : list) {
                String[] s = new String[6];
                s[0] = pb.getId();
                s[1] = pb.getPp();
                s[2] = pb.getType();
                s[3] = pb.getGg();
                s[4] = String.valueOf(pb.getYxq());
                s[5]=pb.getCard();
                listHM.add(s);
            }
            DataBaseExec.multiExec(sql, listHM);
        }
    }

    public List<HashMap<String, String>> getAll_AllProduct() {
        String sql="select * from stit_t_allproduct";
        return DataBaseExec.execQueryForMap(sql, null);
    }

    public List<HashMap<String, String>> getAllProduct() {
        String sql="select * from stit_t_product";
        return DataBaseExec.execQueryForMap(sql, null);
    }
    public void clearAll_AllProduct(){
        String sql="delete from stit_t_allproduct";
        DataBaseExec.execOther(sql,null);
    }

    public void addMutilAllProduct(){
        String sql="insert into stit_t_product (id,pp,zl,gg,yxq,card) select id,pp,zl,gg,yxq,card from stit_t_allproduct";
        DataBaseExec.execOther(sql,null);
    }

    public void clearAllProduct(){
        String sql="delete from stit_t_product";
        DataBaseExec.execOther(sql,null);
    }
    //已过期
    public List<HashMap<String, String>> getYGQProduct(HashMap map) {
        String sql="select * from stit_t_product where yxq<?";
        String[] args = new String[]{map.get("yxq").toString()};
        return DataBaseExec.execQueryForMap(sql, args);
    }
    //近效期
    public List<HashMap<String, String>> getJXQProduct(HashMap map) {
        String sql="select * from stit_t_product where yxq>=? and yxq<?";
        String[] args = new String[]{map.get("yxq1").toString(),map.get("yxq2").toString()};
        return DataBaseExec.execQueryForMap(sql, args);
    }

    //远效期
    public List<HashMap<String, String>> getYXQProduct(HashMap map) {
        String sql="select * from stit_t_product where yxq>=?";
        String[] args = new String[]{map.get("yxq").toString()};
        return DataBaseExec.execQueryForMap(sql, args);
    }

    public void updateAllProductWZ(){
        String sql="update stit_t_product set wz=?";
        String[] args=new String[]{"1"};
        DataBaseExec.execOther(sql,args);
    }

    public void updateProductWZ(String wz,String card){
        String sql="update stit_t_product set wz=? where card=?";
        String[] args=new String[]{wz,card};
        DataBaseExec.execOther(sql,args);
    }

}
