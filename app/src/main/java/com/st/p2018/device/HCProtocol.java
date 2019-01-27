package com.st.p2018.device;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.dao.PZDao;
import com.st.p2018.util.Cache;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/12/6.
 */

public class HCProtocol {
    private static Logger logger=Logger.getLogger(HCProtocol.class);
    private static SerialPort sp;
    private static Lock myLock=new ReentrantLock(true);
    public static int ONLINE=1;
    public static int NOTONLINECOUNT=0;
    //打开串口
    public static int ST_OpenCom() {
        try{
            sp = new SerialPort(new File("/dev/ttyS1"), 38400, 0);
        }catch(Exception e){
            logger.error("打开串口出错",e);
            return -1;
        }
        return 0;
    }
    //关闭串口
    public static int ST_CloseCom(){
        try{
            if(sp!=null){
                sp.close();
            }
        }catch (Exception e){
            logger.error("关闭串口出错",e);
            return -1;
        }
        return 0;
    }

    /**
     * 心跳发送
     */
    public static boolean ST_SendHeart(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x01};

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x01 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("发送心跳出错",e);
            return false;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 软复位
     */
    public static boolean ST_SetSoftRest(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x03};

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x03 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("软复位出错",e);
            return false;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 时间同步
     */
    public static boolean ST_SetTime(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x09 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x04};
            byte[] bytime=new byte[6];
            Calendar calendar = Calendar.getInstance();
            int year=calendar.get(Calendar.YEAR)-2000;
            int month=calendar.get(Calendar.MONTH)+1;
            int day=calendar.get(Calendar.DAY_OF_MONTH);
            int hour=calendar.get(Calendar.HOUR_OF_DAY);
            int minute=calendar.get(Calendar.MINUTE);
            int second=calendar.get(Calendar.SECOND);
            bytime[0]=(byte)year;
            bytime[1]=(byte)month;
            bytime[2]=(byte)day;
            bytime[3]=(byte)hour;
            bytime[4]=(byte)minute;
            bytime[5]=(byte)second;

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bytime);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x04 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("时间同步出错",e);
            return false;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 获取设备信息
     * @return
     */
    public static byte[]  ST_GetDeviceInfo(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x05};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);
            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            return data;
        }catch (Exception e){
            logger.error("获取设备信息出错",e);
            return null;
        }finally {
            myLock.unlock();
        }
    }

    /**
     * 配置设备信息
     * @return
     */
    public static boolean  ST_SetDeviceInfo(byte[] bydata){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x11 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x05};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);
            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGetFor2Seconds(send);
            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x05 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("获取设备信息出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    /**
     * 设置工作模式
     * @param lightModel 灯模式
     * @param cardModel 标签模式
     * @return
     */
    public static boolean ST_SetWorkModel(int lightModel,int cardModel,int cardTime){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x09 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x06};
            byte[] bydata=new byte[6];
            bydata[0]=(byte)lightModel;
            bydata[1]=(byte)cardModel;
            bydata[2]=(byte)cardTime;

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x06 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("设置工作模式出错",e);
            return false;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 获取工作模式
     * @return
     */
    public static boolean ST_GetWorkModel(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x06};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=8 && data[0] == (byte) 0x3A && data[1] == (byte) 0x09
                    && data[3] == (byte) 0x06  ) {
                Cache.zmd=data[4];
                Cache.pc=data[5];
                Cache.pccs=data[6];
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("获取工作模式出错",e);
            return false;
        }finally {
            myLock.unlock();
        }

    }
    /**
     * 获取工作状态
     * @return
     */
    public static HashMap<String,String> ST_GetWorkState(){
        try{
            myLock.lock();
            HashMap<String,String> map = new HashMap<String,String>();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x07};

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

            if (data!=null && data.length>=16 && data[0] == (byte) 0x3A && data[1] == (byte) 0x0E
                    && data[3] == (byte) 0x07 ) {
                //刷卡器
                String skq=DataTypeChange.getBit(data[4]);
                map.put("skq",skq);

                //指纹传感器
                String zwcgq=DataTypeChange.getBit(data[5]);
                map.put("zwcgq",zwcgq);

                //门状态传感器
                String mztcgq=DataTypeChange.getBit(data[6]);
                map.put("mztcgq",mztcgq);

                //电控锁
                String dks=DataTypeChange.getBit(data[7]);
                map.put("dks",dks);

                //红外/行程开关
                String hwxckg=DataTypeChange.getBit(data[8]);
                //String hwxckg=DataTypeChange.bytes2HexString(data[8]);
                map.put("hwxckg",hwxckg);

                //照明灯
                String zmd=DataTypeChange.getBit(data[9]);
                map.put("zmd",zmd);

                //RFID读写器
                String rfid=DataTypeChange.getBit(data[10]);
//                String rfid=DataTypeChange.bytes2HexString(data[10]);
                map.put("rfid",rfid);
            }
            return map;
        }catch (Exception e){
            logger.error("获取工作状态出错",e);
            return null;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 获取用户信息
     * @param power 0-卡权限，1-指纹权限
     * @return
     */
    public static String ST_GetUser(int power){
        try{
            myLock.lock();
            //根据卡或指纹确定返回数据长度
            String card="";
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x05 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x08};
            byte[] bydata=new byte[2];
            bydata[0]=(byte)power;
            bydata[1]=0;
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if(power==0){
                if (data!=null && data.length>=4 && data[0] == (byte) 0x3A && data[1] == (byte) 0x07
                        && data[3] == (byte) 0x08 ) {
                    //卡权限
                        byte[] cardby = new byte[4];
                        System.arraycopy(data, 4, cardby, 0, 4);
                        card = DataTypeChange.byteArrayToHexString(cardby);
                }
            }else if(power==1){
                byte[] cardby = new byte[2];
                System.arraycopy(data, 4, cardby, 0, 2);
                card = DataTypeChange.byteArrayToHexString(cardby);
            }
//            if (data!=null && data.length>=4 && data[0] == (byte) 0x3A && data[1] == (byte) 0x07
//                    && data[3] == (byte) 0x08 ) {
//                //卡权限
//                if(power==0){
//                    byte[] cardby = new byte[4];
//                    System.arraycopy(data, 4, cardby, 0, 4);
//                    card = DataTypeChange.byteArrayToHexString(cardby);
//                }else if(power==1){
//                    byte[] cardby = new byte[4];
//                    System.arraycopy(data, 4, cardby, 0, 4);
//                    card = DataTypeChange.byteArrayToHexString(cardby);
//                }
//
//            }
            return card;
        }catch (Exception e){
            logger.error("获取用户权限出错",e);
            return "";
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 获取盘存数据
     */
    public static HashMap<String,String> ST_GetCard(){
        HashMap<String,String> map = new HashMap<String,String>();
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x09};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=4 && data[0] == (byte) 0x3A && data[3] == (byte) 0x09 ) {
                byte[] cardby = new byte[data.length-5];
                System.arraycopy(data, 4, cardby, 0, data.length-5);
                //判断cardby内容是否为0
                for(int i=0;i<cardby.length/9;i++){
                    byte wz=cardby[i*9];
                    byte[] card=new byte[8];
                    System.arraycopy(cardby,i*9+1,card,0,8);
                    String cardS = DataTypeChange.byteArrayToHexString(card);
                    map.put(cardS.toUpperCase(),String.valueOf(wz));
                }

            }
            return map;
        }catch (Exception e){
            logger.error("读取标签盘存数据出错",e);
            return map;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 删除指纹
     * @param flag 0-删除单个指纹  1-删除所有指纹
     * @param code flag为0时，该参数有效，需要删除的指纹编号。flag为1时，传入0即可
     * @return
     */
    public static boolean ST_DeleteZW(int flag,int code){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[1] ;
            if(flag==0){
               length[0]=6;
            }else{
                length[0]=4;
            }
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x21};
            byte[] bydata=null;

            if(flag==0){
                bydata=new byte[3];
                StringBuffer s = new StringBuffer();
                String str;
                char[] b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
                while(code != 0){
                    s = s.append(b[code%16]);
                    code = code/16;
                }
                str = s.reverse().toString();
                if(str.length()==3){
                    str="0"+str;
                }else if(str.length()==2){
                    str="00"+str;
                }else  if(str.length()==1){
                    str="000"+str;
                }
                byte[] bytes = new byte[str.length() / 2];
                for(int i = 0; i < str.length() / 2; i++) {
                    String subStr = str.substring(i * 2, i * 2 + 2);
                    bytes[i] = (byte) Integer.parseInt(subStr, 16);
                }
                bydata[0]=0;
                bydata[1]=bytes[0];
                bydata[2]=bytes[1];
            }else{
                bydata=new byte[1];
                bydata[0]=1;
            }
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);


            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGetFor2Seconds(send);

            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x21 && data[4] == (byte) 0x00) {
                logger.info("设备中指纹删除成功：flag："+flag+" code:"+code);
                return true;
            }else{
                logger.info("设备中指纹删除失败：flag："+flag+" code:"+code);
                return false;
            }
        }catch (Exception e){
            logger.error("删除指纹出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    public static void ST_GetZWZT(){
        int i=13;
        boolean bl=true;
        try{
            myLock.lock();
            while(bl){
                //发送数据
                byte[] data=sp.getOnly();
                if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                        && data[3] == (byte) 0x22) {
                    if( data[4] == (byte) 0x20){
                        //指纹录入成功
                        sendZWZT("ok");
                        bl=false;
                        break;
                    }else if(data[4]==(byte)0x22){
                        //指纹录入失败
                        sendZWZT("fail");
                        bl=false;
                        break;
                    }else if(data[4]==(byte)0x21){
                        //指纹录入中
                        if(i>0){
                            sendZWZT(String.valueOf(i));
                        }

                    }

                }else if(i<=-2){
                    sendZWZT("fail");
                    bl=false;
                    break;
                }
                i=i-1;
                Thread.sleep(1000);
            }

        }catch (Exception e){
            i=-1;
            logger.error("添加指纹出错",e);
        }finally {
            Cache.zwlrNow=false;
            myLock.unlock();
        }
    }
    public static boolean ST_AddSaveZW(int code){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[]{0x06} ;
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x22};
            byte[] bydata=new byte[3];
            String hex=intToHex(code);
            if(hex.length()==1){
                hex="000"+hex;
            }else if(hex.length()==2){
                hex="00"+hex;
            }else if(hex.length()==3){
                hex="0"+hex;
            }
            bydata[0]=(byte) Integer.parseInt(hex.substring(0,2), 16);
            bydata[1]=(byte) Integer.parseInt(hex.substring(2,4), 16);
            bydata[2]=(byte)15;

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);

            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

