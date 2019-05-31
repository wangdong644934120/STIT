package com.st.p2018.external;

import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    public static void send(String value ) {
        try{
            myLock.lock();
            value="%start%"+value+"%end%";
            try {
                if(socket!=null && !socket.isClosed()) {
                    logger.info("客户端发送数据:"+value);
                    outStream.write(value.getBytes());
                    outStream.flush();

                }else {
                    logger.info("连接失败，不发送数据了:"+value);
                    closeSocket();
                }

            } catch (IOException e) {
                logger.error("发送数据出错",e);
            }
        }catch (Exception e){
            logger.error("发送数据出错",e);
        }finally {
            myLock.unlock();
        }


    }

    class SHThread extends Thread{
        public void run() {
            while(true) {
                try {
                    if (socket == null || socket.isClosed()) {
                        socket = new Socket(Cache.ServerIP, Cache.ServerPort);
                        logger.info("连接第三方平台成功：ip"+Cache.ServerIP+" 端口号："+Cache.ServerPort);
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
                }
                try {
                    Thread.sleep(3000);
                }catch(Exception e) {

                }
            }
        }
    }

}
