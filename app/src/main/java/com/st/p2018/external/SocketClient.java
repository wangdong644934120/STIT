package com.st.p2018.external;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 * Created by Administrator on 2019/1/31.
 */

public class SocketClient extends Thread {
    private static Socket socket = null;
    private static OutputStream outStream = null;
    private static InputStream inStream = null;
    private static Logger logger = Logger.getLogger(SocketClient.class);


    public void run() {
        try {
            new SHThread().start();
        } catch (Exception e) {
            logger.error("socket通信出错",e);
        }
    }

    private static void closeSocket() {
        try {
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
        value="%start%"+value+"%end%";
        try {
            if(socket!=null && !socket.isClosed()) {
                outStream.write(value.getBytes());
                outStream.flush();
                logger.info("发送成功:"+value);
            }else {
               logger.info("连接失败，不发送数据了:"+value);
                closeSocket();
            }

        } catch (IOException e) {
           logger.error("发送数据出错",e);
        }

    }

    class ReadThread extends Thread{
        public void run() {
            String jg="";
            while(true) {
                try {
                    byte[] bydata = new byte[1024];
                    int r=inStream.read(bydata);
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
                    closeSocket();
                    break;
                }

            }
        }
    }

    class SHThread extends Thread{
        public void run() {
            while(true) {
                try {
                    if (socket == null || socket.isClosed()) {
                        socket = new Socket("127.0.0.1", 5000);
                        logger.info("连接成功");
                        inStream = socket.getInputStream();
                        outStream = socket.getOutputStream();
                        new ReadThread().start();
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
