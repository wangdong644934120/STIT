package com.st.p2018.device;

import android.os.Bundle;
import android.os.Message;

import com.st.p2018.util.Cache;
import com.st.p2018.util.MyTextToSpeech;

/**
 * Created by Administrator on 2018/11/8.
 */

public class Deal extends Thread {
    private String value;
    public Deal(String value){
        this.value=value;
    }
    public void run(){
        Message message = Message.obtain(Cache.myHandle);
        Bundle data = new Bundle();  //message也可以携带复杂一点的数据比如：bundle对象。
        data.putString("ts",value);
        message.setData(data);
        Cache.myHandle.sendMessage(message);
        MyTextToSpeech.getInstance().speak(value);
    }
}
