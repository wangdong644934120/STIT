package com.st.p2018.dao;

import com.st.p2018.database.DataBaseExec;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/7/27.
 */

public class ExternalPowerDao {

    /**
     * 从本地获取权限
     * @param card1 卡号或指纹编号十进制或十六进制
     * @param card2 卡号或指纹编号
     * @param type 1-指纹，2-刷卡，3-用户名和密码
     * @return
     */
    public List<HashMap<String, String>> getPower(String card1,String card2, String type) {
        String sql="select * from stit_t_externalpower where (card=? or card=? ) and type=?";
        String[] args = new String[]{card1,card2,type};
        return DataBaseExec.execQueryForMap(sql, args);
    }

    /**
     * 添加权限
     * @param id
     * @param name 姓名
     * @param code 编号
     * @param card 指纹或卡号或用户名+密码
     * @param type 1-指纹，2-刷卡，3-用户名和密码
     */
    public void addPower(String id,String name,String code,String card,String type){
        String sql = "insert into stit_t_externalpower (id ,name, code , card , type) values (?,?,?,?,?) ";
        String[] args= new String[]{id,name,code,card,type};
        DataBaseExec.execOther(sql,args);
    }
}
