package com.st.p2018.device;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/12/6.
 */

public class HCProtocol {
    private static Logger logger=Logger.getLogger(HCProtocol.class);
    private static SerialPort sp;
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
        if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                && data[3] == (byte) 0x01 && data[4] == (byte) 0x00 ) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 软复位
     */
    public static boolean ST_SetSoftRest(){
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
    }

    /**
     * 时间同步
     */
    public static boolean ST_SetTime(){
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
        if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                && data[3] == (byte) 0x04 && data[4] == (byte) 0x00 ) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 设置工作模式
     * @param lightModel 灯模式
     * @param cardModel 标签模式
     * @return
     */
    public static boolean ST_SetWorkModel(int lightModel,int cardModel){
        byte[] head = new byte[] { 0x3A };
        byte[] length = new byte[] { 0x08 };
        byte[] deviceID = new byte[] { 0x00};
        byte[] order = new byte[] {0x06};
        byte[] bydata=new byte[5];
        bydata[0]=(byte)lightModel;
        bydata[1]=(byte)cardModel;

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
                && data[3] == (byte) 0x06 && data[4] == (byte) 0x00 ) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * 获取工作状态
     * @return
     */
    public static HashMap<String,String> ST_GetWorkModel(){
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

        if (data!=null && data.length>=16 && data[0] == (byte) 0x3A && data[1] == (byte) 0x0D
                && data[3] == (byte) 0x07 ) {
            //刷卡器
            if(data[4]==0x00){
                map.put("skq","0");
            }else{
                map.put("skq","1");
            }
            //指纹传感器
            if(data[5]==0x00){
                map.put("zwcgq","0");
            }else{
                map.put("zwcgq","1");
            }
            //门状态传感器
            if(data[6]==0x00){
                map.put("mztcgq","0");
            }else{
                map.put("mztcgq","1");
            }
            //电控锁
            if(data[7]==0x00){
                map.put("dks","0");
            }else{
                map.put("dks","1");
            }
            //红外/行程开关
            if(data[8]==0x00){
                map.put("hwxckg","0");
            }else{
                map.put("hwxckg","1");
            }
            //照明灯
            if(data[9]==0x00){
                map.put("zmd","0");
            }else{
                map.put("zmd","1");
            }
            //RFID读写器
            if(data[10]==0x00){
                map.put("rfid","0");
            }else{
                map.put("rfid","1");
            }
        }
        return map;
    }

    /**
     * 获取用户信息
     * @param power 0-卡权限，1-指纹权限
     * @return
     */
    public static String ST_GetUser(int power){
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
        if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                && data[3] == (byte) 0x08 ) {
            //卡权限
            if(power==0){
                byte[] cardby = new byte[10];
                System.arraycopy(data, 8, cardby, 0, 10);
                card = DataTypeChange.byteArrayToHexString(cardby);
            }else if(power==1){
                byte[] cardby = new byte[10];
                System.arraycopy(data, 8, cardby, 0, 10);
                card = DataTypeChange.byteArrayToHexString(cardby);
            }

        }
        return card;
    }

    /**
     * 获取盘存数据
     */
    public static void ST_GetCard(){

    }

    /**
     * 删除指纹
     * @param flag 0-删除单个指纹  10-删除所有指纹
     * @param code 为0时，该参数有效，需要删除的指纹编号。为10时，传入0即可
     * @return
     */
    public static boolean ST_DeleteZW(int flag,int code){
        byte[] head = new byte[] { 0x3A };
        byte[] length = new byte[1] ;
        if(flag==0){
            length[0]=4;
        }else{
            length[0]=3;
        }
        byte[] deviceID = new byte[] { 0x00};
        byte[] order = new byte[] {0x21};
        byte[] bydata=new byte[1];
        if(flag==0){
            length[0]=4;
            bydata[0]=(byte)code;
        }else{
            length[0]=3;
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
        byte[] data=sp.sendAndGet(send);
        if (data!=null && data[0] == (byte) 0x3A && data[1] == (byte) 0x04
                && data[3] == (byte) 0x21 && data[4] == (byte) 0x00) {
          return true;
        }else{
            return false;
        }

    }

    /**
     * 添加指纹信息
     * @param code 工号
     * @param tzz   指纹特征值
     * @return
     */
    public static boolean ST_AddZW(int code,String tzz){
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
    }

    public static byte getJYData(byte[] datas){

        byte temp=datas[0];
        for (int i = 1; i <datas.length; i++) {
            temp ^=datas[i];
        }

        temp=(byte)~temp;

        return temp;
    }


}
