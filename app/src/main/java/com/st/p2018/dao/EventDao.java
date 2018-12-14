package com.st.p2018.dao;

import com.st.p2018.database.DataBaseExec;
import com.st.p2018.entity.Event;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/12/14.
 */

public class EventDao {
    public boolean addEvent(Event event){
        String sql="insert into stit_t_event (id,code,eventType,content,wz,time) values (?,?,?,?,?,?)";
        String[] args= new String[]{event.getId(),event.getCode(),event.getEventType(),event.getContent(),event.getWz(),String.valueOf(event.getTime())};
        return DataBaseExec.execOther(sql, args);
    }
}
