package com.st.p2018.externalentity;

/**
 * Created by Administrator on 2019/7/29.
 */

public class ExternalPower {
    private String order;
    private String number;
    private String message;
    private PowerData data;

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

    public PowerData getData() {
        return data;
    }

    public void setData(PowerData data) {
        this.data = data;
    }


}
