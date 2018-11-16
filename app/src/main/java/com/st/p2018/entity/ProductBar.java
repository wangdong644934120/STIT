package com.st.p2018.entity;

/**
 * Created by Administrator on 2018/11/13.
 */

public class ProductBar {
    private String pp;  //品牌
    private String type;//种类
    private String gg;  //规格
    private String yxq;   //近效期
    private String yxts;    //有效天数


    public ProductBar(String pp,String type,String gg,String yxq,String yxts){
        this.pp=pp;
        this.gg=gg;
        this.type=type;
        this.yxq=yxq;
        this.yxts=yxts;
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

    public String getYxq() {
        return yxq;
    }

    public void setYxq(String yxq) {
        this.yxq = yxq;
    }

    public String getYxts() {
        return yxts;
    }

    public void setYxts(String yxts) {
        this.yxts = yxts;
    }
}
