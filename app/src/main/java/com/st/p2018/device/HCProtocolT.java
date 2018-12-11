package com.st.p2018.device;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2018/12/6.
 */

public class HCProtocolT {

    private final static long WAITTIME_OPENIP = 5;
    private final static long WAITTIME_SENDANDREAD = 10;
    static HashMap<Integer, String> hcomIP = new HashMap<Integer, String>(); // ip和socket的对应关系
    static HashMap<String, Integer> ipHcom = new HashMap<String, Integer>(); // ip和socket的对应关系
    static HashMap<String, Socket> ipSocket = new HashMap<String, Socket>(); // ip和socket的对应关系
    static HashMap<String, IPListenThread> ipDataListenThread = new HashMap<String, IPListenThread>();// ip和ListenThread的对应关系
    static HashMap<String, ReentrantLock> ipLock = new HashMap<String, ReentrantLock>();
    static HashMap<String, byte[]> threadListenIPData = new HashMap<String, byte[]>();
    static HashMap<String, ComListenThread> comDataListenComThread = new HashMap<String, ComListenThread>();// ip和ListenThread的对应关系
    static HashMap<String, SerialPort> comSerialPort = new HashMap<String, SerialPort>();
    static Logger logger = Logger.getLogger(HCProtocolT.class);
    static String comPort = "";
    static SerialPort sp = null;
    static int nBaudRate=38400;
    private static byte[] lastCard =null;// 上一次读到的数据
    private static long lastTime = 0;// 上一次的有效卡号时间戳

    /**
     * 方法说明：打开串口
     *
     * @param pPort
     *            串口号，如“COM1”，“COM2”
     * @param nBaudRate
     *            打开串口的波特率 默认38400
     * @param nCFlow
     *            流控制方式，0为无控制方式，1为软件控制方式
     * @return 如果打开成功返回串口句柄，如果打开失败返回-1
     */
    public static int BYRD_OpenCom(String pPort, int nBaudRate, int nCFlow) {
        if (ipHcom.keySet().contains(pPort)) {
            closeOneCom(pPort);
        }
        comPort = pPort;
        return openOneCom(pPort, nBaudRate);
    }

    private static void closeOneCom(String pPort) {
        try {
            comDataListenComThread.get(pPort).interrupt();
        } catch (Exception ex) {
            // logger.error(ex);
        }
        Integer hcom = ipHcom.get(pPort);

        comSerialPort.get(pPort).close();
        hcomIP.remove(hcom);
        ipHcom.remove(pPort);
        comSerialPort.remove(pPort);
        comDataListenComThread.remove(pPort);
    }

    /**
     * 方法说明：关闭串口
     *
     * @param hComm
     *            打开串口时返回的句柄
     * @return 是否操作成功，成功返回0，失败返回-1
     */
    public static int BYRD_CloseCom(int hComm) {
        return 1;
    }

    /**
     * 创建设备网络连接
     *
     * @param lPort
     *            本机端口号，默认为0
     * @param gPort
     *            目标端口号
     * @param pIp
     *            目标IP
     * @return
     */
    public static int BYRD_InitPort(int lPort, int gPort, String pIp) {
        if (ipSocket.keySet().contains(pIp)) {
            closeOneIP(pIp);
        }
        return openOneIP(pIp, gPort);
    }

    /**
     * 断开创建的网络连接
     *
     * @param hComm
     *            创建成功的返回句柄
     * @return
     */
    public static int BYRD_ClosePort(int hComm) {
        if (hcomIP.keySet().contains(hComm)) {
            closeOneIP(hcomIP.get(hComm));
        }
        return 1;
    }

    public static void BYRD_SendSound(int hComm, int nID, int nIndex,
                                      String text) {
        byte[] dataToSend = getSound(nID, nIndex, text);
        sendDataAndNoDataBack(hComm, dataToSend);
    }

