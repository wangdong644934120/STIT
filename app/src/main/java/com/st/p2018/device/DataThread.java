package com.st.p2018.device;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/12/12.
 */

public class DataThread extends Thread {
    private Logger logger = Logger.getLogger(this.getClass());
    public void run(){
        while(true){
            try{
                HashMap<String,String> map = HCProtocol.ST_GetWorkModel();
                //刷卡器
//                if(data[4]==0x00){
//                    map.put("skq","0");
//                }else{
//                    map.put("skq","1");
//                }
//                //指纹传感器
//                if(data[5]==0x00){
//                    map.put("zwcgq","0");
//                }else{
//                    map.put("zwcgq","1");
//                }
//                //门状态传感器
//                if(data[6]==0x00){
//                    map.put("mztcgq","0");
//                }else{
//                    map.put("mztcgq","1");
//                }
//                //电控锁
//                if(data[7]==0x00){
//                    map.put("dks","0");
//                }else{
//                    map.put("dks","1");
//                }
//                //红外/行程开关
//                if(data[8]==0x00){
//                    map.put("hwxckg","0");
//                }else{
//                    map.put("hwxckg","1");
//                }
//                //照明灯
//                if(data[9]==0x00){
//                    map.put("zmd","0");
//                }else{
//                    map.put("zmd","1");
//                }
//                //RFID读写器
//                if(data[10]==0x00){
//                    map.put("rfid","0");
//                }else{
//                    map.put("rfid","1");
//                }
            }catch (Exception e){
                logger.error("获取数据出错",e);
            }

            try{
                Thread.sleep(100);
            }catch (Exception e){
                logger.error("获取数据线程等待出错",e);
            }



        }
    }

    private  void sendTS(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("ts",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    private  void sendZT(String value){
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
}
