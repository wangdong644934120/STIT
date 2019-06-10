package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;

import java.util.List;

/**
 * Created by Administrator on 2019/6/10.
 */

public class ActionTotal {
    private List<Product> action;
    private List<TotalMessage> total;

    public List<Product> getAction() {
        return action;
    }

    public void setAction(List<Product> action) {
        this.action = action;
    }

    public List<TotalMessage> getTotal() {
        return total;
    }

    public void setTotal(List<TotalMessage> total) {
        this.total = total;
    }
}
