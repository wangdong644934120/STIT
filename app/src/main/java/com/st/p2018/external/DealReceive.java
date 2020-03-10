package com.st.p2018.external;

import android.os.Bundle;
import android.os.Message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.st.p2018.dao.ExternalPowerDao;
import com.st.p2018.dao.PZDao;
import com.st.p2018.device.HCProtocol;
import com.st.p2018.externalentity.ActionProduct;
import com.st.p2018.entity.Product;
import com.st.p2018.externalentity.ExternalPower;
import com.st.p2018.externalentity.ExternalSick;
import com.st.p2018.externalentity.ProductExpire;
import com.st.p2018.externalentity.ProductSearch;
import com.st.p2018.externalentity.Sick;
import com.st.p2018.externalentity.TotalMessage;
import com.st.p2018.externalentity.TotalProduct;
import com.st.p2018.util.Cache;
import com.st.p2018.util.CacheSick;
import com.st.p2018.util.MyTextToSpeech;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;


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
                    getPower(number,value);
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
                    dealSick(number,value);
                    break;
                case "patientproduct":
                    dealTotal(number,value);
                    break;
                case "total":
                    dealTotal(number,value);
                    break;
                case "productsearch":
                    dealProductSearch(number,value);
                    break;
                case "productexpire":
                    dealProductExpire(number,value);
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
            ExternalPower externalPower = JSON.parseObject(value, new TypeReference<ExternalPower>(){});
            if(externalPower.getData()!=null && !externalPower.getData().getCard().equals("")
                    && !externalPower.getData().getCode().equals("")
                    && !externalPower.getData().getName().equals("")
                    && !externalPower.getData().getType().equals("")){
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
                Cache.operatorCode=externalPower.getData().getCode();
                sendCZY(externalPower.getData().getName());

                if(Cache.chooseSick.equals("0")){
                    logger.info("未配置患者选择，核验成功直接打开柜门");
                    HCProtocol.ST_OpenDoor();
                }
                MyTextToSpeech.getInstance().speak(externalPower.getData().getName()+"核验成功");
                //判断本地是否存在该记录，如果不存在则添加到本地
                ExternalPowerDao epd=new ExternalPowerDao();
                List<HashMap<String,String>> list=epd.getPower(externalPower.getData().getCard(),"",externalPower.getData().getType());
                if(list==null || list.isEmpty()){
                    //添加信息到本地
                    epd.addPower(UUID.randomUUID().toString(),externalPower.getData().getName(),externalPower.getData().getCode(),externalPower.getData().getCard(),externalPower.getData().getType());
                }
            }else{
                MyTextToSpeech.getInstance().speak("无开门权限");
            }


        }catch (Exception e){
            logger.error("获取权限出错",e);
        }

    }

    /**
     * 处理患者信息
     * @param number
     * @param data
     */
    private void dealSick(String number,String data){
        CacheSick.clear();
        try{
            ExternalSick externalSick= JSON.parseObject(data, new TypeReference<ExternalSick>(){});
            for(Sick sick : externalSick.getData()){
                CacheSick.add(sick.getTime(),sick.getName(),sick.getCode(),sick.getDept(),sick.getOperaid(),sick.getOperaname());
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
            JSONObject jsonData = new JSONObject(value);
            String msg =jsonData.get("message")==null?"":jsonData.get("message").toString();
            if(!msg.equals("0")){
                logger.info("耗材处理返回错误数据");
                //数据返回错误
                showError(jsonData.get("data")==null?"":jsonData.get("data").toString());
                return;
            }
            long start=System.currentTimeMillis();
            ActionProduct actionProduct = JSON.parseObject(value, new TypeReference<ActionProduct>(){});

            if(actionProduct.getData()!=null){
                for(Product product :actionProduct.getData()){
                    if(product.getGg()!=null && !product.getGg().equals("")){
                        if(product.getGg().length()>15){
                            product.setGg(product.getGg().substring(0,15));
                        }
                    }
                    if(product.getOperation().equals("存")){
                        Cache.listOperaSave.add(product);
                    }else if(product.getOperation().equals("取")){
                        Cache.listOperaOut.add(product);
                    }
                }
            }
            for(int i=0;i<30;i++){
                if(Cache.myHandleAccess==null){
                    Thread.sleep(100);
                }else{
                    break;
                }
            }
            Thread.sleep(1000);
            if(Cache.myHandleAccess!=null){
                Message message = Message.obtain(Cache.myHandleAccess);
                Bundle bund = new Bundle();
                bund.putString("show","1");
                message.setData(bund);
                Cache.myHandleAccess.sendMessage(message);
                logger.info("耗材确认界面，发送数据显示完成");
            }else{
                logger.info("耗材确认界面已经关闭，无需展示数据");
            }
            logger.info("耗材操作统计耗时:"+(System.currentTimeMillis()-start));
        }catch (Exception e){
            logger.error("处理服务器返回的耗材数据出错",e);
        }


    }

    /**
     * 患者耗材查询
     * @param number
     * @param value
     */
    private void dealProductSearch(String number,String value){
        ProductSearch productSearch=JSON.parseObject(value,new TypeReference<ProductSearch>(){});
        if(productSearch.getData()!=null){
            for(Product product : productSearch.getData()){
                if(product.getGg()!=null && !product.getGg().equals("")){
                    if(product.getGg().length()>15){
                        product.setGg(product.getGg().substring(0,15));
                    }
                }
            }
        }

        CacheSick.listEP.clear();
        CacheSick.listEP.addAll(productSearch.getData());
        Message message = Message.obtain(Cache.myHandle);
        Bundle bund = new Bundle();
        bund.putString("ui","productsearch");
        message.setData(bund);
        Cache.myHandle.sendMessage(message);
    }

    /**
     * 过期耗材
     * @param number
     * @param value
     */
    private void dealProductExpire(String number,String value){
        ProductExpire productExpire =JSON.parseObject(value,new TypeReference<ProductExpire>(){});
        if(productExpire.getData()!=null){
            for(Product product : productExpire.getData()){
                if(product.getGg()!=null && !product.getGg().equals("")){
                    if(product.getGg().length()>15){
                        product.setGg(product.getGg().substring(0,15));
                    }
                }
            }
        }
        CacheSick.listExpire.clear();
        CacheSick.listExpire.addAll(productExpire.getData());
        Message message = Message.obtain(Cache.myHandle);
        Bundle bund = new Bundle();
        bund.putString("ui","productexpire");
        message.setData(bund);
        Cache.myHandle.sendMessage(message);
    }

    /**
     * 处理统计信息
     * @param number
     * @param value
     */
    private void dealTotal(String number,String value){
        try{
            JSONObject jsonData = new JSONObject(value);
            String msg =jsonData.get("message")==null?"":jsonData.get("message").toString();
            if(!msg.equals("0")){
                //数据返回错误
                showError(jsonData.get("data")==null?"":jsonData.get("data").toString());
                return;
            }
        }catch (Exception e){
            logger.error("返回错误数据，解析出错",e);
            return;
        }
        long start=System.currentTimeMillis();
        try{
            Cache.mapTotal.clear();
            TotalProduct totalProduct = JSON.parseObject(value, new TypeReference<TotalProduct>(){});
            logger.info("耗材统计解析耗时:"+(System.currentTimeMillis()-start));

            if(totalProduct.getData()!=null){
                for(TotalMessage totalMessage :totalProduct.getData()){

               /*     for(Product product : totalMessage.getJxq()){
                        if(product.getGg()!=null && !product.getGg().equals("")){
                            if(product.getGg().length()>15){
                                product.setGg(product.getGg().substring(0,15));
                            }
                        }
                    }

                    for(Product product : totalMessage.getQt()){
                        if(product.getGg()!=null && !product.getGg().equals("")){
                            if(product.getGg().length()>15){
                                product.setGg(product.getGg().substring(0,15));
                            }
                        }
                    }*/

                    if(totalMessage.getLocation().equals("1")){
                        Cache.mapTotal.put("1",totalMessage);
                    }
                    if(totalMessage.getLocation().equals("2")){
                        Cache.mapTotal.put("2",totalMessage);
                    }
                    if(totalMessage.getLocation().equals("3")){
                        Cache.mapTotal.put("3",totalMessage);
                    }
                    if(totalMessage.getLocation().equals("4")){
                        Cache.mapTotal.put("4",totalMessage);
                    }
                    if(totalMessage.getLocation().equals("5")){
                        Cache.mapTotal.put("5",totalMessage);
                    }
                    if(totalMessage.getLocation().equals("6")){
                        Cache.mapTotal.put("6",totalMessage);
                    }
                }
            }
            logger.info("耗材缓存整理完成");
            if(Cache.myHandle!=null){
                logger.info("开始发送数据更新到主界面");
                Message messageInitXQ = Message.obtain(Cache.myHandle);
                Bundle bundInitXQ = new Bundle();
                bundInitXQ.putString("initJXQExternal","1");
                messageInitXQ.setData(bundInitXQ);
                Cache.myHandle.sendMessage(messageInitXQ);
                logger.info("发送数据更新到主界面完成");
            }else{
                logger.info("myHandle为空");
            }


        }catch (Exception e){
            logger.error("初始化效期出错",e);
        }
    }

   private void showError(String error){
       if(Cache.myHandleAccess!=null){
           Message message = Message.obtain(Cache.myHandleAccess);
           Bundle bund = new Bundle();
           bund.putString("alert",error);
           message.setData(bund);
           Cache.myHandleAccess.sendMessage(message);
       }else if(Cache.myHandleLockScreen!=null){
           Message message = Message.obtain(Cache.myHandleLockScreen);
           Bundle bund = new Bundle();
           bund.putString("alert",error);
           message.setData(bund);
           Cache.myHandleLockScreen.sendMessage(message);
       }else if(Cache.myHandleSick!=null){
           Message message = Message.obtain(Cache.myHandleSick);
           Bundle bund = new Bundle();
           bund.putString("alert",error);
           message.setData(bund);
           Cache.myHandleSick.sendMessage(message);
       }else if(Cache.myHandle!=null){
           Message message = Message.obtain(Cache.myHandle);
           Bundle bund = new Bundle();
           bund.putString("alert",error);
           message.setData(bund);
           Cache.myHandle.sendMessage(message);
       }
   }
}
