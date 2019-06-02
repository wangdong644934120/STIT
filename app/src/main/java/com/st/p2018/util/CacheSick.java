package com.st.p2018.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2019/6/1.
 */

public class CacheSick {
    private static List<String> listSickOperaID=new ArrayList<String>() ;
    private static List<String> listSickMess=new ArrayList<String>();
    private static HashMap<String,String> mapSickMessAndID=new HashMap<String,String>();
    public static String sickChoose="";

    public static void add(String time,String name,String code,String dept,String operaid){
        listSickOperaID.add(operaid);
        listSickMess.add(name+time+code+dept);
        mapSickMessAndID.put(name+time+code+dept,operaid);
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
