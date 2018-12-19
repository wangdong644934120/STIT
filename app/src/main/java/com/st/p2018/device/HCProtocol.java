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
            sendData("状态:心跳发送");
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="心跳返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:心跳发送无数据返回");
            }
            return true;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x01 && data[4] == (byte) 0x00 ) {
//                return true;
//            }else{
//                return false;
//            }
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
            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
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
            sendData("状态:发送时间同步");
            byte[] data=sp.sendAndGet(send);

            //--
            String fh="发送时间同步返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:获取设备信息发送无数据返回");
            }
            return true;
            //--
            //---
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x04 && data[4] == (byte) 0x00 ) {
//                return true;
//            }else{
//                return false;
//            }
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
            byte[] length = new byte[] { 0x08 };
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
            sendData("状态:获取设备信息发送");
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="获取设备信息返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:获取设备信息发送无数据返回");
            }
            return null;
            //--
//            return data;
        }catch (Exception e){
            logger.error("获取设备信息出错",e);
            return null;
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
            byte[] length = new byte[] { 0x08 };
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
            sendData("状态:设置工作模式信息发送");
            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            byte[] data=sp.sendAndGet(send);

            //--
            String fh="设置工作模式返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:设置工作模式无数据返回");
            }
            return true;
            //--

//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x06 && data[4] == (byte) 0x00 ) {
//                return true;
//            }else{
//                return false;
//            }
        }catch (Exception e){
            logger.error("设置工作模式出错",e);
            return false;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 获取工作状态
     * @return
     */
    public static HashMap<String,String> ST_GetWorkModel(){
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
            sendData("状态:获取工作状态");
            //发送数据
            byte[] data=sp.sendAndGet(send);

            //--
            String fh="获取工作状态返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:获取工作状态无数据返回");
            }
            return null;
            //--

//            if (data!=null && data.length>=16 && data[0] == (byte) 0x3A && data[1] == (byte) 0x0E
//                    && data[3] == (byte) 0x07 ) {
//                //刷卡器
//                String skq=DataTypeChange.bytes2HexString(data[4]);
//                map.put("skq",skq);
//
//                //指纹传感器
//                String zwcgq=DataTypeChange.bytes2HexString(data[5]);
//                map.put("zwcgq",zwcgq);
//
//                //门状态传感器
//                String mztcgq=DataTypeChange.bytes2HexString(data[6]);
//                map.put("mztcgq",mztcgq);
//
//                //电控锁
//                String dks=DataTypeChange.bytes2HexString(data[7]);
//                map.put("dks",dks);
//
//                //红外/行程开关
//                String hwxckg=DataTypeChange.getBit(data[8]);
//                //String hwxckg=DataTypeChange.bytes2HexString(data[8]);
//                map.put("hwxckg",hwxckg);
//
//                //照明灯
//                String zmd=DataTypeChange.bytes2HexString(data[9]);
//                map.put("zmd",zmd);
//
//                //RFID读写器
//                String rfid=DataTypeChange.getBit(data[10]);
////                String rfid=DataTypeChange.bytes2HexString(data[10]);
//                map.put("rfid",rfid);
//            }
//            return map;
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
            sendData("状态:获取卡或指纹权限");
            //发送数据
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="获取卡或指纹权限返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:获取卡或指纹权限无数据返回");
            }
            return null;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x08 ) {
//                //卡权限
//                if(power==0){
//                    byte[] cardby = new byte[10];
//                    System.arraycopy(data, 8, cardby, 0, 10);
//                    card = DataTypeChange.byteArrayToHexString(cardby);
//                }else if(power==1){
//                    byte[] cardby = new byte[10];
//                    System.arraycopy(data, 8, cardby, 0, 10);
//                    card = DataTypeChange.byteArrayToHexString(cardby);
//                }
//
//            }
//            return card;
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
    public static String ST_GetCard(){
        String card="";
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[] { 0x23 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x09};
            byte[] bydata=new byte[32];
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            sendData("状态:获取盘存数据发送");
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="获取盘存数据返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:获取盘存无数据返回");
            }
            return null;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x23
