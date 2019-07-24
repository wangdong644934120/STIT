package com.st.p2018.externalentity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/7/17.
 */

public class ExternalSick implements Serializable{
    private String order;
    private String number;
    private List<Sick> data;

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

    public List<Sick> getData() {
        return data;
    }

    public void setData(List<Sick> data) {
        this.data = data;
    }
}
