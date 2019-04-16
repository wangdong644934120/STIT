package com.st.p2018.external;

import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2019/4/16.
 */

public class SendAgainThread extends Thread {
    private Logger logger = Logger.getLogger(this.getClass());
    public void run(){

            while(true){
                try{
                    List<SendMessage> list = new ArrayList<SendMessage>();
                    list.addAll(CacheSend.getSend());
                    for(SendMessage sm : list){
                        if(sm.getMessageType()==SendMessage.HEART && sm.getCount()>3){
                            //心跳数据，发送大于3次，重置网络连接
                            logger.info("心跳中出现3次发送失败，重置网络连接");
                            SocketClient.closeSocket();
                            //移除所有心跳发送数据
                            CacheSend.removeHeart();
                            break;
                        }
                        if(!Cache.threadFlag.equals("") && sm.getCount()>3){
                            //发送超过3次，没有返回发送成功，将该数据删除
                            CacheSend.removeSend(sm.getNumber());
                            logger.info("网络正常情况，超过3次发送失败，取消发送："+sm.getMessage());
                        }
                        if(!Cache.threadFlag.equals("") && System.currentTimeMillis()-sm.getTime()>10000){
                            //10秒钟没有返回发送成功信息,再次发送数据
                            CacheSend.updateCount(sm.getNumber(),sm.getCount()+1);
                            Cache.socketClient.send(sm.getMessage());
                        }
                    }
                }catch (Exception e){
                    logger.error("重复发送缓存数据出错",e);
                }
                try{
                    Thread.sleep(2000);
                }catch (Exception e){

                }
            }



    }
}
