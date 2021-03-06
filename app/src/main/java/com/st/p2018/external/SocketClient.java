package com.st.p2018.external;

import android.os.Bundle;
import android.os.Message;
import android.widget.Toast;

import com.st.p2018.activity.PersonActivity;
import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Created by Administrator on 2019/1/31.
 */

public class SocketClient extends Thread {
    public static Socket socket = null;
    public static OutputStream outStream = null;
    public static InputStream inStream = null;
    private static Logger logger = Logger.getLogger(SocketClient.class);
    private static Lock myLock=new ReentrantLock(true);



    public void run() {
        try {
            new SHThread().start();
            new SendAgainThread().start();
        } catch (Exception e) {
            logger.error("socket通信出错",e);
        }
    }

    public static void closeSocket() {
        try {
            Cache.threadFlag="";
            if (inStream != null) {
                inStream.close();
                inStream=null;
            }
            if (outStream != null) {
                outStream.close();
                outStream=null;
            }
            if (socket != null) {
                socket.close();
                socket=null;
            }

        } catch (Exception e) {
            logger.error("关闭流和socket出错",e);
        }
    }

    /**
     * 发送数据
     * @param value
     */
    public static boolean send(String value ) {
        boolean bl=false;
        try{
            myLock.lock();
            value="%start%"+value+"%end%";
            try {
                if(socket!=null && !socket.isClosed()) {
                    logger.info("客户端发送数据:"+value);
                    outStream.write(value.getBytes("UTF-8"));
                    outStream.flush();
                    logger.info("发送完成");
                    bl=true;

                }else {
                    logger.info("连接失败，不发送数据了:"+value);
                    bl=false;
                    closeSocket();
                }

            } catch (IOException e) {
                bl=false;
                logger.error("发送数据出错",e);
            }
        }catch (Exception e){
            bl=false;
            logger.error("发送数据出错",e);
        }finally {
            myLock.unlock();

        }
        return bl;

    }

    class SHThread extends Thread{
        public void run() {
            while(true) {
                try {
                    if (socket == null || socket.isClosed()) {
                        socket=new Socket();
                        //socket = new Socket(Cache.ServerIP, Cache.ServerPort);
                        socket.connect(new InetSocketAddress(Cache.ServerIP, Cache.ServerPort), 3000);
                        logger.info("连接第三方平台成功：ip"+Cache.ServerIP+" 端口号："+Cache.ServerPort);
                        logger.info("本机ip地址："+socket.getLocalAddress().getHostAddress());
                        logger.info("本机端口号："+socket.getLocalPort());
                        inStream = socket.getInputStream();
                        outStream = socket.getOutputStream();
                        //数据接收线程
                        String id= UUID.randomUUID().toString();
                        Cache.threadFlag=id;
                        new ReadThread(id).start();
                        //心跳发送线程
                        new HeartThread(id).start();

                    }

                }catch(Exception e) {
                    logger.info("连接第三方平台失败");
                    socket=null;
                    if(Cache.myHandleAccess!=null){
                        Message message = Message.obtain(Cache.myHandleAccess);
                        Bundle bund = new Bundle();
                        bund.putString("ui","connectfail");
                        message.setData(bund);
                        Cache.myHandleAccess.sendMessage(message);
                    }else if(Cache.myHandleLockScreen!=null){
                        Message message = Message.obtain(Cache.myHandleLockScreen);
                        Bundle bund = new Bundle();
                        bund.putString("ui","connectfail");
                        message.setData(bund);
                        Cache.myHandleLockScreen.sendMessage(message);
                    }else if(Cache.myHandleSick!=null){
                        Message message = Message.obtain(Cache.myHandleSick);
                        Bundle bund = new Bundle();
                        bund.putString("ui","connectfail");
                        message.setData(bund);
                        Cache.myHandleSick.sendMessage(message);
                    }else if(Cache.myHandle!=null){
                        Message message = Message.obtain(Cache.myHandle);
                        Bundle bund = new Bundle();
                        bund.putString("ui","connectfail");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                    }
                }
                try {
                    Thread.sleep(5000);
                }catch(Exception e) {

                }
            }
        }
    }

}
