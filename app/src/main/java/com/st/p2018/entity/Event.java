package com.st.p2018.entity;

/**
 * Created by Administrator on 2018/12/14.
 */

public class Event {
    public static String EVENTTYPE_SKOPENDOOR="0";
    public static String EVENTTYPE_ZWOPENDOOR="1";
    public static String EVENTTYPE_PUT="2";
    public static String EVENTTYPE_GET="3";
    private String id;
    private String code;
    private String eventType;
    private String content;
    private String wz;
    private long time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWz() {
        return wz;
    }

    public void setWz(String wz) {
        this.wz = wz;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