    private static byte[] getSound(int nID, int nIndex, String text) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, (byte) (5 + text.length() * 2) };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { 3, (byte) 0xc0 };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] name = null;
        try {
            name = text.getBytes("GB2312");
        } catch (UnsupportedEncodingException ex) {
        }
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, name);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    /**
     * 设置声光报警
     *
     * @param hComm
     * @param nID
     * @param nIndex
     * @param nVoice
     * @param nLed
     * @param nColor
     * @return 0-成功
     */
    public static int BYRD_SetRealTimeBeep(int hComm, int nID, int nIndex,
                                           int nVoice, int nLed, int nColor) {
        byte[] dataToSend = getDataToSetRealTimeBeep(nID, nIndex, nVoice, nLed,
                nColor);
        sendDataAndNoDataBack(hComm, dataToSend);
        return 0;
    }

    /**
     * 设置继电器吸合
     *
     * @param hComm
     * @param nID
     * @param nIndex
     * @param nType
     */
    public static void BYRD_SetRealRelayControl(int hComm, int nID, int nIndex,
                                                int nType) {
        byte[] dataToSend = getDataToSetRealRelayControl(nID, nIndex, nType);
        sendDataAndNoDataBack(hComm, dataToSend);
    }

    /**
     * 获取红外状态
     *
     * @param hComm
     * @param nID
     * @return
     */

    public static byte[] BYRD_GetHongWaiState(int hComm, int nID, int index) {
        try {
            byte[] dataToSend = getDataToGetHongWai(nID, index);
            return sendDataAndGetData(hComm, dataToSend);
        } catch (Exception e) {
            logger.error("获取红外状态时出错", e);
            return null;
        }

    }

    private static byte[] getDataToGetHongWai(int nID, int nIndex) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, 5 };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { 3, (byte) 0xbd };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    /**
     * 方法说明：获取继电器吸合发送数据
     *
     * @param nID
     * @param nIndex
     * @return
     */
    private static byte[] getDataToSetRealRelayControl(int nID, int nIndex,
                                                       int nType) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, 6 };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { 3, (byte) 0xaa };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] data = new byte[] { (byte) nType };
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, data);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    /**
     * 实时报警声音控制设置
     *
     * @param hComm
     *            创建成功返回的连接句柄
     * @param nID
     *            设备ID号
     * @param nIndex
     *            发送命令序号
     * @param nRed
     *            红灯闪烁次数
     * @param nGreen
     *            绿灯闪烁次数
     * @param nBeep
     *            蜂鸣器鸣叫次数
     * @return 成功返回0
     */
    public static int BYRD_SetLEDandBeep(int hComm, int nID, int nIndex,
                                         int nRed, int nGreen, int nBeep) {
        byte[] dataToSend = getDataToSetLEDandBeep(nID, nIndex, nRed, nGreen,
                nBeep);
        sendDataAndNoDataBack(hComm, dataToSend);
        return 0;
    }

    /**
     * 方法说明：关闭一个IP
     *
     * @param pIp
     */
    private static void closeOneIP(String pIp) {
        if (ipSocket.get(pIp) == null) {
            return;
        }
        try {
            ipSocket.get(pIp).close();
            ipDataListenThread.get(pIp).interrupt();
        } catch (IOException ex) {
            // logger.error(ex);
        }
        ipSocket.remove(pIp);
        ipDataListenThread.remove(pIp);
        Integer hcom = ipHcom.get(pIp);
        hcomIP.remove(hcom);
        ipHcom.remove(pIp);
        ipLock.remove(pIp);
        threadListenIPData.remove(pIp);
    }

    /**
     * 方法说明：打开一个IP
     *
     * @param pIp
     * @param gPort
     */
    private static int openOneIP(String pIp, int gPort) {
        new OpenSocketThread(pIp, gPort).start();
        for (int i = 0; i < 3; i++) {
            try {
                if (ipHcom.get(pIp) != null) {
                    return ipHcom.get(pIp);
                }
                Thread.sleep(WAITTIME_OPENIP);
            } catch (InterruptedException ex) {
                // logger.error(ex);
            }
        }
        return -1;
    }

    /**
     * 方法说明：获取485查询指令的命令包
     *
     * @param nID
     * @param nIndex
     * @return
     */
    private static byte[] getDataToSendOfHisRecord485(int nID, int nIndex) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, 5 };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { (byte) 3, (byte) 0x1a };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);//将byte数组添加到另一个byte数组的末尾
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    /**
     * 方法说明：获取485查询指令的命令包
     *
     * @param nID
     * @param nIndex
     * @return
     */
    private static byte[] getDataToSendOfHeart(int nID, int nIndex) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, 5 };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { (byte) 0, (byte) 0 };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    /**
     * 方法说明：获取485查询指令的命令包
     * @param nID
     * @param nIndex
     * @return
     */
    private static byte[] getDataToSetRealTimeBeep(int nID, int nIndex,
                                                   int nVoice, int nLed, int nColor) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, 6 };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { 3, (byte) 0xb7 };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] data = new byte[] { (byte) ((nVoice << 1) + (nLed << 4) + (nColor << 7)) };
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, data);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    private static byte[] getDataToSetLEDandBeep(int nID, int nIndex, int nRed,
                                                 int nGreen, int nBeep) {
        byte[] head = new byte[] { 0x1b };
        byte[] length = new byte[] { 0, 9 };
        byte[] deviceID = new byte[] { 0, (byte) nID };
        byte[] order = new byte[] { 3, (byte) 0xe2 };
        byte[] index = new byte[] { (byte) nIndex };
        byte[] data = new byte[] {
                ((byte) ((nRed << 1) + (nGreen << 2) + (nBeep << 4))),
                (byte) nRed, (byte) nGreen, (byte) nBeep };
        byte[] beforeCrc = new byte[] {};
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, length);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, deviceID);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, order);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, index);
        beforeCrc = DataTypeChange.byteAddToByte(beforeCrc, data);
        byte[] crcCode = getCrcData(beforeCrc);
        return DataTypeChange.byteAddToByte(
                DataTypeChange.byteAddToByte(head, beforeCrc), crcCode);
    }

    /**
     * 方法说明：发送数据后再监听数据
     *
     * @param dataToSend
     * @return
     */
    private static byte[] sendDataAndGetData(int hComm, byte[] dataToSend) {

        String ip = hcomIP.get(hComm);

        ReentrantLock lock = ipLock.get(ip);
        if (ip == null || lock == null) {
            return null;
        }
//		logger.info("需返回，需获取锁");
        lock.lock();
//		logger.info("需返回，获取到了锁");
        try {
            if (ipDataListenThread.containsKey(ip)) {
                Socket socket = ipSocket.get(ip);
                // System.out.println("发送数据："+DataTypeChange.byteArrayToHexString(dataToSend));
                new SendDataFromSocketThread(socket, dataToSend).run();
            } else if (comDataListenComThread.containsKey(ip)) {
                new SendDataFromComThread(hComm, dataToSend).run();
            }

            byte[] dataReceive = setAndGetData(ip, null, 2);
            if (dataReceive.length > 5 && dataReceive[0] == 27
                    && dataReceive[5] == -125 && dataReceive[6] == -67) {
                return dataReceive;
            }
            if (dataReceive.length > 5 && dataReceive[0] == 0x1b) {
                // 收到了数据，需要判断数据完整度
                int dataLength = (255 & dataReceive[1]) * 256
                        + (255 & dataReceive[2]);
                if (dataReceive.length >= dataLength + 3) {
                    // 认为收到的数据为完整数据
                    // 清空数据源
                    setAndGetData(ip, new byte[1], 1);
                    // 复制数据
                    return dataReceive;
                }
            }
            if (dataReceive.length == 1 && dataReceive[0] == 2) {
                return dataReceive;
            }
        } catch (Exception ex) {
        } finally {
            try {
                lock.unlock();
//				logger.info("需返回，释放了锁");
            } catch (Exception e) {
            }
        }

        return null;
    }

    /**
     * 方法说明：发送数据后再监听数据
     *
     * @param dataToSend
     * @return
     */
    private static void sendDataAndNoDataBack(int hComm, byte[] dataToSend) {
        String ip = hcomIP.get(hComm);
        ReentrantLock lock = ipLock.get(ip);
        if (ip == null || lock == null) {
            return;
        }
//		logger.info("无需返回，需获取锁");
        lock.lock();
//		logger.info("无需返回，获取到锁");
        try {
            long time1 = System.currentTimeMillis();
            // setAndGetData(ip, new byte[1], 1);
            if (ipDataListenThread.containsKey(ip)) {
                Socket socket = ipSocket.get(ip);
                long aa = System.currentTimeMillis();
                new SendDataFromSocketNoBackThread(socket, dataToSend).run();
                System.out.println("发送耗时：111："
                        + String.valueOf(System.currentTimeMillis() - aa));
            } else if (comDataListenComThread.containsKey(ip)) {
                new SendDataFromComNoBackThread(hComm, dataToSend).run();
            }
            logger.info("发送数据，无需响应耗时："
                    + String.valueOf(System.currentTimeMillis() - time1));
            // Thread.sleep(10);
        } catch (Exception ex) {
        } finally {
            try {
                lock.unlock();
//				logger.info("无需返回，释放锁");
            } catch (Exception e) {
            }
        }
    }

    private static int openOneCom(String pPort, int nBaudRate) {
        File file = new File(pPort); // 将串口转为文件
        try {
            sp = new SerialPort(file, nBaudRate, 0);
            int hcom = 0;
            for (int i = 0; i < 1000; i++) {
                if (hcomIP.get(i) == null) {
                    ipHcom.put(pPort, i);
                    hcomIP.put(i, pPort);
                    break;
                }
            }
            ComListenThread clt = new ComListenThread(hcom, pPort, sp);
            // clt.start();
            comDataListenComThread.put(pPort, clt);
            threadListenIPData.put(pPort, new byte[1]);
            setAndGetData(pPort, new byte[1], 1);
            comSerialPort.put(pPort, sp);
            ipLock.put(pPort, new ReentrantLock());
            return hcom;
        } catch (Exception ex) {
            return -1;
        }
    }

    static class OpenSocketThread extends Thread {

        String ip = null;
        int port = 0;

        public OpenSocketThread(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public void run() {
            try {
                Socket socket = new Socket(ip, port);
                socket.setSoTimeout(2000);
                socket.setKeepAlive(true);
                IPListenThread ltThread = new IPListenThread(ip, socket);
                ltThread.start();
                ipSocket.put(ip, socket);
                ipDataListenThread.put(ip, ltThread);
                for (int i = 0; i < 1000; i++) {
                    if (hcomIP.get(i) == null) {
                        ipHcom.put(ip, i);
                        hcomIP.put(i, ip);
                        break;
                    }
                }
                ipLock.put(ip, new ReentrantLock());
                setAndGetData(ip, new byte[1], 1);
            } catch (UnknownHostException ex) {
                // logger.error(ex);
            } catch (IOException ex) {
                // logger.error(ex);
            }
        }
    }

    /**
     * 方法说明：从socket发送数据出去
     */
    static class SendDataFromSocketThread {

        Socket socket = null;
        byte[] dataToSend = null;

        public SendDataFromSocketThread(Socket socket, byte[] dataToSend) {
            this.socket = socket;
            this.dataToSend = dataToSend;

        }

        public void run() {
            byte[] inputByte = null;
            try {
                long times = System.currentTimeMillis();

//				logger.info("开始发送数据");
                OutputStream os = socket.getOutputStream();
                os.write(dataToSend);
                os.flush();
//				logger.info("数据发送完成，并开始回读数据");
                InputStream is = socket.getInputStream();

                int lengcount = 0;
                for (int ii = 0; ii < 40; ii++) {
                    Thread.sleep(30);
//					logger.info("定义长度");
                    inputByte = new byte[is.available()];
                    int leng = is.read(inputByte);
//					logger.info("回读数据，长度："+leng);
                    if (leng == 0) {
                        lengcount++;
                    } else {
                        leng = 0;
                    }
                    if (lengcount > 30) {
                        byte[] bb = new byte[1];
                        bb[0] = 2;
                        setAndGetData(socket.getInetAddress().getHostAddress(),
                                bb, 1);
//						logger.info("循环30次，未获取到数据");
                        return;
                    }
                    if (inputByte.length < 11) {
                        setAndGetData(socket.getInetAddress().getHostAddress(),
                                new byte[1], 1);
//						logger.info("数据长度小于11");
                        continue;
                    }
                    if (inputByte[0] != 0x1b) {
                        setAndGetData(socket.getInetAddress().getHostAddress(),
                                new byte[1], 1);
//						logger.info("数据开始不合法");
                        continue;
                    }
//					logger.info("获取数据循环次数："+ii);
                    break;
                }
                if (inputByte.length < 11) {
                    return;
                }
                if (inputByte[0] != 0x1b) {
                    return;
                }
                if (inputByte.length > 13) {
                    logger.info("获取数据耗时："
                            + String.valueOf(System.currentTimeMillis()
                            - times));
                }

                byte[] buffer = new byte[1400];
                int bufferstar = 0;
                int datalenth = 0;

                System.arraycopy(inputByte, 0, buffer, bufferstar,
                        inputByte.length);
                bufferstar = bufferstar + inputByte.length;

                byte[] bytelen = new byte[2];
                try {
                    bytelen[0] = inputByte[1];
                    bytelen[1] = inputByte[2];
                } catch (Exception ea) {
                    System.out.println("inputByte长度：" + inputByte.length);
                    System.out.println("inputByte"
                            + DataTypeChange.byteArrayToHexString(inputByte));
                    logger.error("整理数据长度出错", ea);
                    return;
                }

                datalenth = Integer.valueOf(
                        DataTypeChange.byteArrayToHexString(bytelen), 16);

                if (inputByte.length < datalenth + 5) {
                    System.out.println("需分多次提取数据");
                    // 需分多次提取
                    for (int i = 0; i < 100; i++) {
                        System.out.println("提取次数：" + i);
                        // InputStream is1 = socket.getInputStream();
                        byte[] onebyte = new byte[is.available()];
                        is.read(onebyte);
                        System.arraycopy(onebyte, 0, buffer, bufferstar,
                                onebyte.length);
                        bufferstar = bufferstar + onebyte.length;
                        if (bufferstar < datalenth) {
                            Thread.sleep(10);
                            continue;
                        } else {
                            byte[] byfl = new byte[datalenth + 5];
                            System.arraycopy(buffer, 0, byfl, 0, byfl.length);
                            buffer = new byte[byfl.length];
                            System.arraycopy(byfl, 0, buffer, 0, buffer.length);
                            break;
                        }
                    }

                }
                if (buffer[0] == 0x1b && datalenth > 6) {
                    // System.out.println("放数据:"+DataTypeChange.byteArrayToHexString(buffer));
                    setAndGetData(socket.getInetAddress().getHostAddress(),
                            buffer, 1);
                    // System.out.println("数据提取完成耗时： " +
                    // String.valueOf(System.currentTimeMillis() - timess));
                }
                if (buffer[0] == (byte) 0x1b && buffer[5] == (byte) 0x80
                        && buffer[6] == (byte) 0x00) {
                    // 心跳返回
                    setAndGetData(socket.getInetAddress().getHostAddress(),
                            buffer, 1);
                }
            } catch (Exception ex) {
                // logger.error("数据发送时报错", ex);
                // System.out.println("设备不在线了---------------");
                byte[] bb = new byte[1];
                bb[0] = 2;
                setAndGetData(socket.getInetAddress().getHostAddress(), bb, 1);
            }
        }
    }

    /**
     * 方法说明：从socket发送数据出去
     */
    static class SendDataFromSocketNoBackThread {

        Socket socket = null;
        byte[] dataToSend = null;

        public SendDataFromSocketNoBackThread(Socket socket, byte[] dataToSend) {
            this.socket = socket;
            this.dataToSend = dataToSend;

        }

        public void run() {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(dataToSend);
                os.flush();
            } catch (Exception ex) {
                logger.error("数据发送时报错", ex);
            }
        }
    }

    /**
     * 方法说明：从串口发送数据出去
     */
    static class SendDataFromComThread {

        int hcom = 0;
        byte[] dataToSend = null;

        public SendDataFromComThread(int hcom, byte[] dataToSend) {
            this.hcom = hcom;
            this.dataToSend = dataToSend;
        }

        public void run() {
            try {
                if (sp == null || hcom == -1) {
                    return;
                }
//				if(flag != null){
//					byte[] data = sp.doBulkTransfer(dataToSend, 1000);
//					logger.info("0:"+data[0]+",1:"+data[1]+",length:"+data.length);
//					if (data != null && data.length == 7 && data[0] == 0x4F
//							&& data[1] == 0x4B) {
//						// data[2]卡号长度
//						try {
//							String cardUID = IDCardParser.bytes2HexString(data);
//							String card = cardUID.substring(cardUID.length() - 8,
//									cardUID.length());
//							setAndGetData(comPort, card.getBytes(), 1);
//						} catch (Exception e) {
//							logger.error("485设备轮询获取数据封装出错", e);
//						}
//					}
//					if (data != null && data.length == 12 && data[5] == 0x4F
//							&& data[6] == 0x4B) {
//						// data[2]卡号长度
//						try {
//							String cardUID = IDCardParser.bytes2HexString(data);
//							String card = cardUID.substring(cardUID.length() - 8,
//									cardUID.length());
//							setAndGetData(comPort, card.getBytes(), 1);
//						} catch (Exception e) {
//							logger.error("485设备轮询获取数据封装出错", e);
//						}
//					}
//				}

                OutputStream os = comSerialPort.get(hcomIP.get(hcom))
                        .getOutputStream();
                os.write(dataToSend);
                os.flush();
                for (int ii = 0; ii < 40; ii++) {
                    Thread.sleep(30);
                    InputStream input = comSerialPort.get(hcomIP.get(hcom))
                            .getInputStream();

                    if (input.available() > 0) {

                        byte[] buf = new byte[input.available()];
                        input.read(buf);
                        if (buf.length > 0 && buf[0] == 0x1b) {
                            if (buf.length > 15) {
                                System.out.println("设置数据："
                                        + DataTypeChange
                                        .byteArrayToHexString(buf));
                            }
                            // threadListenIPData.put(comName, buf);
                            setAndGetData(comPort, buf, 1);
                            break;
                        }
                    }
                }





                //
//				if (data.length > 5) {
//					setAndGetData(comPort, data, 1);
//				}
                // os.write(dataToSend);
                // os.flush();
                // byte[] data
                // =comSerialPort.get(hcomIP.get(hcom)).doBulkTransfer(dataToSend,
                // 500);
                // System.out.println("设置数据："
                // + DataTypeChange
                // .byteArrayToHexString(data));
                // for (int ii = 0; ii < 40; ii++) {
                // Thread.sleep(30);
                // InputStream input = sp.getInputStream();
                // // comSerialPort.get(hcomIP.get(hcom)).doBulkTransfer(cmd,
                // timeout)
                // if (input.available() > 0) {
                //
                // byte[] buf = new byte[input.available()];
                // input.read(buf);
                // if (buf.length > 0 && buf[0] == 0x1b) {
                // if (buf.length > 15) {
                // System.out.println("设置数据："
                // + DataTypeChange
                // .byteArrayToHexString(buf));
                // }
                // // threadListenIPData.put(comName, buf);
                // setAndGetData(comPort, buf, 1);
                // break;
                // }
                // }
                // }

            } catch (Exception e) {
                // System.out.println(e);
            }
        }
    }

    /**
     * 方法说明：从串口发送数据出去
     */
    static class SendDataFromComNoBackThread {

        int hcom = 0;
        byte[] dataToSend = null;

        public SendDataFromComNoBackThread(int hcom, byte[] dataToSend) {
            this.hcom = hcom;
            this.dataToSend = dataToSend;
        }

        public void run() {
            try {
                OutputStream os = comSerialPort.get(hcomIP.get(hcom))
                        .getOutputStream();
                os.write(dataToSend);
                os.flush();
            } catch (Exception e) {
                logger.error("串口发送数据出错", e);
            }
        }
    }

    /**
     * 方法说明：设置和获取数据
     *
     * @param ip
     * @param data
     * @param flag
     *            1为写数据，2为取数据
     */
    private synchronized static byte[] setAndGetData(String ip, byte[] data,
                                                     int flag) {
        if (flag == 1) {
            threadListenIPData.put(ip, data);
            return null;
        } else {
            return threadListenIPData.get(ip);
        }
    }

    private static byte[] getCrcData(byte[] inData) {
        int messageLength = inData.length;
        byte[] crcData = new byte[2];
        short currentValue = 0;

        for (int i = 0; i < messageLength; i++) {
            if (inData[i] < 0) {
                currentValue ^= (0x00ff & ((short) inData[i]));
            } else {
                currentValue ^= inData[i];
            }
            for (int j = 0; j < 8; j++) {
                if ((currentValue & 0x0001) != 0) {
                    currentValue >>= 1;
                    currentValue &= 0x7fff;
                    currentValue ^= 0x8408;
                } else {
                    currentValue >>= 1;
                    currentValue &= 0x7fff;
                }
            }
        }
        short currentValueLow = currentValue;
        short currentValueHigh = currentValue;
        currentValueLow ^= 0xffff;
        currentValueLow &= 0x00ff;
        currentValueHigh ^= 0xffff;
        currentValueHigh >>= 8;
        currentValueHigh &= 0x00ff;
        crcData[0] = (byte) currentValueHigh;
        crcData[1] = (byte) currentValueLow;
        return crcData;
    }

    /**
     * 类说明：串口监听线程
     */
    static class ComListenThread extends Thread {

        int hcom = 0;
        String comName = "";
        SerialPort sp = null;

        public ComListenThread(int hcom, String comName, SerialPort sp) {
            this.hcom = hcom;
            this.comName = comName;
            this.sp = sp;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (sp.getInputStream().available() > 0) {
                        byte[] buf = new byte[sp.getInputStream().available()];
                        sp.getInputStream().read(buf);
                        if (buf.length > 0 && buf[0] == 0x1b) {
                            if (buf.length > 15) {
                                System.out.println("设置数据："
                                        + DataTypeChange
                                        .byteArrayToHexString(buf));
                            }
                            // threadListenIPData.put(comName, buf);
                            System.out.println("数据存放前："
                                    + DataTypeChange.byteArrayToHexString(buf));
                            setAndGetData(comName, buf, 1);
                        }
                    }
                } catch (Exception ex) {
                    // logger.error(ex);
                    return;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    // logger.error(ex);
                    return;
                }
            }
        }
    }

