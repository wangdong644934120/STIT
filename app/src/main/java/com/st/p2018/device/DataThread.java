package com.st.p2018.device;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.dao.EventDao;
import com.st.p2018.dao.PersonDao;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.Event;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.external.SocketClient;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Administrator on 2018/12/12.
 */

public class DataThread extends Thread {
    private Logger logger = Logger.getLogger(this.getClass());
    private PersonDao personDao=null;
    private ProductDao productDao=null;
    private HashMap<String,String> map=new HashMap<String,String>();
    private int openPDFlag=0;
    private long pdstart=0l;
    private boolean dealFlag=false;
    private String pcjd="0";

    public void run(){
        personDao= new PersonDao();
        productDao=new ProductDao();
        while(Cache.deviceCommunication){
            try {
                    HashMap<String, String> map = HCProtocol.ST_GetWorkState();
                    if (map.get("skq") != null) {
                        alaSKQ(map.get("skq").toString());
                    }
                    if (map.get("zwcgq") != null) {
                        alaZWCGQ(map.get("zwcgq").toString());
                    }
                    if (map.get("mztcgq") != null ) {
                       alaMZTCGQ(map.get("mztcgq").toString());
                    }
                    if (map.get("dks") != null ) {
                       alaDKS(map.get("dks").toString());
                    }
                    if (map.get("hwxckg") != null ) {
                        alaHWXCKG(map.get("hwxckg").toString());
                    }
                    if (map.get("zmd") != null ) {
                       alaZMD(map.get("zmd").toString());
                    }
                    if (map.get("rfid") != null ) {
                        alaRFID(map.get("rfid").toString());
                    }

                }catch(Exception e){
                    logger.error("获取数据出错", e);
                }

                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    logger.error("获取数据线程等待出错", e);
                }


            }
            logger.info("退出设备通讯线程");
    }
    //刷卡器
    private void alaSKQ(String skq){
        skq = skq.substring(7, 8);
        if (skq.equals("1")) {
            logger.info("刷卡器有动作："+skq);
            //刷卡器有动作，下发获取刷卡信息指令
            String card=HCProtocol.ST_GetUser(0);
            card=card.toUpperCase();
            logger.info("获取到卡号:"+card);
            //判断是否发送到第三方平台
            String sendValue="{\"order\":\"power\",\"type\":\"2\",\"code\":\""+Cache.appcode+"\",\"number\":\""+UUID.randomUUID().toString()+"\",\"data\":\""+card+"\"}";
            if(sendExternal(sendValue)){
                return;
            }
            if(Cache.getPersonCard){
                logger.info("人员管理界面要卡号，不进行权限判断");
                sendKH(card);
                return;
            }
            if(Cache.isPCNow==1){
                return;
            }
            List<HashMap<String,String>> list=personDao.getPersonByCardOrZW(card);
            if(list !=null && list.size()>0){
                //下发开门指令
                if(HCProtocol.ST_OpenDoor()){
                    logger.info("下发开门成功");
                    sendCZY(list.get(0).get("name"));
                    MyTextToSpeech.getInstance().speak(list.get(0).get("name")+"开门完成");
                }else{
                    logger.info("下发开门失败");
                }
            }else{
                sendCZY("");
                MyTextToSpeech.getInstance().speak("此卡无开门权限");
            }
        }
    }
    //指纹传感器
    private void alaZWCGQ(String zwcgq){
        zwcgq = zwcgq.substring(6, 8);

        if (zwcgq.equals("00")) {
            //未检测到指纹
        }
        if (zwcgq.equals("01")) {
            //指纹识别中忽略
        }
        if (zwcgq.equals("10")) {
            //指纹匹配失败
            sendCZY("");
            MyTextToSpeech.getInstance().speak("此指纹无开门权限");
        }
        //应该为11
        if (zwcgq.equals("01")) {
            logger.info("指纹模块有动作："+zwcgq);
            //指纹匹配成功，下发获取指纹编号
            //刷卡器有动作，下发获取刷卡信息指令
            String card=HCProtocol.ST_GetUser(1);
            logger.info("获取到指纹编号十六进制："+card);
            card=String.valueOf(Long.parseLong(card,  16));
            logger.info("指纹编号转十进制结果："+card);
            //判断是否发送到第三方平台
            String sendValue="{\"order\":\"power\",\"type\":\"1\",\"code\":\"" + Cache.appcode + "\",\"number\":\""+UUID.randomUUID().toString()+"\",\"data\":\""+card+"\"}";
            if(sendExternal(sendValue)){
                return;
            }
            if(Cache.isPCNow==1){
                return;
            }
            List<HashMap<String,String>> list=personDao.getPersonByCardOrZW(card);
            if(list !=null && list.size()>0){
                //下发开门指令
                if(HCProtocol.ST_OpenDoor()){
                    logger.info("下发开门成功");
                    sendCZY(list.get(0).get("name"));
                    MyTextToSpeech.getInstance().speak(list.get(0).get("name")+"开门完成");
                }else{
                    logger.info("下发开门失败");
                }

            }else{
//                sendCZY("");
//                MyTextToSpeech.getInstance().speak("此指纹无开门权限");
            }
        }

//        //指纹传感器，下发获取指纹信息

    }
    //门状态传感器 开门 监控电控锁状态   关门  监控门状态
    private void alaMZTCGQ(String mztcgq){

        mztcgq=mztcgq.substring(6,8);
        if(mztcgq.contains("1")){

            //初始化第一次判断
            if(Cache.mztcgq==2){
                logger.info("设置门开");
                //设置门开
                updateUI("men","","1");
                Cache.mztcgq=1;

            }
            //门开
            if(Cache.mztcgq==0){
                logger.info("设置门开");
                //设置门开
                updateUI("men","","1");
                Cache.mztcgq=1;
            }

        }else if(mztcgq.equals("00")){
            //初始化第一次判断
            if(Cache.mztcgq==2){
                logger.info("设置门关");
                //设置门关
                updateUI("men","","0");
                Cache.mztcgq=0;
            }
            //门关
            if(Cache.mztcgq==1){
                logger.info("设置门关");
                //设置门关
                updateUI("men","","0");
                Cache.mztcgq=0;
                //门关后如果启用锁屏了，并且是触发盘存模式，红外都没有被触发，那么直接锁屏
                if(Cache.myHandleLockScreen==null){
                    if(Cache.lockScreen.equals("1") && Cache.pc==1 && !Cache.hwxc1 && !Cache.hwxc2 && !Cache.hwxc3 && !Cache.hwxc4 && !Cache.hwxc5 && !Cache.hwxc6){
                        //开启锁屏
                        logger.info("开启锁屏");
                        Message message = Message.obtain(Cache.myHandle);
                        Bundle bund = new Bundle();
                        bund.putString("ui","lock");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                    }
                }
            }



        }
        //门状态传感器，下发获取门状态
    }
    //电控锁
    private void alaDKS(String dks){
    }
    //红外行程开关
    private void alaHWXCKG(String hwxckg){
        String hwxc6=hwxckg.substring(2,3);
        String hwxc5=hwxckg.substring(3,4);
        String hwxc4=hwxckg.substring(4,5);
        String hwxc3=hwxckg.substring(5,6);
        String hwxc2=hwxckg.substring(6,7);
        String hwxc1=hwxckg.substring(7,8);
        if(Cache.hwxc1){
            //原红外触发状态，现红外关闭状态
            if(hwxc1.equals("0")){
                logger.info("设置红外1未触发");
                updateUI("hwxc","1","0");
                Cache.hwxc1=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc1.equals("1")){
                Cache.cfpdcs.add("1");
                logger.info("设置红外1触发");
                updateUI("hwxc","1","1");
                Cache.hwxc1=true;
            }
        }
        if(Cache.hwxc2){
            //原红外触发状态，现红外关闭状态
            if(hwxc2.equals("0")){
                logger.info("设置红外2未触发");
                updateUI("hwxc","2","0");
                Cache.hwxc2=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc2.equals("1")){
                Cache.cfpdcs.add("2");
                logger.info("设置红外2触发");
                updateUI("hwxc","2","1");
                Cache.hwxc2=true;
            }
        }
        if(Cache.hwxc3){
            //原红外触发状态，现红外关闭状态
            if(hwxc3.equals("0")){
                logger.info("设置红外3未触发");
                updateUI("hwxc","3","0");
                Cache.hwxc3=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc3.equals("1")){
                Cache.cfpdcs.add("3");
                logger.info("设置红外3触发");
                updateUI("hwxc","3","1");
                Cache.hwxc3=true;
            }
        }
        if(Cache.hwxc4){
            //原红外触发状态，现红外关闭状态
            if(hwxc4.equals("0")){
                logger.info("设置红外4未触发");
                updateUI("hwxc","4","0");
                Cache.hwxc4=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc4.equals("1")){
                Cache.cfpdcs.add("4");
                logger.info("设置红外4触发");
                updateUI("hwxc","4","1");
                Cache.hwxc4=true;
            }
        }
        if(Cache.hwxc5){
            //原红外触发状态，现红外关闭状态
            if(hwxc5.equals("0")){
                logger.info("设置红外5未触发");
                updateUI("hwxc","5","0");
                Cache.hwxc5=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc5.equals("1")){
                Cache.cfpdcs.add("5");
                logger.info("设置红外5触发");
                updateUI("hwxc","5","1");
                Cache.hwxc5=true;
            }
        }
        if(Cache.hwxc6){
            //原红外触发状态，现红外关闭状态
            if(hwxc6.equals("0")){
                logger.info("设置红外6未触发");
                updateUI("hwxc","6","0");
                Cache.hwxc6=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc6.equals("1")){
                Cache.cfpdcs.add("6");
                logger.info("设置红外6触发");
                updateUI("hwxc","6","1");
                Cache.hwxc6=true;
            }
        }
    }
    //照明灯
    private void alaZMD(String zmd){
        zmd=zmd.substring(5,7);
        //照明灯初始状态判断
        if(Cache.zmdztcs==2){
            Cache.zmdztcs=1;
            if(zmd.contains("1")){
                logger.info("设置灯开");
                updateUI("deng","","1");
                Cache.zmdzt=true;

            }else if(zmd.equals("00")){
                //设置灯关
                logger.info("设置灯关");
                updateUI("deng","","0");
                Cache.zmdzt=false;
            }
        }
        if(zmd.contains("1")){
            //设置灯开
            if(!Cache.zmdzt){
                logger.info("设置灯开");
                updateUI("deng","","1");
                Cache.zmdzt=true;
            }
        }else if(zmd.equals("00")){
            //设置灯关
            if(Cache.zmdzt){
                logger.info("设置灯关");
                updateUI("deng","","0");
                Cache.zmdzt=false;
            }
        }

    }
    //RFID
    private void alaRFID(String rfid){
        //RFID读写器
        String zt = rfid.substring(0,2);
        String data=rfid.substring(2,4);
        String jd=rfid.substring(4,8);
        //logger.info("盘存状态："+zt);

        if(zt.equals("01")){
            Cache.isPCNow=1;
            //正在盘存标签
            dealFlag=true;
            if(data.equals("00")){
                //无标签数据初始
                //logger.info("正在盘存标签无标签数据");
                //无标签数据，打开盘存进度
                openJD();
            }else if(data.equals("01")){
                //有标签数据
                //logger.info("正在盘存标签有标签数据");
                openJD();
                getCard();
            }
            //更新进度
            updateJD(jd,true);

        }else if(zt.equals("00")){
            if(dealFlag){

                dealFlag=false;
                //处理器标签数据
                logger.info("标签盘存结束，开始处理数据");
                //有标签数据
                openJD();
                logger.info("标签盘存结束，耗时："+(System.currentTimeMillis()-pdstart));
                //更新进度
                updateJD(jd,false);
                //关闭盘存进度
                closeJD();
                logger.info("获取标签个数："+map.size());
                /*HashMap<String,Integer> mapCS= new HashMap<String,Integer>();
                Set<String> keys = map.keySet();
                for(String key : keys){
                    if(mapCS.get(map.get(key))==null){
                        mapCS.put(map.get(key),1);
                    }else{
                        mapCS.put(map.get(key),mapCS.get(map.get(key))+1);
                    }
                }
                Set<String> keysCS=mapCS.keySet();
                for(String key : keysCS){
                    logger.info("第"+key+"层："+mapCS.get(key));
                }*/

                //对标签数据进行处理
                if(Cache.getHCCS==0){
                    //关门盘点数据
                    if(Cache.external){
                        Cache.listOperaOut.clear();
                        Cache.listOperaSave.clear();

                        //打开耗材确认界面
                        Message message = Message.obtain(Cache.myHandle);
                        Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                        bund.putString("ui","access");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                        sendExternalProduct("product");
                    }else{
                        HashMap<String,String> mapBQ= (HashMap<String,String>)map.clone();
                        map.clear();
                        new DataDeal(mapBQ).start();
                    }
                }else if(Cache.getHCCS==1){
                    //耗材初始化要数据
                    Cache.HCCSMap=(HashMap<String,String>)map.clone();
                    map.clear();
                    sendHCCS();
                    Cache.getHCCS=0;
                }else if(Cache.getHCCS==2){
                    //主界面盘点要数据
                    if(Cache.external){
                        //打开耗材统计界面
                        Message message = Message.obtain(Cache.myHandle);
                        Bundle bund = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
                        bund.putString("ui","pd");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                        //连接第三方平台
                        sendExternalProduct("total");
                    }else{
                        //从本地数据库读取数据进行处理
                        Cache.HCCSMap=(HashMap<String,String>)map.clone();
                        map.clear();
                        sendPDZJM();
                    }
                    Cache.getHCCS=0;
                } else if(Cache.getHCCS==3){
                    //界面加载时要数据
                    if(Cache.external){
                        sendExternalProduct("total");
                    }
                    Cache.getHCCS=0;
                    if(Cache.lockScreen.equals("0")){
                        logger.info("未配置锁屏");
                        return;
                    }
                    if(Cache.mztcgq==1){
                        //门开，无需锁屏
                        logger.info("当前门状态为开，无需锁屏");
                    }else{
                        logger.info("当前门状态为关，开启锁屏");
                        Message message = Message.obtain(Cache.myHandle);
                        Bundle bund = new Bundle();
                        bund.putString("ui","lock");
                        message.setData(bund);
                        Cache.myHandle.sendMessage(message);
                    }
                }

            }
        }

    }

    //发送数据到第三方平台
    private void sendExternalProduct(String order){
        //发送数据到第三方平台
        if(SocketClient.socket!=null){
            Set<String> pr=map.keySet();
            HashMap<String,List<String>> mapJSON=new HashMap<String,List<String>>(); //key--location，List--耗材EPC

            if(Cache.pc==0){
                //如果是全部盘存，则将所有位置的标签耗材都要发送（可能该层耗材被全部拿走）
                int cs=0;
                if(Cache.gcqy1){
                    cs=1;
                }
                if(Cache.gcqy2){
                    cs=2;
                }
                if(Cache.gcqy3){
                    cs=3;
                }
                if(Cache.gcqy4){
                    cs=4;
                }
                if(Cache.gcqy5){
                    cs=5;
                }
                if(Cache.gcqy6){
                    cs=6;
                }
                for(int i=1;i<=cs;i++){
                    mapJSON.put(String.valueOf(i),new ArrayList<String>());
                }
            }else if(Cache.pc==1){
                //如果是触发盘存，则需要将所有触发的红外所在层的标签发送（可能该层耗材被全部拿走）
                for(String cf : Cache.cfpdcs){
                    if(cf.equals("0")){
                        continue;
                    }
                    mapJSON.put(cf,new ArrayList<String>());
                }
                Cache.cfpdcs.clear();
            }

            for(String p : pr){
                if(mapJSON.get(map.get(p))==null){
                    List<String> listP = new ArrayList<String>();
                    listP.add(p);
                    mapJSON.put(map.get(p),listP);
                }else{
                    mapJSON.get(map.get(p)).add(p);
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"order\":\""+order+"\",\"code\":\""+Cache.appcode+"\",\"operator\":\""+Cache.operatorCode+"\",\"number\":\"");
            sb.append(UUID.randomUUID().toString()).append("\",\"data\":[");
            Set<String> location = mapJSON.keySet();
            for(String loa : location){
                sb.append("{\"location\":\"").append(loa).append("\",");
                sb.append("\"data\":[");
                List<String> listCard= mapJSON.get(loa);
                if(!listCard.isEmpty()){
                    for(String card : listCard){
                        sb.append("\"").append(card).append("\",");
                    }
                    sb.deleteCharAt(sb.length()-1).append("]},");
                }else{
                    sb.append("]},");
                }

            }
            if(!location.isEmpty()){
                sb.deleteCharAt(sb.length()-1);
            }
            sb.append("]}");
            String sendValue=sb.toString();
            map.clear();
            SocketClient.send(sendValue);
        }
    }

    //获取标签数据
    private void getCard(){
        //有标签数据
        while(true){
            HashMap<String,String> mapSingle=HCProtocol.ST_GetCard();
            //logger.info("标签个数:"+mapSingle.size());
            //todo解析标签ID及位置，添加到map中
            map.putAll(mapSingle);
            if(mapSingle.isEmpty()){
                break;
            }
        }
    }

    /**
     * 更新盘存进度
     * @param jd
     */
    private void updateJD(String jd,boolean isJ10){

        //更新进度
        BigInteger bi = new BigInteger(jd, 2);
        int dqcs =Integer.parseInt(bi.toString());
        int zcs=Cache.pccs;
        int zjd=(int)((double)dqcs/(double)zcs*100);
        if(isJ10){
            if(zjd>=99){
                zjd=99;
            }
        }else{
            zjd=100;
        }
        if(!String.valueOf(zjd).equals(pcjd)){
            pcjd=String.valueOf(zjd);
            logger.info("开始更新进度:"+pcjd);
            sendPD(pcjd);
        }
    }
    //打开盘存进度提示
    private void openJD(){
        if(openPDFlag==0){
            sendOpenPD("openpd");
            pdstart=System.currentTimeMillis();
            openPDFlag=1;
            MyTextToSpeech.getInstance().speak("正在读取请稍候");
        }
    }
    //关闭盘存进度
    private void closeJD(){
        if(openPDFlag==1){
            Cache.isPCNow=0;
            logger.info("关闭进度条");
            sendPD("closedpd");
            openPDFlag=0;
            MyTextToSpeech.getInstance().speak("读取结束");
        }
    }
    //发送操作员
    private  void sendCZY(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("czy",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
    //发送红外状态
    private  void sendZT(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();
        if(value.equals("1")){
            value="0";
        }else if(value.equals("0")){
            value="1";
        }
        data.putString("hw",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
    //打开盘点界面
    private  void sendOpenPD(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("pd",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
    //发送近效期
    private  void sendJXQ(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("initJXQ","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
    //发送盘点进度
    private  void sendPD(String value){
        if(Cache.myHandleProgress==null){
            return;
        }
        Message message = Message.obtain(Cache.myHandleProgress);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("pd",value);
        message.setData(data);
        Cache.myHandleProgress.sendMessage(message);
    }

    //盘点主界面
    private  void sendPDZJM(){
        //打开盘点主界面
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();
        data.putString("pdzjm","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }

        //更新主界面数据
        if(Cache.myHandlePD!=null){
            message = Message.obtain(Cache.myHandlePD);
            Bundle bund = new Bundle();
            bund.putString("show","1");
            message.setData(bund);
            Cache.myHandlePD.sendMessage(message);
        }

    }
    //发送关闭锁屏
    private  void sendCloseLockScreen(){
        if(Cache.myHandleLockScreen!=null){
            Message message = Message.obtain(Cache.myHandleLockScreen);
            Bundle data = new Bundle();

            data.putString("close","1");
            message.setData(data);
            Cache.myHandleLockScreen.sendMessage(message);
        }

    }

    /**
     * 更新界面控件
     * @param type  类型 men deng  hwxc
     * @param wz  针对红外、行程开关
     * @param zt  1 触发，0关闭触发
     */
    private  void updateUI(String type,String wz,String zt){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("type",type);
        data.putString("wz",wz);
        data.putString("zt",zt);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    class DataDeal extends Thread{
        HashMap<String,String> mapDeal=null;
        public DataDeal(HashMap<String,String> mapDeal){
            this.mapDeal=mapDeal;
        }
        public void run(){
            Cache.listPR.clear();
           List<HashMap<String,String>> list =new ArrayList<HashMap<String,String>>();
           if(Cache.sdpdcs.equals("0")){
               //不是手动盘点
               if(Cache.pc==0){
                   //全部盘存
                   list=productDao.getAllProduct();
               }else if(Cache.pc==1){
                   //触发盘存
                   list=productDao.getPorductByCFHWXC(Cache.cfpdcs);
               }
           }else{
               //手动盘点
               list=productDao.getPorductBySDHWXC(Cache.sdpdcs);
           }
//           if(Cache.cfpdcs.equals("0")){
//                list=productDao.getAllProduct();
//           }else{
//               list=productDao.getPorductByHWXC(Cache.cfpdcs);
//           }
            Cache.sdpdcs="0";
            Cache.cfpdcs.clear();
           Set<String> dealKeys=mapDeal.keySet();
           HashMap<String,String> mapSave=new HashMap<String,String>();
           for(HashMap map : list){
               //取出标签
               if(!map.get("wz").toString().equals("0") && !dealKeys.contains(map.get("card").toString())){
                   //标签被取出
                   mapSave.put(map.get("card").toString(),"0");
                   Cache.listPR.add(new ProductRecord(map.get("pp").toString(),map.get("zl").toString(),map.get("gg").toString(),"取出",map.get("wz").toString()));
               }
               //存放标签
               if(map.get("wz").toString().equals("0") && dealKeys.contains(map.get("card").toString())){
                   //标签被存放
                   mapSave.put(map.get("card").toString(),mapDeal.get(map.get("card").toString()).toString());
                   Cache.listPR.add(new ProductRecord(map.get("pp").toString(),map.get("zl").toString(),map.get("gg").toString(),"存放",mapDeal.get(map.get("card").toString()).toString()));
               }
               //标签未动
               if(dealKeys.contains(map.get("card").toString())){
                   if(!map.get("wz").toString().equals("0") && !mapDeal.get(map.get("card").toString()).equals(map.get("wz").toString())){
                       //标签位置更换
                       mapSave.put(map.get("card").toString(),mapDeal.get(map.get("card").toString()));
                       Cache.listPR.add(new ProductRecord(map.get("pp").toString(),map.get("zl").toString(),map.get("gg").toString(),"取出",map.get("wz").toString()));
                       Cache.listPR.add(new ProductRecord(map.get("pp").toString(),map.get("zl").toString(),map.get("gg").toString(),"存放",mapDeal.get(map.get("card").toString()).toString()));
                   }
               }
           }
           //界面显示内容
            startRecord();
            //数据库更新内容
            Set<String> updatesKey=mapSave.keySet();
            for(String key : updatesKey){
                productDao.updateProductWZ(mapSave.get(key).toString(),key);
            }
            //初始化近效期图示
            sendJXQ();
        }
    }

    /**
     * 添加指纹、刷卡事件记录
     * @param code
     * @param type
     */
    private void addZWSKEvent(String code,String type){
        Event event = new Event();
        event.setId(UUID.randomUUID().toString());
        event.setCode(code);
        event.setEventType(type);
        event.setContent(code.equals("")?"失败":"成功");
        event.setTime(System.currentTimeMillis());
        EventDao eventDao=new EventDao();
        eventDao.addEvent(event);
    }


    private  void startRecord(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();
        data.putString("record","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }


    private  void sendKH(String value){
        if(Cache.myHandlePerson ==null){
            logger.info("handle卡号发送失败");
            return;
        }
        Message message = Message.obtain(Cache.myHandlePerson);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("kh",value);
        message.setData(data);
        Cache.myHandlePerson.sendMessage(message);
    }

    private  void sendHCCS(){
        if(Cache.myHandleHCCS==null){
            logger.info("初始耗材发送失败");
            return;
        }
        Message message = Message.obtain(Cache.myHandleHCCS);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("cshc","1");
        message.setData(data);
        Cache.myHandleHCCS.sendMessage(message);
    }

    private boolean sendExternal(String sendValue){
        boolean bl=false;
        if(Cache.external){
            bl=true;
            //发送数据到第三方平台
           if(SocketClient.socket!=null){
               SocketClient.send(sendValue);
           }
        }
        return bl;
    }
}
