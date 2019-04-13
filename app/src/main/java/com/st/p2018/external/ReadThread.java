package com.st.p2018.external;


import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2019/4/13.
 */

public class ReadThread extends  Thread {

    private Logger logger = Logger.getLogger(this.getClass());

    public void run() {
        String jg="";
        while(true) {
            try {
                byte[] bydata = new byte[1024];
                int r=SocketClient.inStream.read(bydata);
                if(r>-1) {
                    String str=new String(bydata).trim();
                    if(str.contains("%end%")) {
                        String value =str.substring(0,str.indexOf("%end%"));
                        jg=jg+value;
                        jg=jg.replaceAll("%start%", "");
                        //处理结果数据
                        new DealReceive(new String(jg)).start();
                        value=str.substring(str.indexOf("%end%")+5, str.length());
                        jg=value;
                    }else {
                        jg=jg+str;
                    }
                }

            }catch(Exception e) {
                logger.error("读取流出错",e);
                break;
            }
            try{
                Thread.sleep(10);
            }catch (Exception e){
            }

        }
    }
}
