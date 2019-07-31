package com.st.p2018.util;

import android.content.Context;
import android.os.Handler;
import com.st.p2018.entity.PDEntity;
import com.st.p2018.entity.Product;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.externalentity.TotalMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */

public class Cache {
    public static Context myContext;
    public static Handler myHandle; //主界面的handle
    public static Handler myHandleProgress; //读取等待的handle
    public static Handler myHandlePerson; //人员管理界面handle
    public static Handler myHandleHCCS; //耗材管理界面耗材初始的handle
    public static Handler myHandleLockScreen; //锁屏界面的锁屏handle
    public static Handler myHandleSick;  //患者界面的患者信息显示handle
    public static Handler myHandleAccess;//耗材确认界面的handle
    public static Handler myHandlePD;   //盘点结果显示handle
    public static Handler myHandleDevice;//设备信息界面显示handle
    public static boolean getPersonCard;//是否是人员界面要卡号
    public static boolean isFirstStart;//是否是首次启动
    public static int getHCCS=0;  //0--关门盘存，1-耗材初始时要数据线，2-主界面盘点要数据,3-加载界面盘点所有耗材
    public static HashMap<String,String> HCCSMap=new HashMap<String,String>(); //key-card,value-wz
    public static int mztcgq=2;//门状态传感器  1--开，0--关
    public static int zmdztcs=2; //照明灯状态初始
    public static boolean hwxc1=false;  //界面红外触发状态
    public static boolean hwxc2=false;
    public static boolean hwxc3=false;
    public static boolean hwxc4=false;
    public static boolean hwxc5=false;
    public static boolean hwxc6=false;
    public static boolean gcqy1=false;  //柜层启用状态
    public static boolean gcqy2=false;
    public static boolean gcqy3=false;
    public static boolean gcqy4=false;
    public static boolean gcqy5=false;
    public static boolean gcqy6=false;
    public static boolean zmdzt=false;
    public static List<ProductRecord> listPR = new ArrayList<ProductRecord>();
    public static String gx="Ⅰ型"; //柜型
    public static String cpxlh="";//产品序列号
    public static String yjbbh="";//硬件版本号
    public static String gjbbh="";//固件版本号
    public static String apkversion="";//apk版本号
    public static int pc=1; //盘存方式  0-全盘，1-触发
    public static int pccs=5;   //盘存次数
    public static int pcjg=5;   //盘存时间间隔
    public static int zmd=0;//照明灯控制模式
    public static String sdpdcs="0";    //手动盘点层数
    public static String isAdmin="0";   //是否是管理员登录，0-否，1-是
    public static List<String> cfpdcs=new ArrayList<String>(); //触发盘点层数 0-全部盘存，1-只盘存第一层,2...
    public static boolean zwlrNow=false;   //正在录入指纹
    public static boolean external=false;  //是否挂接第三方平台
    public static String appname;   //app名称
    public static String appcode="0";   //app编号
    public static String ServerIP="";  //连接服务器地址
    public static int ServerPort=0;   //连接服务器端口号
    public static String threadFlag="";    //与第三方平台通讯线程标志
    public static String lockScreen="0";  //是否启用锁屏功能，0-不启用，1-启用
    public static String chooseSick="0"; //是否启用选择患者功能，0-不启用，1-启用
    public static List<Product> listOperaSave=new ArrayList<Product>();  //护士存操作缓存
    public static List<Product> listOperaOut=new ArrayList<Product>();  //护士取操作缓存
    public static HashMap<String,TotalMessage> mapTotal=new HashMap<String,TotalMessage>(); //第三方平台获取到的效期统计信息
    public static String operatorCode=""; //操作员编号
    //public static HashMap<String,PDEntity> mapPD=new HashMap<String,PDEntity>(); //key wz,value 统计  主动盘点统计界面数据结构
    public static int isPCNow=0;//正在盘存标签，0-未盘存，1-正在盘存
    public static boolean deviceCommunication=true;
    public static boolean autoClose=true;

}
