package com.st.p2018.entity;

/**
 * Created by Administrator on 2019/1/2.
 */

public class ProductQuery {
    private String pp;  //品牌
    private String type;//种类
    private String gg;  //规格
    private String yxrq;  //有效日期
    private String syts;  //剩余天数
    private String wz;  //位置

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

    public String getYxrq() {
        return yxrq;
    }

    public void setYxrq(String yxrq) {
        this.yxrq = yxrq;
    }

    public String getSyts() {
        return syts;
    }

    public void setSyts(String syts) {
        this.syts = syts;
    }

    public String getWz() {
        return wz;
    }

    public void setWz(String wz) {
        this.wz = wz;
    }
}
