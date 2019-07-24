package com.st.p2018.external;


import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/4/13.
 */

public class ReadThread extends  Thread {

    private Logger logger = Logger.getLogger(this.getClass());
    String id;
    public ReadThread(String id){
        this.id=id;
    }
    public void run() {

        List<byte[]> listBY=new ArrayList<byte[]>();
        while(true) {
            if(!Cache.threadFlag.equals(id)){
                logger.info("退出读取数据线程");
                break;
            }
            try {
                byte[] bydata = new byte[1024];
                int r=SocketClient.inStream.read(bydata);
                if(r>-1) {
                    String str=new String(bydata,"UTF-8").trim();
                    if(str.startsWith("%start%") && str.endsWith("%end%")){
                        //接收了一条完整数据
                        String jg=str.replaceAll("%start%", "").replaceAll("%end%", "");
                        listBY.clear();
                        //处理结果数据
                        new DealReceive(jg).start();
                    }else if(str.startsWith("%start%") && !str.endsWith("%end%")) {
                        //接收数据不完整，只接收第一个包
                        listBY.add(bydata);
                    }else if (!str.startsWith("%start%") && !str.endsWith("%end%")){
                        //接收数据不完整，接收到第二个包,后面还有包
                        listBY.add(bydata);
                        //处理数据
                    }else if(!str.startsWith("%start%") && str.endsWith("%end%")){
                        //接收数据完成，接收到最后一个包
                        listBY.add(bydata);
                        //处理结果数据
                        int size=0;
                        for(byte[] bb : listBY){
                            size=size+bb.length;
                        }
                        byte[] bytes= new byte[size];
                        int count=0;
                        for(byte[] bb : listBY){
                            for(int i=0;i<bb.length;i++){
                                bytes[count]=bb[i];
                                count=count+1;
                            }
                        }
                        String jg=new String(bytes,"UTF-8").trim();
                        jg=jg.replaceAll("%start%", "").replaceAll("%end%", "");
                        new DealReceive(jg).start();
                        listBY.clear();
                    }
                }

            }catch(Exception e) {
                logger.error("读取流出错",e);
            }
            try{
                Thread.sleep(10);
            }catch (Exception e){
            }

        }
    }
}
