package com.st.p2018.util;

import android.content.Context;
import android.media.AudioManager;
import android.os.RemoteException;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SynthesizerListener;
import com.st.p2018.activity.MainActivity;

import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2018/11/7.
 */

public class MyTextToSpeech {
    private static Logger logger = Logger.getLogger(MyTextToSpeech.class);
    private static MyTextToSpeech instance = null;
    // 语音合成对象
    private static SpeechSynthesizer mTts = null;

    private static String speaktext="";//播放缓存
    private static Boolean flag=false;

    private MyTextToSpeech() {
    }

    /**
     * 懒汉单例
     *
     * @param
     * @return
     */
    public static synchronized MyTextToSpeech getInstance() {
        if (instance == null) {
            instance = new MyTextToSpeech();
        }
        return instance;
    }



    /**
     * 初始化
     *
     * @param context
     */
    public void initial(Context context) {
        mTts = new SpeechSynthesizer(context, mTtsInitListener);
    }
    static int mcode = -1;
    /**
     * 初期化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {

        @Override
        public void onInit(ISpeechModule arg0, int code) {
            // Log.d(TAG, "InitListener init() code = " + code);
            mcode = code;
            if (code == ErrorCode.SUCCESS) {
                try {
                    setParam();
                    if(flag){
                        speak(speaktext);
                        flag=false;
                    }
                    logger.info("语音合成参数设置成功");
                } catch (Exception e) {
                    logger.error("语音合成参数设置出错:",e);
                }
            }
        }
    };

    private static void speakInit(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!MySpeechUtil.checkSpeechServiceInstall(Cache.myContext)) {
                        MySpeechUtil.processInstall(Cache.myContext,"SpeechService.apk");
                    }
                    MyTextToSpeech.getInstance().initial(Cache.myContext);
                } catch (Exception e) {
                    logger.error("讯飞语音重新初始化出错:",e);
                }
            }
        }).start();
    }

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    private static void setParam() {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
        mTts.setParameter(SpeechSynthesizer.VOICE_NAME, "xiaoyan");
        mTts.setParameter(SpeechSynthesizer.SPEED,"50");
        mTts.setParameter(SpeechSynthesizer.PITCH, "50");
        mTts.setParameter(SpeechSynthesizer.VOLUME, "100");
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
        @Override
        public void onBufferProgress(int progress) throws RemoteException {
            //总共进度多大
//			logger.info("讯飞语音回调监听值onBufferProgress："+progress);
        }

        @Override
        public void onCompleted(int code) throws RemoteException {
            logger.info("讯飞语音回调监听值onCompleted："+code);
            if(code != 0 && code != 20017){ //0是语音结束；20017是上次语音未结束被打断
                logger.info("讯飞语音回调监听值onCompleted值不为0或20017，重新初始化讯飞语音");
                speakInit();
            }
        }

        @Override
        public void onSpeakBegin() throws RemoteException {
            //语音开始
//			logger.info("讯飞语音回调监听值onSpeakBegin");
        }

        @Override
        public void onSpeakPaused() throws RemoteException {
//			logger.info("讯飞语音回调监听值onSpeakPaused");
        }

        @Override
        public void onSpeakProgress(int progress) throws RemoteException {
            //语音进度
//			logger.info("讯飞语音回调监听值onSpeakProgress:"+progress);
        }

        @Override
        public void onSpeakResumed() throws RemoteException {
//			logger.info("讯飞语音回调监听值onSpeakResumed");
        }
    };



    /**
     * 合成
     *
     * @param text
     * @return
     */
    public int speak(String text) {
        AudioManager audioManager = (AudioManager) Cache.myContext.getSystemService(Context.AUDIO_SERVICE);
        int current = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        if(current == 0){//若声音为0，则将声音设置为最大15
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, AudioManager.FLAG_PLAY_SOUND);
        }
        if(mcode!=0 || mTts == null)
        {
            flag=true;
            speaktext=text;
            speakInit();
        }else {
            if (mTts != null) {
                return mTts.startSpeaking(text, mTtsListener);
            }else{
                logger.info("讯飞语音播放失败:"+mTts);
                return -1;
            }
        }
        return -1;
    }

    /**
     * 停止
     */
    public void stop() {
        if (mTts != null) {
            mTts.stopSpeaking(mTtsListener);
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (mTts != null) {
            mTts.pauseSpeaking(mTtsListener);
        }
    }

    /**
     * 继续
     */
    public void resume() {
        if (mTts != null) {
            mTts.resumeSpeaking(mTtsListener);
        }
    }

    /**
     * 销毁
     */
    public void destory() {
        try{
            if (mTts != null) {
                mTts.stopSpeaking(mTtsListener);
                // 退出时释放连接
                mTts.destory();
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 设置语速
     *
     * @param speed
     */
    public void setSpeed(int speed) {
        if (mTts != null) {
            mTts.setParameter(SpeechSynthesizer.SPEED, String.valueOf(speed));
        }
    }

    /**
     * 设置音量
     *
     * @param volume
     */
    public void setVolume(int volume) {
        if (mTts != null) {
            mTts.setParameter(SpeechSynthesizer.VOLUME, String.valueOf(volume));
        }
    }
}
