package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/6/10.
 */

public class ExternalPorduct implements Serializable {
    private String order;
    private String number;
    private ActionTotal data;


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

    public ActionTotal getData() {
        return data;
    }

    public void setData(ActionTotal data) {
        this.data = data;
    }


}
