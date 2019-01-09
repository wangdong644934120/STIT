package com.st.p2018.dao;

import com.st.p2018.database.DataBaseExec;
import com.st.p2018.entity.ProductBar;
import com.st.p2018.util.Cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

    public List<HashMap<String, String>> getProductByJXQ() {
        String sql="select * from stit_t_product where wz!='0'";
        return DataBaseExec.execQueryForMap(sql, null);
    }

    public List<HashMap<String,String>> getPorductBySDHWXC(String hwxc){
        String sql="select * from stit_t_product where wz=0 or wz=?";
        String[] args = new String[]{hwxc};
        return DataBaseExec.execQueryForMap(sql,args);
    }

    public List<HashMap<String,String>> getPorductByCFHWXC(List<String> list){
        list.add("0");
        StringBuilder sb=new StringBuilder("select * from stit_t_product "
            + " where wz in ( ");
		for (String wz : list) {
            sb.append("'").append(wz).append("'").append(",");
        }
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");
		return DataBaseExec.execQueryForMap(sb.toString(),null);
    }

    public void clearAll_AllProduct(){
        String sql="delete from stit_t_allproduct";
        DataBaseExec.execOther(sql,null);
    }

    public List<HashMap<String,String>> getAllProductByHCCS(){
        Set<String> cards = Cache.HCCSMap.keySet();
        StringBuilder sql=new StringBuilder("select id,pp,zl,gg,yxq,card from stit_t_allproduct " +
                "where card in(");
        for(String card : cards){
            sql.append("'").append(card.toUpperCase()).append("',");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");

        List<HashMap<String,String>> list=DataBaseExec.execQueryForMap(sql.toString(),null);
        return list;
    }

    public void getAllProductByHCCS1(){
        Set<String> cards = Cache.HCCSMap.keySet();
        StringBuilder sql=new StringBuilder("insert into stit_t_product (id,pp,zl,gg,yxq,card) select id,pp,zl,gg,yxq,card from stit_t_allproduct " +
                "where card in(");
        for(String card : cards){
            sql.append("'").append(card).append("',");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(")");

        DataBaseExec.execOther(sql.toString(),null);
    }

    public void updateHCCSWZ(){

    }

    public void clearAllProduct(){
        String sql="delete from stit_t_product";
        DataBaseExec.execOther(sql,null);
    }
    //已过期
    public List<HashMap<String, String>> getYGQProduct(HashMap map) {
        String sql="select * from stit_t_product where yxq<? and wz !='0'";
        String[] args = new String[]{map.get("yxq").toString()};
        return DataBaseExec.execQueryForMap(sql, args);
    }
    //近效期
    public List<HashMap<String, String>> getJXQProduct(HashMap map) {
        String sql="select * from stit_t_product where yxq>=? and yxq<? and wz !='0'";
        String[] args = new String[]{map.get("yxq1").toString(),map.get("yxq2").toString()};
        return DataBaseExec.execQueryForMap(sql, args);
    }

    //远效期
    public List<HashMap<String, String>> getYXQProduct(HashMap map) {
        String sql="select * from stit_t_product where yxq>=? and wz !='0'";
        String[] args = new String[]{map.get("yxq").toString()};
        return DataBaseExec.execQueryForMap(sql, args);
    }

    public void updateAllProductWZ(List<HashMap<String,String>> list){
        StringBuilder sql=new StringBuilder("insert into stit_t_product (id,pp,zl,gg,yxq,card,wz) values ");
        for(HashMap<String,String> map : list){
            sql.append("('").append(map.get("id").toString()).append("','");
            sql.append(map.get("pp").toString()).append("','");
            sql.append(map.get("zl").toString()).append("','");
            sql.append(map.get("gg").toString()).append("','");
            sql.append(map.get("yxq").toString()).append("','");
            sql.append(map.get("card").toString().toUpperCase()).append("','");
            sql.append(map.get("wz").toString()).append("'),");
        }
        sql.deleteCharAt(sql.length()-1);

        DataBaseExec.execOther(sql.toString(),null);
    }

    public void updateProductWZ(String wz,String card){
        String sql="update stit_t_product set wz=? where card=?";
        String[] args=new String[]{wz,card};
        DataBaseExec.execOther(sql,args);
    }


}
