package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;

import java.util.List;

/**
 * Created by Administrator on 2019/7/22.
 */

public class ProductSearch {
    private String order;
    private String number;
    private List<Product> data;

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

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }
}
