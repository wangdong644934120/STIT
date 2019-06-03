package com.st.p2018.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/6/2.
 */

public class Product implements Serializable {

    private String pp; //品牌
    private String mc; //名称
    private String xqpc; //效期批次
    private String yxrq; //有效日期
    private String syts; //剩余天数
    private String epc; //耗材epc
    private String szwz; //所在位置
    private String operation; //操作


    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getMc() {
        return mc;
    }

    public void setMc(String mc) {
        this.mc = mc;
    }

    public String getXqpc() {
        return xqpc;
    }

    public void setXqpc(String xqpc) {
        this.xqpc = xqpc;
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

    public String getSzwz() {
        return szwz;
    }

    public void setSzwz(String szwz) {
        this.szwz = szwz;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
