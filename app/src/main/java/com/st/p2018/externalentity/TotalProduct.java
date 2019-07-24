package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;

import java.util.List;

/**
 * Created by Administrator on 2019/7/24.
 */

public class TotalProduct {
    private String order;
    private String number;
    private String message;
    private List<TotalMessage> data;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TotalMessage> getData() {
        return data;
    }

    public void setData(List<TotalMessage> data) {
        this.data = data;
    }
}
