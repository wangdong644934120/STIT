package com.st.p2018.entity;

/**
 * Created by Administrator on 2018/11/13.
 */

public class ProductBar {
    private String id;
    private String pp;  //品牌
    private String type;//种类
    private String gg;  //规格
    private long yxq;   //近效期
    private String card;


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

    public long getYxq() {
        return yxq;
    }

    public void setYxq(long yxq) {
        this.yxq = yxq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }
}
