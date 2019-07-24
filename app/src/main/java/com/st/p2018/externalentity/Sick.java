package com.st.p2018.externalentity;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/7/17.
 */

public class Sick implements Serializable{

    private String name; //患者姓名
    private String code; //患者编号
    private String dept; //手术部门
    private String operaid; //手术id
    private String operaname; //手术名称
    private String time; //手术时间

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getOperaid() {
        return operaid;
    }

    public void setOperaid(String operaid) {
        this.operaid = operaid;
    }

    public String getOperaname() {
        return operaname;
    }

    public void setOperaname(String operaname) {
        this.operaname = operaname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
