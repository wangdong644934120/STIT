package com.st.p2018.device;


import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2018/12/12.
 */

public class HeartThread extends Thread{
    private Logger logger = Logger.getLogger(this.getClass());
    public void run(){
        while(true){
            try{
                boolean bl=HCProtocol.ST_SendHeart();
                if(bl){
                    logger.info("发送心跳成功");
                    HCProtocol.ONLINE=1;
                    HCProtocol.NOTONLINECOUNT=0;
                }else{
                    logger.info("发送心跳失败");
                    HCProtocol.NOTONLINECOUNT=HCProtocol.NOTONLINECOUNT+1;
                    if(HCProtocol.NOTONLINECOUNT>=3){
                        HCProtocol.ONLINE=0;
                        HCProtocol.NOTONLINECOUNT=3;
                    }
                }
            }catch (Exception e){
                logger.error("发送心跳出错",e);
            }
            try{
                Thread.sleep(30000);
            }catch (Exception e){
                logger.error("线程等待出错",e);
            }
        }
    }
}
