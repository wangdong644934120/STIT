package com.st.p2018.device;

/**
 * Created by Administrator on 2018/12/6.
 */

public class DataTypeChangeException extends Exception {
    private int id;

    public DataTypeChangeException(String message, int id) {
        super(message);
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
