package com.st.p2018.entity;

import com.st.p2018.dao.ProductDao;

/**
 * Created by Administrator on 2018/12/17.
 */

public class ProductRecord {
    private String pp;  //品牌
    private String type;//种类
    private String gg;  //规格
    private String cz;  //操作
    private String wz;  //位置

    public ProductRecord(String pp,String type,String gg,String cz,String wz){
        this.pp=pp;
        this.type=type;
        this.gg=gg;
        this.cz=cz;
        this.wz=wz;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGg() {
        return gg;
    }

    public void setGg(String gg) {
        this.gg = gg;
    }

    public String getCz() {
        return cz;
    }

    public void setCz(String cz) {
        this.cz = cz;
    }

    public String getWz() {
        return wz;
    }

    public void setWz(String wz) {
        this.wz = wz;
    }
}
