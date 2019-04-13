package com.st.p2018.external;

import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.util.UUID;


/**
 * Created by Administrator on 2019/4/13.
 */

public class HeartThread extends Thread {

    private Logger logger = Logger.getLogger(this.getClass());
    int failTime=0;

    public void run(){
        while(true){
            try{
                String number= UUID.randomUUID().toString();
                String sendValue="{\"order\":\"heart\",\"number\":\""+number+"\",\"data\":\""+ Cache.ipmac+"\"}";
                Cache.socketClient.send(sendValue);
            }catch (Exception e){
                failTime=failTime+1;
            }
            if(failTime>=3){
                logger.info("心跳连续发送三次失败，重新连接服务器");
                SocketClient.closeSocket();
                break;
            }
            try{
                Thread.sleep(10000);
            }catch (Exception e){

            }

        }
    }
}
