package com.st.p2018.external;

import android.os.Bundle;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.st.p2018.dao.PZDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.externalentity.ExternalPorduct;
import com.st.p2018.entity.PDEntity;
import com.st.p2018.entity.Product;
import com.st.p2018.externalentity.ActionTotal_TotalMessage;
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
    private Logger logger= Logger.getLogger(DealReceive.class);
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
                case "patientproduct":

                    break;
                case "total":
                    dealTotal(number,value);
                    break;
                default:
                    String sendValue="{\"order\":\""+order+"\",\"number\":\""+number+"\",\"message\":\"1\"}";
                    SocketClient.send(sendValue);
                break;
            }

        }catch(Exception e){
            logger.error("解析服务器发送数据出错",e);
        }

    }

    /**
     * 获取设备信息
     * @param number
     */
    private void getDeviceInfo(String number){
        try{
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
        }catch (Exception e){
            logger.error("获取设备信息出错",e);
        }

    }

    /**
     * 配置设备信息
     * @param number
     * @param data
     */
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
            logger.error("配置设备信息出错",e);
        }

    }

    /**
     * 处理人员指纹信息
     * @param type
     * @param code
     * @param tzz
     * @param number
     */
    private void dealPerson(String type,String code,String tzz,String number){
        try{
            String sendValue="";
            if(type.equals("1")){
                //添加指纹
                if(HCProtocol.ST_AddZW(Integer.valueOf(code),tzz)){
                    logger.info("添加指纹成功,编号："+code);
                    // 指纹添加成功
                    sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
                }else{
                    logger.info("添加指纹失败,编号："+code);
                    //指纹添加失败
                    sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"3\"}";
                }
            }else if(type.equals("2")){
                //删除指纹
                if(HCProtocol.ST_DeleteZW(0,Integer.valueOf(code))){
                    logger.info("删除指纹成功,编号："+code);
                    //删除指纹成功
                    sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
                }else{
                    logger.info("删除指纹失败,编号："+code);
                    //删除指纹失败
                    sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"3\"}";
                }
            }else if(type.equals("3")){
                //删除所有指纹
                if(HCProtocol.ST_DeleteZW(1,0)){
                    logger.info("删除所有指纹成功");
                    //删除指纹成功
                    sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"0\"}";
                }else{
                    logger.info("删除所有指纹失败");
                    //删除指纹失败
                    sendValue="{\"order\":\"person\",\"number\":\""+number+"\",\"message\":\"3\"}";
                }
            }
            SocketClient.send(sendValue);
        }catch (Exception e){
            logger.error("处理人员指纹信息出错",e);
        }

    }

    /**
     * 配置柜号
     * @param number
     * @param data
     */
    private void setGH(String number,String data){
        //显示柜号，并保存到数据库--{“number”:”1”,”name”:”高值耗材柜”}
        String sendValue="{\"order\":\"code\",\"number\":\""+number+"\",\"message\":\"0\"}";
        try{
            String xtmc="";
            String code="";
            String lock="";
            String choosesick="";
            try{
                JSONObject jsonData = new JSONObject(data);
                xtmc=jsonData.getString("name")==null?Cache.appname:jsonData.getString("name").toString();
                code=jsonData.getString("number")==null?Cache.appcode:jsonData.getString("number").toString();
                lock=jsonData.getString("lockscreen")==null?Cache.lockScreen:jsonData.getString("lockscreen").toString();
                choosesick=jsonData.getString("choosesick")==null?Cache.chooseSick:jsonData.getString("choosesick").toString();
            }catch (Exception ex){
                logger.error("解析系统编号及名称data数据出错",ex);
                sendValue="{\"order\":\"code\",\"number\":\""+number+"\",\"message\":\"2\"}";
                SocketClient.send(sendValue);
                return;
            }

            PZDao pzDao= new PZDao();
            pzDao.updateAppName(xtmc,code, Cache.ServerIP, String.valueOf( Cache.ServerPort),lock,choosesick);
            sendAPPName(xtmc);
            Cache.appname=xtmc;
            Cache.appcode=code;
            logger.info("设置系统名称："+xtmc+",系统编号："+code);
            //发送配置成功消息
            sendValue="{\"order\":\"code\",\"number\":\""+number+"\",\"message\":\"0\"}";
            SocketClient.send(sendValue);
        }catch(Exception e){
            logger.error("配置系统名称及编号出错",e);

        }

    }

    /**
     * 发送操作员信息
     * @param value
     */
    private  void sendCZY(String value){
        try{
            Message message = Message.obtain(Cache.myHandle);
            Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            data.putString("czy",value);
            message.setData(data);
            Cache.myHandle.sendMessage(message);
        }catch (Exception e){
            logger.error("发送操作员信息出错",e);
        }

    }

    /**
     * 发送APP名称信息
     * @param appname
     */
    private  void sendAPPName(String appname){
        try{
            Message message = Message.obtain(Cache.myHandle);
            Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            data.putString("appname",appname);
            message.setData(data);
            Cache.myHandle.sendMessage(message);
        }catch (Exception e){
            logger.error("修改app名称出错",e);
        }

    }

    /**
     * 开门
     * @param number
     * @param data
     */
    private void openDoor(String number,String data){
        try{
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
        }catch (Exception e){
            logger.error("开门出错",e);
        }

    }

    /**
     * 开灯
     * @param number
     * @param data
     */
    private void openLight(String number,String data){
        try{
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
        }catch (Exception e){
            logger.error("开灯出错",e);
        }


    }

    /**
     * 获取权限
     * @param number
     * @param data
     */
    private void getPower(String number,String data){
        try{
            if(data.equals("") || data.toLowerCase().equals("null")){
                MyTextToSpeech.getInstance().speak("无开门权限");
                return;
            }
            if(Cache.isPCNow==1){
                logger.info("正在盘存，禁止开门");
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
            String[] oprmess=data.split("\\+");
            String name=data;
            Cache.operatorCode=data;
            if(oprmess.length==2){
                name=oprmess[0];
                Cache.operatorCode=oprmess[1];
            }
            sendCZY(name);

            if(Cache.chooseSick.equals("0")){
                logger.info("未配置患者选择，核验成功直接打开柜门");
                HCProtocol.ST_OpenDoor();
            }
            MyTextToSpeech.getInstance().speak(name+"核验成功");
        }catch (Exception e){
            logger.error("获取权限出错",e);
        }

    }

    /**
     * 处理患者信息
     * @param number
     * @param value
     */
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
                logger.info("handlesick有效");
                Message message = Message.obtain(Cache.myHandleSick);
                Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                bund.putString("show","sick");
                message.setData(bund);
                Cache.myHandleSick.sendMessage(message);
            }

        }catch (Exception e){
            logger.error("处理患者信息出错",e);
        }
    }

    /**
     * 处理耗材信息
     * @param number
     * @param value
     */
    private void dealPorduct(String number,String value){
        try{
            long start=System.currentTimeMillis();
            ExternalPorduct externalPorduct = JSON.parseObject(value, new TypeReference<ExternalPorduct>(){});

            if(externalPorduct.getData()!=null){
                for(Product product :externalPorduct.getData().getAction()){
                    if(product.getOperation().equals("存")){
                        Cache.listOperaSave.add(product);
                    }else if(product.getOperation().equals("取")){
                        Cache.listOperaOut.add(product);
                    }
                }
            }
            if(Cache.myHandleAccess!=null){
                Message message = Message.obtain(Cache.myHandleAccess);
                Bundle bund = new Bundle();
                bund.putString("show","1");
                message.setData(bund);
                Cache.myHandleAccess.sendMessage(message);
            }else{
                logger.info("耗材确认界面已经关闭，无需展示数据");
            }


            logger.info("耗材操作统计耗时:"+(System.currentTimeMillis()-start));
            start=System.currentTimeMillis();

            Cache.mapTotal.put("jxq",new ArrayList<Product>());
            Cache.mapTotal.put("yxq",new ArrayList<Product>());
            Cache.mapTotal.put("ygq",new ArrayList<Product>());
            if(externalPorduct.getData()!=null){
                for(ActionTotal_TotalMessage totalMessage : externalPorduct.getData().getTotal()){
                    if(totalMessage.getXq().equals("近效期")){
                        Cache.mapTotal.get("jxq").addAll(totalMessage.getData());
                    }else if(totalMessage.getXq().equals("远效期")){
                        Cache.mapTotal.get("yxq").addAll(totalMessage.getData());
                    }else if(totalMessage.getXq().equals("已过期")){
                        Cache.mapTotal.get("ygq").addAll(totalMessage.getData());
                    }
                }
            }

            Message messageInitXQ = Message.obtain(Cache.myHandle);
            Bundle bundInitXQ = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
            bundInitXQ.putString("initJXQExternal","1");
            messageInitXQ.setData(bundInitXQ);
            Cache.myHandle.sendMessage(messageInitXQ);
            logger.info("耗材效期统计耗时:"+(System.currentTimeMillis()-start));
        }catch (Exception e){
            logger.error("处理服务器返回的耗材数据出错",e);
        }


    }

    /**
     * 处理统计信息
     * @param number
     * @param value
     */
    private void dealTotal(String number,String value){
        long start=System.currentTimeMillis();
        try{
            Cache.mapPD.clear();
            Cache.mapTotal.put("jxq",new ArrayList<Product>());
            Cache.mapTotal.put("yxq",new ArrayList<Product>());
            Cache.mapTotal.put("ygq",new ArrayList<Product>());
            ExternalPorduct externalPorduct = JSON.parseObject(value, new TypeReference<ExternalPorduct>(){});
            logger.info("耗材统计解析耗时:"+(System.currentTimeMillis()-start));

            if(externalPorduct.getData()!=null){
                for(ActionTotal_TotalMessage totalMessage :externalPorduct.getData().getTotal()){
                    String xq="jxq";
                    if(totalMessage.getXq().equals("近效期")){
                        xq="jxq";
                    }else if(totalMessage.getXq().equals("远效期")){
                        xq="yxq";
                    }else if(totalMessage.getXq().equals("已过期")){
                        xq="ygq";
                    }
                    Cache.mapTotal.get(xq).addAll(totalMessage.getData());
                    for(Product product : totalMessage.getData()){
                        initXQ(totalMessage.getXq(),product);
                    }
                }
            }

            if(Cache.myHandlePD!=null){
                Message message = Message.obtain(Cache.myHandlePD);
                Bundle bund = new Bundle();
                bund.putString("show","1");
                message.setData(bund);
                Cache.myHandlePD.sendMessage(message);
            }else{
                logger.info("盘点界面已经关闭，取消信息显示");
            }

            Message messageInitXQ = Message.obtain(Cache.myHandle);
            Bundle bundInitXQ = new Bundle();
            bundInitXQ.putString("initJXQExternal","1");
            messageInitXQ.setData(bundInitXQ);
            Cache.myHandle.sendMessage(messageInitXQ);

        }catch (Exception e){
            logger.error("初始化效期出错",e);
        }
    }

    /**
     * 初始化效期
     * @param xq
     * @param product
     */
    private void initXQ(String xq,Product product){
        if(xq.equals("近效期")){
            if(Cache.mapPD.get(product.getSzwz())==null){
                PDEntity pdEntity=new PDEntity();
                pdEntity.setJxq(pdEntity.getJxq()+1);
                Cache.mapPD.put(product.getSzwz(),pdEntity);
            }else{
                PDEntity pdEntity=Cache.mapPD.get(product.getSzwz());
                Cache.mapPD.get(product.getSzwz()).setJxq(pdEntity.getJxq()+1);
            }
        }else if(xq.equals("远效期")){
            if(Cache.mapPD.get(product.getSzwz())==null){
                PDEntity pdEntity=new PDEntity();
                pdEntity.setYxq(pdEntity.getYxq()+1);
                Cache.mapPD.put(product.getSzwz(),pdEntity);
            }else{
                PDEntity pdEntity=Cache.mapPD.get(product.getSzwz());
                Cache.mapPD.get(product.getSzwz()).setYxq(pdEntity.getYxq()+1);
            }
        }else if(xq.equals("已过期")){
            if(Cache.mapPD.get(product.getSzwz())==null){
                PDEntity pdEntity=new PDEntity();
                pdEntity.setYgq(pdEntity.getYgq()+1);
                Cache.mapPD.put(product.getSzwz(),pdEntity);
            }else{
                PDEntity pdEntity=Cache.mapPD.get(product.getSzwz());
                Cache.mapPD.get(product.getSzwz()).setYgq(pdEntity.getYgq()+1);
            }
        }
    }

}
