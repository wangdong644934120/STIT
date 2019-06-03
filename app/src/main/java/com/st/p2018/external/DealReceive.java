package com.st.p2018.external;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.dao.PZDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.entity.Product;
import com.st.p2018.util.Cache;
import com.st.p2018.util.CacheSick;
import com.st.p2018.util.MyTextToSpeech;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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
            logger.info("接收到服务器发送数据："+value);
            JSONObject jsonData;
            String order="";
            String data="";
            String message="";
            String number="";
            try{
                jsonData = new JSONObject(value);
                order =jsonData.get("order")==null?"":jsonData.get("order").toString();

            }catch (Exception ex){
                logger.error("解析服务器发送数据出错",ex);
                String sendValue="{\"order\":\""+order+"\",\"number\":\""+number+"\",\"message\":\"2\"}";
                SocketClient.send(sendValue);
                return;
            }

            switch(order){
                case "heart":
                    //心跳
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    CacheSend.removeSend(number);
                    break;
                case "person":
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    String type=jsonData.get("type")==null?"":jsonData.get("type").toString();
                    String code=jsonData.get("code")==null?"":jsonData.get("code").toString();
                    String tzz=jsonData.get("tzz")==null?"":jsonData.get("tzz").toString();
                    dealPerson(type,code,tzz,number);

                    break;
                case "power":
                    //指纹或刷卡返回
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    data=jsonData.get("data")==null?"":jsonData.get("data").toString();
                    getPower(number,data);
                    break;
                case "product":
                    dealPorduct(number,value);
                    /*if(HCProtocol.ST_GetAllCard()){
                        logger.info("下发盘点成功");
                    }else{
                        logger.info("下发盘点失败");
                    }*/
                    break;
                case "deviceinfo":
                    //获取设备信息
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    getDeviceInfo(number);
                    break;
                case "config":
                    //获取设备配置信息
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    data=jsonData.get("data")==null?"":jsonData.get("data").toString();
                    setDeviceConfig(number,data);
                    break;
                case "code":
                    //配置柜号信息
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    data=jsonData.get("data")==null?"":jsonData.get("data").toString();
                    setGH(number,data);
                    break;
                case "door":
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    data=jsonData.get("data")==null?"":jsonData.get("data").toString();
                    openDoor(number,data);
                    break;

                case "light":
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    data=jsonData.get("data")==null?"":jsonData.get("data").toString();
                    openLight(number,data);
                    break;
                case "patient":
                    number=jsonData.get("number")==null?"":jsonData.get("number").toString();
                    data=jsonData.get("data")==null?"":jsonData.get("data").toString();
                    dealSick(number,value);
                    break;
                default:
                    String sendValue="{\"order\":\""+order+"\",\"number\":\""+number+"\",\"message\":\"1\"}";
                    SocketClient.send(sendValue);
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
        String device="{\"type\":\""+(Cache.gx.equals("Ⅰ型")?"1":"2")+"\",\"using\":\""+gc+"\"}";
        String sendValue="{\"order\":\"deviceinfo\",\"number\":\""+number+"\",\"data\":"+device+",\"message\":\"0\"}";
        SocketClient.send(sendValue);
    }

    private void setDeviceConfig(String number,String data){
        String sendValue="{\"order\":\"config\",\"number\":\""+number+"\",\"message\":\"0\"}";
        try{
            int lightI=0;
            int rfidI=0;
            int countI=5;
            int intervalI=5;
            try{
                JSONObject jsonData = new JSONObject(data);
                String light=jsonData.getString("light")==null?"0":jsonData.getString("light").toString();
                String rfid=jsonData.getString("rfid")==null?"0":jsonData.getString("rfid").toString();
                String count=jsonData.getString("count")==null?"5":jsonData.getString("count").toString();
                String interval=jsonData.getString("interval")==null?"5":jsonData.getString("interval").toString();

                lightI=Integer.valueOf(light);
                rfidI=Integer.valueOf(rfid);
                countI=Integer.valueOf(count);
                intervalI=Integer.valueOf(interval);

                if(intervalI>=5 && intervalI<=255){
                }else{
                    sendValue="{\"order\":\"config\",\"number\":\""+number+"\",\"message\":\"2\"}";
                    SocketClient.send(sendValue);
                    return;
                }

            }catch (Exception ex){
                logger.error("解析设备配置参数出错",ex);
                sendValue="{\"order\":\"config\",\"number\":\""+number+"\",\"message\":\"2\"}";
                SocketClient.send(sendValue);
                return;
            }

            boolean bl1=HCProtocol.ST_SetWorkModel(lightI,rfidI,countI,intervalI);
            if(bl1){
                sendValue="{\"order\":\"config\",\"number\":\""+number+"\",\"message\":\"0\"}";
                logger.info("下发工作模式成功");
            }else{
                sendValue="{\"order\":\"config\",\"number\":\""+number+"\",\"message\":\"3\"}";
                logger.info("下发工作模式失败");
            }
            //发送配置成功消息

            SocketClient.send(sendValue);
        }catch(Exception e){

        }

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
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"3\"}";
            }
        }else if(type.equals("2")){
            //删除指纹
            if(HCProtocol.ST_DeleteZW(0,Integer.valueOf(code))){
                //删除指纹成功
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
            }else{
                //删除指纹失败
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"3\"}";
            }
        }else if(type.equals("3")){
            //删除所有指纹
            if(HCProtocol.ST_DeleteZW(1,0)){
                //删除指纹成功
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
            }else{
                //删除指纹失败
                sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"3\"}";
            }
        }
        SocketClient.send(sendValue);
    }

    private void setGH(String number,String data){
        //显示柜号，并保存到数据库--{“number”:”1”,”name”:”高值耗材柜”}
        String sendValue="{\"order\":\"code\",\"number\":\""+number+"\",\"message\":\"0\"}";
        try{
            String xtmc="";
            String code="";
            try{
                JSONObject jsonData = new JSONObject(data);
                xtmc=jsonData.getString("name")==null?Cache.appname:jsonData.getString("name").toString();
                code=jsonData.getString("number")==null?Cache.appcode:jsonData.getString("number").toString();
            }catch (Exception ex){
                logger.error("解析系统编号及名称data数据出错",ex);
                sendValue="{\"order\":\"code\",\"number\":\""+number+"\",\"message\":\"2\"}";
                SocketClient.send(sendValue);
                return;
            }

            PZDao pzDao= new PZDao();
            pzDao.updateAppName(xtmc,code, Cache.ServerIP, String.valueOf( Cache.ServerPort));
            sendAPPName(xtmc);
            Cache.appname=xtmc;
            Cache.appcode=code;
            logger.info("设置系统名称："+xtmc+",系统编号："+code);
            //发送配置成功消息
            sendValue="{\"order\":\"code\",\"number\":\""+number+"\",\"message\":\"0\"}";
            SocketClient.send(sendValue);
        }catch(Exception e){

        }

    }

    private  void sendCZY(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("czy",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    private  void sendAPPName(String appname){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("appname",appname);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    private void openDoor(String number,String data){
        String sendValue="";
        if(data.equals("1")){
            if(HCProtocol.ST_OpenDoor()){
                sendValue="{\"order\":\"door\",\"number\":\""+number+"\",\"message\":\"0\"}";
                logger.info("下发开门成功");
                MyTextToSpeech.getInstance().speak("开门成功");
            }else{
                sendValue="{\"order\":\"door\",\"number\":\""+number+"\",\"message\":\"3\"}";
                logger.info("下发开门失败");
                MyTextToSpeech.getInstance().speak("开门失败");
            }
            SocketClient.send(sendValue);
        }
    }

    private void openLight(String number,String data){
        String sendValue="";
        if(data.equals("1")){
            if(HCProtocol.ST_OpenLight()){
                sendValue="{\"order\":\"light\",\"number\":\""+number+"\",\"message\":\"0\"}";
                logger.info("下发开灯成功");
                MyTextToSpeech.getInstance().speak("开灯成功");
            }else{
                sendValue="{\"order\":\"light\",\"number\":\""+number+"\",\"message\":\"3\"}";
                logger.info("下发开灯失败");
                MyTextToSpeech.getInstance().speak("开灯失败");
            }
        }else if(data.equals("0")){
            if(HCProtocol.ST_CloseLight()){
                sendValue="{\"order\":\"light\",\"number\":\""+number+"\",\"message\":\"0\"}";
                logger.info("下发关灯成功");
                MyTextToSpeech.getInstance().speak("关灯成功");
            }else{
                sendValue="{\"order\":\"light\",\"number\":\""+number+"\",\"message\":\"3\"}";
                logger.info("下发关灯失败");
                MyTextToSpeech.getInstance().speak("关灯失败");
            }
        }
        if(!sendValue.equals("")){
            SocketClient.send(sendValue);
        }

    }

    private void getPower(String number,String data){
        if(data.equals("")){
            MyTextToSpeech.getInstance().speak("无开门权限");
            return;
        }

        //关闭锁屏界面
        if(Cache.lockScreen.equals("1")){
            if(Cache.myHandleLockScreen==null){
                logger.info("handle关闭锁屏发送失败");
                return;
            }
            Message message = Message.obtain(Cache.myHandleLockScreen);
            Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            bund.putString("close","ok");
            message.setData(bund);
            Cache.myHandleLockScreen.sendMessage(message);
        }
        if(Cache.chooseSick.equals("1")){
            if(Cache.myHandle==null){
                logger.info("handle打开患者选择界面发送失败");
                return;
            }
            Message message = Message.obtain(Cache.myHandle);
            Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            bund.putString("ui","sick");
            message.setData(bund);
            Cache.myHandle.sendMessage(message);
        }
        sendCZY(data);
        MyTextToSpeech.getInstance().speak(data+"核验成功");
      /* if(HCProtocol.ST_OpenDoor()){
            logger.info("下发开门成功");
            sendCZY(data);
            MyTextToSpeech.getInstance().speak(data+"开门成功");
        }else{
            logger.info("下发开门失败");
            MyTextToSpeech.getInstance().speak("下发开门失败");
        }*/
    }

    private void dealSick(String number,String value){
        CacheSick.clear();
        try{
            JSONObject jsonObject=new JSONObject(value);
            JSONArray jsonArray=jsonObject.getJSONArray("data");
            for(int i=0;i<jsonArray.length();i++){
                JSONObject obj=jsonArray.getJSONObject(i);
                String time=obj.getString("time");
                String name=obj.getString("name");
                String code=obj.getString("code");
                String dept=obj.getString("dept");
                String operaid=obj.getString("operaid");
                CacheSick.add(time,name,code,dept,operaid);
            }
            //通知患者选择界面显示数据
            if(Cache.chooseSick.equals("1")){
                if(Cache.myHandleSick==null){
                    logger.info("handle打开患者选择界面发送失败");
                    return;
                }
                Message message = Message.obtain(Cache.myHandleSick);
                Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                bund.putString("show","sick");
                message.setData(bund);
                Cache.myHandleSick.sendMessage(message);
            }

        }catch (Exception e){
            e.printStackTrace();

        }



    }

    private void dealPorduct(String number,String value){
        long start=System.currentTimeMillis();
        try{
            JSONObject jsonObject=new JSONObject(value);
            String data=jsonObject.getString("data");
            JSONObject jsonData=new JSONObject(data);
            JSONArray jsonArrayAction=jsonData.getJSONArray("action");
            JSONArray jsonArrayTotal=jsonData.getJSONArray("total");
            Cache.listOperaOut.clear();
            Cache.listOperaSave.clear();
            for(int i=0;i<jsonArrayAction.length();i++){
                JSONObject obj=jsonArrayAction.getJSONObject(i);
                String pp=obj.getString("pp");
                String mc=obj.getString("mc");
                String xqpc=obj.getString("xqpc");
                String yxrq=obj.getString("yxrq");
                String syts=obj.getString("syts");
                String szwz=obj.getString("szwz");
                String epc=obj.getString("epc");
                String operation=obj.getString("operation");
                Product product=new Product();
                product.setPp(pp);
                product.setMc(mc);
                product.setXqpc(xqpc);
                product.setYxrq(yxrq);
                product.setSyts(syts);
                product.setSzwz(szwz);
                product.setEpc(epc);
                product.setOperation(operation);
                if(operation.equals("存")){
                    Cache.listOperaSave.add(product);
                }else if(operation.equals("取")){
                    Cache.listOperaOut.add(product);
                }
            }
            System.out.println("耗时1:"+(System.currentTimeMillis()-start));
            Message message = Message.obtain(Cache.myHandle);
            Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            bund.putString("ui","access");
            message.setData(bund);
            Cache.myHandle.sendMessage(message);


            start=System.currentTimeMillis();
            Cache.mapTotal.put("jxq",new ArrayList<Product>());
            Cache.mapTotal.put("yxq",new ArrayList<Product>());
            Cache.mapTotal.put("ygq",new ArrayList<Product>());

            for(int i=0;i<jsonArrayTotal.length();i++){
                JSONObject objXQ=jsonArrayTotal.getJSONObject(i);
                String xq=objXQ.getString("xq");
                JSONArray jsonTotalXQ=objXQ.getJSONArray("data");
                List<Product> listProductXQ=new ArrayList<Product>();
                for(int j=0;j<jsonTotalXQ.length();j++){
                    JSONObject obj=jsonArrayAction.getJSONObject(i);
                    String pp=obj.getString("pp");
                    String mc=obj.getString("mc");
                    String xqpc=obj.getString("xqpc");
                    String yxrq=obj.getString("yxrq");
                    String syts=obj.getString("syts");
                    String szwz=obj.getString("szwz");
                    String epc=obj.getString("epc");
                    String operation=obj.getString("operation");
                    Product product=new Product();
                    product.setPp(pp);
                    product.setMc(mc);
                    product.setXqpc(xqpc);
                    product.setYxrq(yxrq);
                    product.setSyts(syts);
                    product.setSzwz(szwz);
                    product.setEpc(epc);
                    product.setOperation(operation);
                    if(xq.equals("近效期")){
                        Cache.mapTotal.get("jxq").add(product);
                    }else if(xq.equals("远效期")){
                        Cache.mapTotal.get("yxq").add(product);
                    }else if(xq.equals("已过期")){
                        Cache.mapTotal.get("ygq").add(product);
                    }
                }

            }

            System.out.println("耗时2:"+(System.currentTimeMillis()-start));
            Message messageInitXQ = Message.obtain(Cache.myHandle);
            Bundle bundInitXQ = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            bundInitXQ.putString("initJXQExternal","1");
            messageInitXQ.setData(bundInitXQ);
            Cache.myHandle.sendMessage(messageInitXQ);

        }catch (Exception e){
            e.printStackTrace();

        }



    }
}
