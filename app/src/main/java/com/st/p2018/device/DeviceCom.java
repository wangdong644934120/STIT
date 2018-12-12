package com.st.p2018.device;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
    Lock myLock=new ReentrantLock(true);
//    SerialPort sp;
    private String value="1";
    public void run(){

        new HeartThread().start();
        new TimeThread().start();
        new DataThread().start();
//        A a = new A();
//        a.start();
//        B b=new B();
//        b.start();
//        if(1==1){
//            return;
//        }

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
                boolean bl=HCProtocol.ST_SetTime();
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

    class A extends Thread{
        public void run(){
            while(true){
                try{
                    System.out.println("A开始获取锁");
                    myLock.lock();
                    System.out.println("A成功获取锁");
                    System.out.println("A");
                }catch (Exception e){

                }finally {
                    System.out.println("A开始释放锁");
                    myLock.unlock();
                    System.out.println("A成功释放锁");
                }
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }

            }

        }
    }

    class B extends Thread{
        public void run(){
            while(true){
                try{
                    System.out.println("B开始获取锁");
                    myLock.lock();
                    System.out.println("B成功获取锁");
                    System.out.println("B");
                }catch (Exception e){

                }finally {
                    System.out.println("B开始释放锁");
                    myLock.unlock();
                    System.out.println("B成功释放锁");
                }
                try{
                    Thread.sleep(1000);
                }catch (Exception e){

                }
            }

        }
    }
}
