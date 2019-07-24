package com.st.p2018.util;

import com.st.p2018.entity.Product;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */

public class CacheSick {
    private static List<String> listSickOperaID=new ArrayList<String>() ;  //存放手术ID
    private static List<String> listSickMess=new ArrayList<String>(); //存放界面显示信息
    private static HashMap<String,String> mapSickMessAndID=new HashMap<String,String>(); //界面显示信息与手术ID进行关联
    public static String sickChoose="";
    public static List<Product> listEP=new ArrayList<Product>();
    public static List<Product> listExpire=new ArrayList<Product>();

    public static void add(String time,String name,String code,String dept,String operaid,String operaname){
        listSickOperaID.add(operaid);
        String value=name;
        String valuenohtml=name;

        if(!operaname.equals("")){
            value=value+"<font color=\"0000FF\"> | </font>"+operaname;
            valuenohtml=valuenohtml+" | "+operaname;
        }

        if(!time.equals("")){
            value=value+"<font color=\"#0000FF\"> | </font>"+time;
            valuenohtml=valuenohtml+" | "+time;
        }
        if(!code.equals("")){
            value=value+"<font color=\"#0000FF\"> | </font>"+code;
            valuenohtml=valuenohtml+" | "+code;
        }

        if(!operaname.equals("")){
            value=value+"<font color=\"#0000FF\"> | </font>"+operaname;
            valuenohtml=valuenohtml+" | "+operaname;
        }
        if(!dept.equals("")){
            value=value+"<font color=\"#0000FF\"> | </font>"+dept;
            valuenohtml=valuenohtml+" | "+dept;
        }

        listSickMess.add(value);

        mapSickMessAndID.put(valuenohtml,operaid);
    }
    public static void clear(){
        listSickOperaID.clear();
        listSickMess.clear();
        mapSickMessAndID.clear();

    }
    public static List<String> getSickOperaID(){
        return listSickOperaID;
    }

    public static List<String> getSickMess(){
        return listSickMess;
    }

    public static HashMap<String,String> getSickMessAndID(){
        return mapSickMessAndID;
    }
}