//    /**
//     * 485设备轮询获取数据
//     *
//     * @param hComm
//     * @param nID
//     * @param nIndex
//     * @param pUidInfo
//     * @param nUidCount
//     * @return
//     */
//    public static int BYRD_GetHisRecord485(int hComm, int nID, int nIndex,
//                                           byte[] pUidInfo, int[] nUidCount,ChannelInfo ci) {
//
//        String ip = hcomIP.get(hComm);
//
//        byte[] dataBack = null;
////		dataBack = DataTypeChange.byteAddToByte(
////				sendDataAndGetData(hComm, dataToSend), new byte[30]);
//        if (ipDataListenThread.containsKey(ip)) {
//            //网口485设备轮询获取数据
//            byte[] dataToSend = getDataToSendOfHisRecord485(nID, nIndex);
//            dataBack = DataTypeChange.byteAddToByte(
//                    sendDataAndGetData(hComm, dataToSend), new byte[30]);
//        }else if (comDataListenComThread.containsKey(ip)){
//
//            int bus = Integer.valueOf(ci.getPortOrBus());
//            if(ci.getConnectType() == ChannelInfo.COMTYPE && bus == 1){
//                //串口读市民卡
//                byte[] dataToSend = getDataToSendOfHisRecord485(nID, nIndex);
//                dataBack = DataTypeChange.byteAddToByte(
//                        sendDataAndGetData(hComm, dataToSend), new byte[30]);
//            }
//            if(ci.getConnectType() == ChannelInfo.COMTYPE && bus == 2){
//                //串口读14443A卡
//                dataBack = getCard();
//            }
//            if(ci.getConnectType() == ChannelInfo.COMTYPE && bus == 3){
//                //2016读卡器模块串口读14443A卡
//                dataBack = get14443ACard();
//            }
//        }
//        // if (dataBack != null && dataBack[16] != -1 && dataBack[0] != 0) {
//        // logger.debug("485获得有效返回，获得数据内容：" +
//        // DataTypeChange.byteArrayToHexString(dataBack));
//        // }
//        if (dataBack == null || dataBack.length == 0 || dataBack[0] == 2) {
//            return 100;
//        }
//        byte[] bytelen = new byte[2];
//        bytelen[0] = dataBack[1];
//        bytelen[1] = dataBack[2];
//        int datalenth = Integer.valueOf(
//                DataTypeChange.byteArrayToHexString(bytelen), 16);
//
//        if(dataBack != null && dataBack.length == 7 && dataBack[0] == 0x4F && dataBack[1] == 0x4B){
//            // data[2]卡号长度
//            nUidCount[0] = 1;
//            System.out.println("读取到14443A卡信息");
//            pUidInfo[0] = 0x01;// 读到的卡类型
//            System.arraycopy(dataBack, 0, pUidInfo, 0, 7);// 14443A卡卡号
//            try {
//                String cardUID = IDCardParser.bytes2HexString(dataBack);
//                String card = cardUID.substring(6).toUpperCase();
//            } catch (Exception e) {
//                logger.error("14443A卡封装出错", e);
//            }
//        }else if (datalenth > 11 && dataBack.length >= 10 && dataBack[9] == 0x05) {
//            nUidCount[0] = 1;
//            System.out.println("读取到身份证信息");
//            // 读取到身份证
//            pUidInfo[0] = 0x05;// 读到的卡类型
//            pUidInfo[1] = dataBack[10];// 年
//            pUidInfo[2] = dataBack[11];// 月
//            pUidInfo[3] = dataBack[12];// 日
//            pUidInfo[4] = dataBack[13];// 时
//            pUidInfo[5] = dataBack[14];// 分
//            pUidInfo[6] = dataBack[15];// 秒
//            pUidInfo[7] = dataBack[datalenth + 5 - 3];// 进出方向
//            System.arraycopy(dataBack, 16, pUidInfo, 8, 8);// 卡号
//            System.arraycopy(dataBack, 31, pUidInfo, 16, 256);// 身份证信息
//            System.arraycopy(dataBack, 287, pUidInfo, 16 + 256, 1024);
//        } else if (datalenth > 11 && dataBack.length >= 10 && dataBack[9] == 0x06 ) {
//            // 读取到市民卡
//            nUidCount[0] = 1;
//            System.out.println("读取到市民卡信息");
//            pUidInfo[0] = 0x06;// 读到的卡类型
//            pUidInfo[1] = dataBack[10];// 年
//            pUidInfo[2] = dataBack[11];// 月
//            pUidInfo[3] = dataBack[12];// 日
//            pUidInfo[4] = dataBack[13];// 时
//            pUidInfo[5] = dataBack[14];// 分
//            pUidInfo[6] = dataBack[15];// 秒
//            pUidInfo[7] = dataBack[26];// 方向
//            System.arraycopy(dataBack, 16, pUidInfo, 8, 10);// 市民卡卡号（最前多一个0）
//        } else if (datalenth > 15 && dataBack.length >= 16) {
//            System.out.println("读取到其他卡");
//            nUidCount[0] = 1;
//            // 其他设备读到身份证UUID，市民卡卡号,15693卡号，14443卡号
//            pUidInfo[0] = dataBack[9];// 读到的卡类型
//            pUidInfo[1] = dataBack[10];// 年
//            pUidInfo[2] = dataBack[11];// 月
//            pUidInfo[3] = dataBack[12];// 日
//            pUidInfo[4] = dataBack[13];// 时
//            pUidInfo[5] = dataBack[14];// 分
//            pUidInfo[6] = dataBack[15];// 秒
//            if (dataBack[1] == 0x00 && dataBack[2] == 0x16) {
//                // 普通卡
//                pUidInfo[7] = dataBack[24];// 方向
//                System.arraycopy(dataBack, 16, pUidInfo, 8, datalenth - 14);
//                if (pUidInfo[8] == 0XFF && pUidInfo[9] == 0XFF
//                        && pUidInfo[10] == 0XFF) {
//                    nUidCount[0] = 128;
//                }
//            } else {
//                pUidInfo[7] = dataBack[26];// 方向
//                System.arraycopy(dataBack, 16, pUidInfo, 8, datalenth - 14);
//            }
//            System.out.println("读取到其他卡完成");
//        }
//
//        if (nUidCount[0] == 0) {
//            return 128;
//        }
//        return 0;
//    }

    static class IPListenThread extends Thread {

        String ip;
        Socket socket = null;

        public IPListenThread(String ip, Socket socket) {
            this.ip = ip;
            this.socket = socket;
        }

        @Override
        public void run() {

        }
    }

    /**
     * 485设备轮询获取数据
     * @param hComm
     * @param pUidInfo
     * @param nUidCount
     * @return
     */
    public static int BYRD_GetAnTheftDoor(int hComm, byte[] pUidInfo,
                                          int[] nUidCount) {
        String ip = hcomIP.get(hComm);
        ReentrantLock lock = ipLock.get(ip);
        try {
            lock.lock();
            Socket socket = ipSocket.get(ip);
            InputStream input = socket.getInputStream();
            byte[] data = new byte[input.available()];
            input.read(data);

            if (data.length == 0) {
                // logger.info("获取值为空");
                return 128;
            }

            if (data.length < 11) {
                logger.info("图书防盗门获取数据长度不正确");
                return 128;
            }
            if (data[0] != (byte) 0x1b || data[1] != (byte) 0x00
                    || data[2] != (byte) 0x06 || data[5] != (byte) 0x83
                    || data[6] != (byte) 0xa3) {
                logger.info("图书防盗门获取数据格式不正确:"
                        + DataTypeChange.byteArrayToHexString(data));
                return 128;
            }
            for (int i = 0; i < data.length; i++) {
                pUidInfo[i] = data[i];
            }
            nUidCount[0] = 1;
            return 0;
        } catch (Exception e) {
            logger.error("从防盗门获取数据出错", e);
            return 100;
        } finally {
            lock.unlock();
        }

    }

    public static int sendHeartToAnTheftDoor(int hComm, int nID, int nIndex) {
        int value = 100;
        String ip = hcomIP.get(hComm);
        ReentrantLock lock = ipLock.get(ip);
        try {
            lock.lock();
            byte[] dataToSend = getDataToSendOfHeart(nID, nIndex);
//			logger.info("心跳发送数据："+DataTypeChange.byteArrayToHexString(dataToSend));
            byte[] dataBack = DataTypeChange.byteAddToByte(
                    sendDataAndGetData(hComm, dataToSend), new byte[30]);

            // if (dataBack != null && dataBack[16] != -1 && dataBack[0] != 0) {
            // logger.debug("485获得有效返回，获得数据内容：" +
            // DataTypeChange.byteArrayToHexString(dataBack));
            // }
            // logger.info("心跳返回："+DataTypeChange.byteArrayToHexString(dataBack));
            if (dataBack == null || dataBack[0] == 2) {
                return 100;
            }

            return 0;
        } catch (Exception e) {
            logger.info("图书防盗门发送心跳出错");
        } finally {
            lock.unlock();
        }

        return value;
    }


}
