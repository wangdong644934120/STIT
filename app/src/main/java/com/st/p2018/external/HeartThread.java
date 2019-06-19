package com.st.p2018.external;

import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.util.UUID;


/**
 * Created by Administrator on 2019/4/13.
 */

public class HeartThread extends Thread {

    private Logger logger = Logger.getLogger(this.getClass());

    String id;

    public HeartThread(String id){
        this.id=id;
    }

    public void run(){
        while(true){
            if(!Cache.threadFlag.equals(id)){
                logger.info("退出心跳线程");
                break;
            }
            try{
                String number= UUID.randomUUID().toString();
                String sendValue="{\"order\":\"heart\",\"number\":\""+number+"\",\"data\":\""+ Cache.appcode+"\"}";
                CacheSend.addSend(number,sendValue,SendMessage.HEART);
                SocketClient.send(sendValue);
            }catch (Exception e){
                logger.info("心跳发送失败");
            }

            try{
                Thread.sleep(30000);
            }catch (Exception e){

            }

        }
    }
}
