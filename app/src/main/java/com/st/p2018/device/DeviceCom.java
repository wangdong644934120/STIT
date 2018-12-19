package com.st.p2018.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.st.p2018.dao.PZDao;
import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/11/8.
 */

public class DeviceCom extends Thread{

    private Logger logger = Logger.getLogger(this.getClass());

    private String value="1";
    public void run(){
        openCom();
        //byte[] byDevice=HCProtocol.ST_GetDeviceInfo();
        //JXDevice(byDevice);
        //new HeartThread().start();
        //new TimeThread().start();
       //new DataThread().start();
    }

    private void JXDevice(byte[] data){
        if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x11
                && data[3] == (byte) 0x05 && data[4] == (byte) 0x0E ) {
            HashMap<String,String> map=new HashMap<String,String>();
            String gx="Ⅰ型";
            if(data[4]==0x01){
                //1型柜
                gx="Ⅰ型";
            }else if(data[4]==0x02){
                //11型柜
                gx="Ⅱ型";
                Cache.gx=gx;
            }
            map.put("gx",gx);
            map.put("gc1","0");
            map.put("gc2","0");
            map.put("gc3","0");
            map.put("gc4","0");
            map.put("gc5","0");
            map.put("gc6","0");
            String qygc=DataTypeChange.getBit(data[13]);
            for(int i=qygc.length();i>=0;i--){
                if(qygc.substring(qygc.length()-1,qygc.length()).equals("1")){
                    map.put("gc"+i,"1");
                }else{
                    map.put("gc"+i,"0");
                }
            }
            PZDao pzDao=new PZDao();
            pzDao.updatePZByDevice(map);
            logger.info("获取设备信息完成");
            Cache.hwxc1=(map.get("1").equals("1"))?true:false;
            Cache.hwxc2=(map.get("2").equals("1"))?true:false;
            Cache.hwxc3=(map.get("3").equals("1"))?true:false;
            Cache.hwxc4=(map.get("4").equals("1"))?true:false;
            Cache.hwxc5=(map.get("5").equals("1"))?true:false;
            Cache.hwxc6=(map.get("6").equals("1"))?true:false;
            sendData("状态:柜体型号："+gx);
            sendData("状态:柜层启用："+map.get("gc1")+map.get("gc2")+map.get("gc3")+map.get("gc4")+map.get("gc5")+map.get("gc6"));
        }
    }
    private void openCom(){
        try{
            HCProtocol.ST_OpenCom();
            Thread.sleep(4000);
        }catch (Exception e){
            sendData("打开串口失败");
            logger.error("打开串口失败",e);

        }
    }

    private  void sendData(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("ts",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    private  void sendDataHW(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        if(value.equals("1")){
            value="0";
        }else if(value.equals("0")){
            value="1";
        }
        data.putString("hw",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

}
