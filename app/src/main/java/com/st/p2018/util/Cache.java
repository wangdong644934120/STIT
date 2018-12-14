package com.st.p2018.util;

import android.content.Context;

import android.os.Handler;

import com.st.p2018.entity.Event;
import com.st.p2018.entity.ProductBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/11/7.
 */

public class Cache {
    public static Context myContext;
    public static Handler myHandle;
    public static String code;
    public static boolean mztcgq=false;//门状态传感器  true--开，false--关
    public static boolean hwxc1=false;
    public static boolean hwxc2=false;
    public static boolean hwxc3=false;
    public static boolean hwxc4=false;
    public static boolean hwxc5=false;
    public static boolean hwxc6=false;
    public static boolean zmdzt=false;

//    public static HashMap<String,HashMap<String,List<ProductBar>>>  getProduct(){
//        //key 近效期  key--种类
//        HashMap<String,HashMap<String,List<ProductBar>>> map = new HashMap<String,HashMap<String,List<ProductBar>>>();
//
//        HashMap mapzl= new HashMap<String,List<ProductBar>>();
//        List<ProductBar> list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-2"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-11","-1"));
//        mapzl.put("导管",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-11","-1"));
//        mapzl.put("球囊",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-11","-1"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-11","-1"));
//        mapzl.put("支架",list);
//        map.put("已过期",mapzl);
//
//
//        mapzl= new HashMap<String,List<ProductBar>>();
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","3"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","3"));
//
//        mapzl.put("导管",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","1"));
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","1"));
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","1"));
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","1"));
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","1"));
//
//        mapzl.put("球囊",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","1"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","1"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","1"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","1"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","1"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","1"));
//        mapzl.put("支架",list);
//        map.put("1天-7天",mapzl);
//
//        mapzl= new HashMap<String,List<ProductBar>>();
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-19","12"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-20","8"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","7"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","9"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","14"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","9"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","10"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","14"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","9"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","10"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","14"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","9"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","10"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","14"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","9"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","10"));
//        mapzl.put("导管",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","9"));
//        mapzl.put("球囊",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","10"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","11"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","11"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","11"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","11"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","11"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","11"));
//
//
//        mapzl.put("支架",list);
//        map.put("7天-15天",mapzl);
//
//        mapzl= new HashMap<String,List<ProductBar>>();
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-19","20"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-20","80"));
//        list.add(new ProductBar("乐普","导管","RF*DG35008M","2018-11-14","70"));
//
//        mapzl.put("导管",list);
//        list=new ArrayList<ProductBar>();
//        //list.add(new ProductBar("乐普","球囊","7mm*6cm","2018-11-14","90"));
//        mapzl.put("球囊",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","100"));
//        list.add(new ProductBar("乐普","支架","双球头覆膜型27-18-25","2018-11-14","110"));
//        mapzl.put("支架",list);
//        list=new ArrayList<ProductBar>();
//        list.add(new ProductBar("乐普","支架","导丝27-18-25","2018-11-14","100"));
//        list.add(new ProductBar("乐普","支架","导丝27-18-25","2018-11-14","110"));
//        mapzl.put("导丝",list);
//        map.put("15天以上",mapzl);
//
//
//        return map;
//    }
}
