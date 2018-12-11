package com.st.p2018.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;


import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/11/8.
 */

public class DeviceCom extends Thread{

    private Logger logger = Logger.getLogger(this.getClass());
//    SerialPort sp;
    private String value="1";
    public void run(){
        openCom();
        while(true){
            try{
//                byte[] bb= new byte[2];
//                bb[0]=0x00;
//                bb[1]=0x01;
                //sp.testSendCOM(bb);
//                byte[] g=sp.testGetCOM();
//                for(int i=0;i<g.length;i++){
//                    System.out.println(g[i]);
//                }
                boolean bl=HCProtocol.SetTime();
                System.out.println(bl);
                sendDataHW();
            }catch(Exception e){

            }
            try{
                Thread.sleep(500);
            }catch(Exception e){

            }

        }
//        int i=0;
//        while(true){
//            i=i+1;
//            try{
//                Message message = Message.obtain(Cache.myHandle);
//                Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
//                data.putString("ts",String.valueOf(i));
//                message.setData(data);
//                Cache.myHandle.sendMessage(message);
//                //message.sendToTarget();
//                Thread.sleep(1000);
//            }catch (Exception e){
//                try{
//                    Thread.sleep(10000);
//                }catch (Exception ex){
//
//                }
//            }
//
//        }
    }

    private void openCom(){
        try{
            Thread.sleep(4000);
            HCProtocol.ST_OpenCom();
            //HCProtocol.sendHeart();
            //sp = new SerialPort(new File("/dev/ttyS1"), 9600, 0);
            int a=0;
            sendData("打开串口成功");
//            Thread.sleep(4000);
//            sendData("正在打开串口...");
//            Thread.sleep(2000);
//            sendData("串口打开成功...");
            Thread.sleep(2000);
            sendData("正在初始化设备...");
            Thread.sleep(2000);
            sendData("初始化设备失败...");
//            Thread.sleep(2000);
//            sendData("正在连接服务器...");
//            Thread.sleep(2000);
//            sendData("连接服务器失败...");
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