//                    && data[3] == (byte) 0x09 ) {
//                byte[] cardby = new byte[32];
//                System.arraycopy(data, 4, cardby, 0, 32);
//                //todo判断cardby内容是否为0
//
//                card = DataTypeChange.byteArrayToHexString(cardby);
//            }
//            return card;
        }catch (Exception e){
            logger.error("读取标签盘存数据出错",e);
            return card;
        }finally {
            myLock.unlock();
        }

    }

    /**
     * 删除指纹
     * @param flag 0-删除单个指纹  1-删除所有指纹
     * @param code 为0时，该参数有效，需要删除的指纹编号。为10时，传入0即可
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
                bydata[0]=0;
                bydata[1]=0x01;
                bydata[2]=0x02;
            }else{
                bydata=new byte[1];
                bydata[0]=1;
            }
            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            if(flag==0){
                before=DataTypeChange.byteAddToByte(before,bydata);
            }

            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            sendData("状态:删除指纹");
            byte[] data=sp.sendAndGet(send);

            //--
            String fh="删除指纹返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:删除指纹无数据返回");
            }
            return true;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x21 && data[4] == (byte) 0x00) {
//                return true;
//            }else{
//                return false;
//            }
        }catch (Exception e){
            logger.error("删除指纹出错",e);
            return false;
        }finally {
            myLock.unlock();
        }


    }

    public static boolean ST_AddSaveZW(int code){
        try{
            myLock.lock();
            byte[] head = new byte[] { 0x3A };
            byte[] length = new byte[5] ;

            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x22};
            byte[] bydata=new byte[2];
            bydata[0]=(byte)01;
            bydata[1]=(byte)02;

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            before=DataTypeChange.byteAddToByte(before,bydata);

            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            sendData("状态:添加指纹0102");
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="添加指纹返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:添加指纹无数据返回");
            }
            return true;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x28 && data[4] == (byte) 0x00) {
//                return true;
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
            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
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
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x50};

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            sendData("状态:开门发送");
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="开门返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:开门发送无数据返回");
            }
            return true;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x01 && data[4] == (byte) 0x00 ) {
//                return true;
//            }else{
//                return false;
//            }
        }catch (Exception e){
            logger.error("发送心跳出错",e);
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
            byte[] length = new byte[] { 0x03 };
            byte[] deviceID = new byte[] { 0x00};
            byte[] order = new byte[] {0x51};

            byte[] before=new byte[]{};
            before=DataTypeChange.byteAddToByte(before,head);
            before=DataTypeChange.byteAddToByte(before,length);
            before=DataTypeChange.byteAddToByte(before,deviceID);
            before=DataTypeChange.byteAddToByte(before,order);
            byte jyData=getJYData(before);

            byte[] send= DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            sendData("状态:开灯发送");
            byte[] data=sp.sendAndGet(send);
            //--
            String fh="开灯返回";
            if(data!=null){
                for(int i=0;i<data.length;i++){
                    fh=fh+String.valueOf(data[i])+",";
                }
                fh=fh.substring(0,fh.length()-1);
                sendData("状态:"+fh);
            }else{
                sendData("状态:开灯发送无数据返回");
            }
            return true;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x01 && data[4] == (byte) 0x00 ) {
//                return true;
//            }else{
//                return false;
//            }
        }catch (Exception e){
            logger.error("发送心跳出错",e);
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
            byte[] length = new byte[]{0x03};
            byte[] deviceID = new byte[]{0x00};
            byte[] order = new byte[]{0x52};

            byte[] before = new byte[]{};
            before = DataTypeChange.byteAddToByte(before, head);
            before = DataTypeChange.byteAddToByte(before, length);
            before = DataTypeChange.byteAddToByte(before, deviceID);
            before = DataTypeChange.byteAddToByte(before, order);
            byte jyData = getJYData(before);

            byte[] send = DataTypeChange.byteAddToByte(before, jyData);
            //发送数据
            sendData("状态:关灯发送");
            byte[] data = sp.sendAndGet(send);
            //--
            String fh = "关灯返回";
            if (data != null) {
                for (int i = 0; i < data.length; i++) {
                    fh = fh + String.valueOf(data[i]) + ",";
                }
                fh = fh.substring(0, fh.length() - 1);
                sendData("状态:" + fh);
            } else {
                sendData("状态:开灯发送无数据返回");
            }
            return true;
            //--
//            if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
//                    && data[3] == (byte) 0x01 && data[4] == (byte) 0x00 ) {
//                return true;
//            }else{
//                return false;
//            }
        }catch (Exception e){
            logger.error("发送心跳出错",e);
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
}
