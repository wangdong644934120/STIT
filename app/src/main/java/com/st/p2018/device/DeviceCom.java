package com.st.p2018.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.st.p2018.dao.PZDao;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

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
        byte[] byDevice=HCProtocol.ST_GetDeviceInfo();
        if(byDevice.length==0){
            logger.info("获取设备信息无返回数据");
        }
        JXDevice(byDevice);
        boolean bl=HCProtocol.ST_GetWorkModel();
        if(bl){
            String zmd="";
            if(Cache.zmd==0){
                zmd="灯自动";
            }else if(Cache.zmd==1){
                zmd="灯常开";
            }else if(Cache.zmd==2){
                zmd="灯常关";
            }
            logger.info("状态:照明灯:"+zmd);
            logger.info("状态:盘存方式:"+(Cache.pc==0?"全部盘存":"触发盘存"));
            logger.info("状态:盘存次数:"+Cache.pccs);
        }else{
            logger.info("报警:获取工作模式失败");
        }
       new DataThread().start();
        if(Cache.external){
            Cache.getHCCS=3;
            if(HCProtocol.ST_GetAllCard()){
                logger.info("加载界面下发获取所有耗材成功");
            }else{
                logger.info("加载界面下发获取所有耗材失败");
            }
        }

    }

    private void JXDevice(byte[] data){
        if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x11
                && data[3] == (byte) 0x05  ) {
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
            //产品序列号
            byte[] cpxlh = new byte[6];
            System.arraycopy(data, 5, cpxlh, 0, 6);
            for(byte b : cpxlh){
                Cache.cpxlh=Cache.cpxlh+DataTypeChange.getHeight4(b);
                Cache.cpxlh=Cache.cpxlh+DataTypeChange.getLow4(b);
            }
            logger.info("产品序列号："+Cache.cpxlh);

            //硬件版本号
            byte[] yjbbh=new byte[1];
            System.arraycopy(data,6,yjbbh,0,1);
            Cache.yjbbh="V"+DataTypeChange.getHeight4(yjbbh[0])+"."+DataTypeChange.getLow4(yjbbh[0]);
            logger.info("硬件版本号："+Cache.yjbbh);
            //固件版本号
            byte[] gjbbh=new byte[1];
            System.arraycopy(data,7,gjbbh,0,1);
            Cache.gjbbh="V"+DataTypeChange.getHeight4(gjbbh[0])+"."+DataTypeChange.getLow4(gjbbh[0]);
            logger.info("固件版本号："+Cache.gjbbh);

            String qygc=DataTypeChange.getBit(data[13]);
            logger.info("获取设备信息完成");
            Cache.gcqy1=(qygc.substring(7,8).equals("1"))?true:false;
            Cache.gcqy2=(qygc.substring(6,7).equals("1"))?true:false;
            Cache.gcqy3=(qygc.substring(5,6).equals("1"))?true:false;
            Cache.gcqy4=(qygc.substring(4,5).equals("1"))?true:false;
            Cache.gcqy5=(qygc.substring(3,4).equals("1"))?true:false;
            Cache.gcqy6=(qygc.substring(2,3).equals("1"))?true:false;
            logger.info("状态:柜体型号："+gx);
            logger.info("状态:柜层启用(最后为第一层抽)："+qygc);
        }else{
            sendData("报警:获取设备信息失败");
        }
        sendGX();
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

    //根据柜型更新缩略图
    private  void sendGX(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("gx","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
}
