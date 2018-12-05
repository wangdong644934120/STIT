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
    public void addMutilProduct(List<ProductBar> list){
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

    public List<HashMap<String, String>> getProduct() {
        String sql="select * from stit_t_allproduct";
        return DataBaseExec.execQueryForMap(sql, null);
    }

    public void clearAllProduct(){
        String sql="delete from stit_t_allproduct";
        DataBaseExec.execOther(sql,null);
    }
}
