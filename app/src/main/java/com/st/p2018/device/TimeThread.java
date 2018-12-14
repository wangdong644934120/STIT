package com.st.p2018.device;

import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2018/12/12.
 */

public class TimeThread extends Thread {
    private Logger logger = Logger.getLogger(this.getClass());
    public void run(){
        while(true){
            try{
                if(HCProtocol.ONLINE==1){
                    boolean bl=HCProtocol.ST_SetTime();
                    if(bl){
                        logger.info("时间同步成功");
                    }else{
                        logger.info("时间同步失败");
                    }
                }else{
                    logger.info("设备不在线，取消本次时间同步");
                }

            }catch (Exception e){
                logger.error("时间同步出错",e);
            }
            try{
                //Thread.sleep(60000*15);
                Thread.sleep(10000);
            }catch (Exception e){
                logger.error("线程等待出错",e);
            }
        }
    }
}
