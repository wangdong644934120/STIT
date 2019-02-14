package com.st.p2018.external;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.device.HCProtocol;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import org.apache.log4j.Logger;
import org.json.JSONObject;


/**
 * Created by Administrator on 2019/1/31.
 */

public class DealReceive extends Thread{
    private Logger logger= Logger.getLogger(this.getClass());
    String value="";
    public DealReceive(String value){
        this.value=value;
    }
    public void run(){
        try{
            JSONObject jsonData = new JSONObject(value);
            String order =jsonData.get("order")==null?"":jsonData.get("order").toString();
            String data=jsonData.get("data")==null?"":jsonData.get("data").toString();
            String number=jsonData.get("number")==null?"":jsonData.get("number").toString();
            switch(order){
                case "heart":
                    //心跳
                    String sendValue="{\"order\":\"heart\",\"number\":\""+number+"\",\"data\":\""+Cache.ipmac+"\"}";
                    Cache.socketClient.send(sendValue);
                    break;
                case "person":
                    String type=jsonData.get("type")==null?"":jsonData.get("type").toString();
                    String code=jsonData.get("code")==null?"":jsonData.get("code").toString();
                    String tzz=jsonData.get("tzz")==null?"":jsonData.get("tzz").toString();
                    dealPerson(type,code,tzz,number);
                    break;
                case "power":
                    //指纹或刷卡返回
                    if(data.equals("")){
                        MyTextToSpeech.getInstance().speak("无开门权限");
                        return;
                    }
                    if(HCProtocol.ST_OpenDoor()){
                        logger.info("下发开门成功");
                        sendCZY(data);
                        MyTextToSpeech.getInstance().speak(data+"开门成功");
                    }else{
                        logger.info("下发开门失败");
                    }
                    break;
                case "product":
                    if(HCProtocol.ST_GetAllCard()){
                        logger.info("下发盘点成功");
                    }else{
                        logger.info("下发盘点失败");
                    }
                    break;
                case "deviceinfo":
                    //获取设备信息
                    getDeviceInfo(number);
                    break;
                case "config":
                    //获取设备配置信息
                    getDeviceConfig(number);
                    break;
                case "code":
                    //配置柜号信息
                    setGH(data);
                    break;
                case "door":
                    if(data.equals("1")){
                        if(HCProtocol.ST_OpenDoor()){
                            logger.info("下发开门成功");
                            MyTextToSpeech.getInstance().speak(data+"开门成功");
                        }else{
                            logger.info("下发开门失败");
                            MyTextToSpeech.getInstance().speak("开门失败");
                        }
                    }
                    break;

                case "light":
                    if(data.equals("1")){
                        if(HCProtocol.ST_OpenLight()){
                            logger.info("下发开灯成功");
                            MyTextToSpeech.getInstance().speak("开灯成功");
                        }else{
                            logger.info("下发开灯失败");
                            MyTextToSpeech.getInstance().speak("开灯失败");
                        }
                    }else if(data.equals("0")){
                        if(HCProtocol.ST_CloseLight()){
                            logger.info("下发关灯成功");
                            MyTextToSpeech.getInstance().speak("关灯成功");
                        }else{
                            logger.info("下发关灯失败");
                            MyTextToSpeech.getInstance().speak("关灯失败");
                        }
                    }
                    break;
                default:
                break;
            }

        }catch(Exception e){

        }

    }
    private void getDeviceInfo(String number){
        String gc="";
        if(Cache.gcqy1){
            gc=gc+"1";
        }else{
            gc=gc+"0";
        }
        if(Cache.gcqy2){
            gc=gc+"1";
        }else{
            gc=gc+"0";
        }
        if(Cache.gcqy3){
            gc=gc+"1";
        }else{
            gc=gc+"0";
        }
        if(Cache.gcqy4){
            gc=gc+"1";
        }else{
            gc=gc+"0";
        }
        if(Cache.gcqy5){
            gc=gc+"1";
        }else{
            gc=gc+"0";
        }
        if(Cache.gcqy6){
            gc=gc+"1";
        }else{
            gc=gc+"0";
        }
        String device="{\"type\":\""+(Cache.gx.equals("")?"1":"2")+"\"using\":\""+gc+"\"}";
        String sendconfig="{\"order\":\"device\",\"number\":\""+number+"\",\"data\":"+device+",\"message\":\"0\"}";
        Cache.socketClient.send(sendconfig);
    }

    private void getDeviceConfig(String number){

    }
    private void dealPerson(String type,String code,String tzz,String number){
        String sendValue="";
        if(type.equals("1")){
            //添加指纹
            if(HCProtocol.ST_AddZW(Integer.valueOf(code),tzz)){
                // 指纹添加成功
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
            }else{
                //指纹添加失败
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"-1\"}";
            }
        }else if(type.equals("2")){
            //删除指纹
            if(HCProtocol.ST_DeleteZW(0,Integer.valueOf(code))){
                //删除指纹成功
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
            }else{
                //删除指纹失败
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"-1\"}";
            }
        }else if(type.equals("3")){
            //删除所有指纹
            if(HCProtocol.ST_DeleteZW(1,0)){
                //删除指纹成功
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
            }else{
                //删除指纹失败
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"-1\"}";
            }
        }
        Cache.socketClient.send(sendValue);
    }

    private void setGH(String data){
        //显示柜号，并保存到数据库
    }
    private  void sendCZY(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("czy",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
}
