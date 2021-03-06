package com.st.p2018.external;

/**
 * Created by Administrator on 2019/4/16.
 */

public class SendMessage {
    public static int HEART=0; //心跳
    public static int PATIENT=1; //患者信息
    public static int CODE=2;//柜号配置
    public static int CONFIG=3;//设备配置
    public static int DEVICEINFO=4;//设备信息
    public static int PERSON=5;//指纹配置
    public static int POWER=6;//权限信息
    public static int PRODUCT=7;//耗材上传
    public static int TOTAL=8;//耗材统计
    public static int PATIENTPRODUCT=9;//耗材确认

    private String number;  //标记
    private long time;  //产生时间
    private int count; //发送次数
    private String message; //发送内容
    private int messageType;    //内容类型

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }
}
