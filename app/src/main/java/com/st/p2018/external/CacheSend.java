package com.st.p2018.external;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2019/4/16.
 */

public class CacheSend {

    private static Logger logger = Logger.getLogger(CacheSend.class);
    private static Lock myLock=new ReentrantLock(true);
    private static List<SendMessage> list=new ArrayList<SendMessage>();

    /**
     * 添加发送数据到缓存
     * @param key uuid
     * @param value 发送内容
     */
    public static void addSend(String key,String value,int messageType){
        try{
            myLock.lock();
            if(list.size()>100){
                logger.info("待发送数据缓存数据超过100条，丢弃此条数据");
                return;
            }
            SendMessage sm=new SendMessage();
            sm.setNumber(key);
            sm.setTime(System.currentTimeMillis());
            sm.setCount(1);
            sm.setMessage(value);
            sm.setMessageType(messageType);
            list.add(sm);
        }catch (Exception e){
            logger.error("添加发送数据到缓存出错",e);
        }finally {
            myLock.unlock();
        }
    }

    public static List<SendMessage> getSend(){
        try{
            myLock.lock();
           return list;
        }catch (Exception e){
            logger.error("获取缓存发送数据出错",e);
            return list;
        }finally {
            myLock.unlock();
        }
    }

    public static void removeSend(String number){
        try{
            myLock.lock();
           for(SendMessage sm : list){
               if(sm.getNumber().equals(number)){
                   list.remove(sm);
                   break;
               }
           }
        }catch (Exception e){
            logger.error("删除缓存发送数据出错",e);
        }finally {
            myLock.unlock();
        }
    }

    public static void updateCount(String number,int count){
        try{
            myLock.lock();
            for(SendMessage sm : list){
                if(sm.getNumber().equals(number)){
                    sm.setCount(count);
                    sm.setTime(System.currentTimeMillis());
                    break;
                }
            }
        }catch (Exception e){
            logger.error("更新缓存发送数据次数出错",e);
        }finally {
            myLock.unlock();
        }
    }

    public static void removeHeart(){
        try{
            myLock.lock();
            List<SendMessage> listFlag = new ArrayList<SendMessage>();
            for(SendMessage sm : list){
                if(sm.getMessageType()==SendMessage.HEART){
                    listFlag.add(sm);
                }
            }
            logger.info("移除缓存心跳数据个数："+listFlag.size());
            for(SendMessage sm : listFlag){
                list.remove(sm);
            }
        }catch (Exception e){
            logger.error("更新缓存发送数据次数出错",e);
        }finally {
            myLock.unlock();
        }
    }
}
