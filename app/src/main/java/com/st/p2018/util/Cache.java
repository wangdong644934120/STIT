package com.st.p2018.util;

import android.content.Context;

import android.os.Handler;

import com.st.p2018.entity.Event;
import com.st.p2018.entity.ProductBar;
import com.st.p2018.entity.ProductRecord;
import com.st.p2018.view.PercentCircle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */

public class Cache {
    public static Context myContext;
    public static Handler myHandle;
    public static Handler myHandleProgress;
    public static Handler myHandleKH;
    public static boolean getPersonCard;
    public static String code;
    public static boolean mztcgq=false;//门状态传感器  true--开，false--关
    public static boolean hwxc1=false;
    public static boolean hwxc2=false;
    public static boolean hwxc3=false;
    public static boolean hwxc4=false;
    public static boolean hwxc5=false;
    public static boolean hwxc6=false;

    public static boolean gcqy1=true;
    public static boolean gcqy2=true;
    public static boolean gcqy3=true;
    public static boolean gcqy4=true;
    public static boolean gcqy5=true;
    public static boolean gcqy6=true;
    public static boolean zmdzt=false;
    public static List<ProductRecord> listPR = new ArrayList<ProductRecord>();
    public static String gx="Ⅰ型";
    public static int pc=0; //盘存方式  0-全盘，1-触发
    public static int pccs=1;   //盘存次数
    public static int zmd=0;//照明灯控制模式
    public static String sdpdcs="0";    //手动盘点层数
    public static List<String> cfpdcs=new ArrayList<String>(); //触发盘点层数 0-全部盘存，1-只盘存第一层,2...

}
