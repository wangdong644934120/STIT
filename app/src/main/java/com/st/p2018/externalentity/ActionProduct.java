package com.st.p2018.externalentity;

import com.st.p2018.entity.Product;
import java.io.Serializable;
import java.util.List;

public class ActionProduct implements Serializable{
    private String order;
    private String number;
    private String message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }
}
