package com.st.p2018.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.st.p2018.util.Cache;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/11/8.
 */

public class DeviceCom extends Thread{


    public void run(){
        openCom();
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
            sendData("正在打开串口...");
            Thread.sleep(2000);
            sendData("串口打开成功...");
            Thread.sleep(2000);
            sendData("正在初始化设备...");
            Thread.sleep(2000);
            sendData("初始化设备失败...");
            Thread.sleep(2000);
            sendData("正在连接服务器...");
            Thread.sleep(2000);
            sendData("连接服务器失败...");
        }catch (Exception e){

        }
    }

    private  void sendData(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("ts",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
}
