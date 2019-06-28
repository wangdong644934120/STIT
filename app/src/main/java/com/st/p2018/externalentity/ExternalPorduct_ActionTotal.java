package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/6/10.
 */

public class ExternalPorduct_ActionTotal {
    private List<Product> action = new ArrayList<Product>();
    private List<ActionTotal_TotalMessage> total=new ArrayList<ActionTotal_TotalMessage>();

    public List<Product> getAction() {
        return action;
    }

    public void setAction(List<Product> action) {
        this.action = action;
    }

    public List<ActionTotal_TotalMessage> getTotal() {
        return total;
    }

    public void setTotal(List<ActionTotal_TotalMessage> total) {
        this.total = total;
    }
}
