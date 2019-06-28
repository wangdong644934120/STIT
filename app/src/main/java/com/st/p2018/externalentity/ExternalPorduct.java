package com.st.p2018.externalentity;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/6/10.
 */

public class ExternalPorduct implements Serializable {
    private String order;
    private String number;
    private ExternalPorduct_ActionTotal data;


    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public ExternalPorduct_ActionTotal getData() {
        return data;
    }

    public void setData(ExternalPorduct_ActionTotal data) {
        this.data = data;
    }


}
