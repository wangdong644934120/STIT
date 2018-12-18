package com.st.p2018.device;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.dao.EventDao;
import com.st.p2018.dao.PersonDao;
import com.st.p2018.dao.ProductDao;
import com.st.p2018.entity.Event;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

import org.apache.log4j.Logger;

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
    int a=0;
    public void run(){
        personDao= new PersonDao();
        productDao=new ProductDao();
        while(true){
            try {
                    HashMap<String, String> map = HCProtocol.ST_GetWorkModel();
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
    }
    //刷卡器
    private void alaSKQ(String skq){
        skq = skq.substring(0, 1);
        if (skq.equals("1")) {
            //刷卡器有动作，下发获取刷卡信息指令
            String card=HCProtocol.ST_GetUser(0);
            List<HashMap<String,String>> list=personDao.getPersonByCardOrZW(card);
            if(list !=null && list.size()>0){
                //下发开门指令
                HCProtocol.STIT_OpenDoor();
                Cache.code=list.get(0).get("code");
                addZWSKEvent(Cache.code,Event.EVENTTYPE_SKOPENDOOR);

                sendTS("操作:"+list.get(0).get("name")+"刷卡开门成功");
                MyTextToSpeech.getInstance().speak(list.get(0).get("name")+"刷卡开门成功");

            }else{
                sendTS("报警:此卡无开门权限");
                MyTextToSpeech.getInstance().speak("此卡无开门权限");
                addZWSKEvent("",Event.EVENTTYPE_SKOPENDOOR);
            }
        }
    }
    //指纹传感器
    private void alaZWCGQ(String zwcgq){
        zwcgq = zwcgq.substring(0, 2);

        if (zwcgq.equals("00")) {
            //未检测到指纹
        }
        if (zwcgq.equals("01")) {
            //指纹识别中忽略
        }
        if (zwcgq.equals("10")) {
            //指纹匹配失败
            sendTS("报警:此指纹无开门权限");
            MyTextToSpeech.getInstance().speak("此指纹无开门权限");
        }
        if (zwcgq.equals("11")) {
            //指纹匹配成功，下发获取指纹编号
            //刷卡器有动作，下发获取刷卡信息指令
            String card=HCProtocol.ST_GetUser(1);
            List<HashMap<String,String>> list=personDao.getPersonByCardOrZW(card);
            if(list !=null && list.size()>0){
                //下发开门指令
                HCProtocol.STIT_OpenDoor();
                Cache.code=list.get(0).get("code");
                addZWSKEvent(Cache.code,Event.EVENTTYPE_ZWOPENDOOR);
                sendTS("操作:"+list.get(0).get("name")+"指纹开门成功");
                MyTextToSpeech.getInstance().speak(list.get(0).get("name")+"指纹开门成功");
            }else{
                sendTS("报警:此指纹无开门权限");
                MyTextToSpeech.getInstance().speak("此指纹无开门权限");
                addZWSKEvent("",Event.EVENTTYPE_ZWOPENDOOR);
            }
        }

//        //指纹传感器，下发获取指纹信息
//        sendTS("时间:15:43:20");
//        sendTS("报警:指纹识别失败");
//        sendTS("时间:15:43:20");
//        sendTS("操作:张大山 存100 取100");
    }
    //门状态传感器
    private void alaMZTCGQ(String mztcgq){
        mztcgq=mztcgq.substring(0,2);
        if(mztcgq.contains("0")){
            //门开
            if(!Cache.mztcgq){
                //设置门开
                updateUI("men","","1");
                Cache.mztcgq=true;
            }
        }else if(mztcgq.equals("11")){
            //门关
            if(Cache.mztcgq){
                //设置门关
                updateUI("men","","0");
                Cache.mztcgq=false;
            }
        }
        //门状态传感器，下发获取门状态
    }
    //电控锁
    private void alaDKS(String dks){
        //电控锁,下发获取电控锁状态
        dks=dks.substring(0,2);
        if(dks.contains("0")){
            //电控锁开
        }else if(dks.equals("11")){
            //电控锁关
        }
    }

    //红外行程开关
    private void alaHWXCKG(String hwxckg){
        String hwxc1=hwxckg.substring(0,1);
        String hwxc2=hwxckg.substring(1,2);
        String hwxc3=hwxckg.substring(2,3);
        String hwxc4=hwxckg.substring(3,4);
        String hwxc5=hwxckg.substring(4,5);
        String hwxc6=hwxckg.substring(5,6);
        if(Cache.hwxc1){
            //原红外触发状态，现红外关闭状态
            if(hwxc1.equals("1")){
                updateUI("hwxc","1","0");
                Cache.hwxc1=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc1.equals("0")){
                updateUI("hwxc","1","1");
                Cache.hwxc1=true;
            }
        }
        if(Cache.hwxc2){
            //原红外触发状态，现红外关闭状态
            if(hwxc2.equals("1")){
                updateUI("hwxc","2","0");
                Cache.hwxc2=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc2.equals("0")){
                updateUI("hwxc","2","1");
                Cache.hwxc2=true;
            }
        }
        if(Cache.hwxc3){
            //原红外触发状态，现红外关闭状态
            if(hwxc3.equals("1")){
                updateUI("hwxc","3","0");
                Cache.hwxc3=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc3.equals("0")){
                updateUI("hwxc","3","1");
                Cache.hwxc3=true;
            }
        }
        if(Cache.hwxc4){
            //原红外触发状态，现红外关闭状态
            if(hwxc4.equals("1")){
                updateUI("hwxc","4","0");
                Cache.hwxc4=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc4.equals("0")){
                updateUI("hwxc","4","1");
                Cache.hwxc4=true;
            }
        }
        if(Cache.hwxc5){
            //原红外触发状态，现红外关闭状态
            if(hwxc5.equals("1")){
                updateUI("hwxc","5","0");
                Cache.hwxc5=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc5.equals("0")){
                updateUI("hwxc","5","1");
                Cache.hwxc5=true;
            }
        }
        if(Cache.hwxc6){
            //原红外触发状态，现红外关闭状态
            if(hwxc6.equals("1")){
                updateUI("hwxc","6","0");
                Cache.hwxc6=false;
            }
        }else{
            //元红外关闭状态，现红外触发状态
            if(hwxc6.equals("0")){
                updateUI("hwxc","6","1");
                Cache.hwxc6=true;
            }
        }
    }
    //照明灯
    private void alaZMD(String zmd){
        zmd=zmd.substring(0,2);
        if(zmd.contains("0")){
            //设置灯开
            if(!Cache.zmdzt){
                updateUI("deng","","1");
                Cache.zmdzt=true;
            }
        }else if(zmd.equals("11")){
            //设置灯关
            if(Cache.zmdzt){
                updateUI("deng","","0");
                Cache.zmdzt=false;
            }
        }

    }
    private void alaRFID(String rfid){
        //----------模拟
//        if(rfid.equals("00000000")){
//            if(a==0){
//                HashMap<String,String> mapBQ= (HashMap<String,String>)map.clone();
//                new DataDeal(mapBQ).start();
//                a=1;
//            }
//
//        }
//        if(rfid.equals("11111111")){
//            a=0;
//        }
        //--------------------
        //RFID读写器
        String zt = rfid.substring(6,7);
        String data=rfid.substring(4,5);

        if(zt.equals("00")){
            //读写器休眠中
        }else if(zt.equals("01")){
            //正在盘存标签
            if(data.equals("01")){
                getCard();
            }else if(data.equals("10")){
                //告警
            }
        }else if(zt.equals("10")){
            //标签盘存结束
            if(data.equals("01")){
                //有标签数据
               getCard();
               //对标签数据进行处理
                HashMap<String,String> mapBQ= (HashMap<String,String>)map.clone();
                new DataDeal(mapBQ).start();
            }else if(data.equals("10")){
                //告警
            }
        }



        //检测正在盘点
//                    for(int i=1;i<=10;i++){
//                        sendPD(String.valueOf(i));
//                        sendTS("状态:盘点完成"+String.valueOf(i)+"0%");
//                        try{
//                            Thread.sleep(1000);
//                        }catch (Exception e){
//
//                        }
//                    }

    }

    private void getCard(){
        //有标签数据
        while(true){
            String card=HCProtocol.ST_GetCard();
            //todo解析标签ID及位置，添加到map中

            //盘点进度显示
            if(map.containsValue("6")){
                sendPD("95");
            }else if(map.containsValue("5")){
                sendPD("90");
            }else if(map.containsValue("4")){
                sendPD("80");
            }else if(map.containsValue("3")){
                sendPD("50");
            }else if(map.containsValue("2")){
                sendPD("30");
            }else if(map.containsValue("1")){
                sendPD("10");
            }

            if(card.equals("")){
                break;
            }
        }
    }

    private  void sendTS(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("ts",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    private  void sendZT(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        if(value.equals("1")){
            value="0";
        }else if(value.equals("0")){
            value="1";
        }
        data.putString("hw",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }

    private  void sendPD(String value){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。

        data.putString("pd",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
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
            mapDeal.put("A12245678","1");
            mapDeal.put("A12345678","1");
            mapDeal.put("B12345678","1");
            mapDeal.put("C12345679","1");
            mapDeal.put("D12345679","1");
            mapDeal.put("E12345680","1");
            mapDeal.put("F12345681","2");
//            mapDeal.put("F12345680","2");
           List<HashMap<String,String>> list = productDao.getAllProduct();
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
                   mapSave.put(map.get("card").toString(),"1");
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


        }
    }

    private  void startRecord(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("record","1");
        message.setData(data);
        Cache.myHandle.sendMessage(message);
    }
}