//            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x22 ) {
                return true;
//            }else{
//                return false;
//            }
        }catch (Exception e){
            logger.error("添加指纹出错",e);
            return  false;
        }finally {
            myLock.unlock();
        }

    }

    private static  String intToHex(int n) {
        //StringBuffer s = new StringBuffer();
        StringBuilder sb = new StringBuilder(8);
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            sb = sb.append(b[n%16]);
            n = n/16;
        }
        a = sb.reverse().toString();
        return a;
    }
    /**
     * 添加指纹信息
     * @param code 工号
     * @param tzz   指纹特征值
     * @return
     */
    public static boolean ST_AddZW(int code,String tzz){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[1] ;

            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x28};
            byte[] bytzz=tzz.getBytes();
            byte[] bydata=new byte[1+bytzz.length];
            bydata[0]=(byte)code;
            System.arraycopy(bytzz,0,bydata,1,bytzz.length);
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);

            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);
            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x28 && data[4] == (byte) 0x00) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("添加指纹出错",e);
            return  false;
        }finally {
            myLock.unlock();
        }

    }


    //开门指令
    public static boolean ST_OpenDoor(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x04 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x50};
            byte[] bydata=new byte[]{0x03};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x50 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("发送开门指令出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    //开灯
    public static boolean ST_OpenLight(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x04 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x51};
            byte[] bydata=new byte[]{0x03};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x51 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("发送开灯指令出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    //关灯
    public static boolean ST_CloseLight() {
        try {
            myLock.lock();
            byte[] head = new byte[]{0x3A};
            byte[] length = new byte[]{0x04};
            byte[] deviceID = new byte[]{0x00};
            byte[] order = new byte[]{0x51};
            byte[] bydata=new byte[]{0x00};
            byte[] before = new byte[]{};
            before = DataTypeChange.byteAddToByte(before, head);
            before = DataTypeChange.byteAddToByte(before, length);
            before = DataTypeChange.byteAddToByte(before, deviceID);
            before = DataTypeChange.byteAddToByte(before, order);
            before = DataTypeChange.byteAddToByte(before, bydata);
            byte jyData = getJYData(before);

            byte[] send = DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data = sp.sendAndGet(send);

            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x51 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("发送关灯指令出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    /**
     * 指定天线号盘存
     * @param hwxc
     * @return
     */
    public static boolean ST_GetCardByChoose(int hwxc){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x05 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x32};
            byte[] bydata=new byte[2];
            bydata[0]=(byte)hwxc;
            bydata[1]=(byte)Cache.pccs;
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x32 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("指定天线号盘存出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    /**
     * 下发指令盘存所有
     * @return
     */
    public static boolean ST_GetAllCard(){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x32};
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

            if (data!=null && data.length>=5 && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                    && data[3] == (byte) 0x32 && data[4] == (byte) 0x00 ) {
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            logger.error("盘存所有标签下发出错",e);
            return false;
        }finally {
            myLock.unlock();
        }
    }

    //校验数据
    public static byte getJYData(byte[] datas){

        byte temp=datas[0];
        for (int i = 1; i <datas.length; i++) {
            temp ^=datas[i];
        }

        temp=(byte)~temp;

        return temp;
    }

    public static  void sendData(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("ts",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    public static  void sendZWZT(String value){
        Message message = Message.obtain(Cache.myHandleKH);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("zw",value);
        message.setData(data);
        Cache.myHandleKH.sendMessage(message);
    }
}
