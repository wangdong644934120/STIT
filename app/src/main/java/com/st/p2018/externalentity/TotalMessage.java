package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;

import java.util.List;

/**
 * Created by Administrator on 2019/7/24.
 */

public class TotalMessage {
    private String location;
    private List<Product> jxq;
    private List<Product> qt;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<Product> getJxq() {
        return jxq;
    }

    public void setJxq(List<Product> jxq) {
        this.jxq = jxq;
    }

    public List<Product> getQt() {
        return qt;
    }

    public void setQt(List<Product> qt) {
        this.qt = qt;
    }
}
