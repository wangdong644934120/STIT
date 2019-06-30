package com.st.p2018.device;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2018/12/6.
 */

public class DataTypeChange {
    public DataTypeChange() {
    }

    public static String byteArrayToHexString(byte[] byteArray) {
        String str = "";

        for(int j = 0; j < byteArray.length; ++j) {
            int byteNumber = byteArray[j];
            if(byteNumber < 0) {
                byteNumber += 256;
            }

            if(byteNumber < 16) {
                str = str + '0';
            }

            str = str + Integer.toHexString(byteNumber);
        }

        return str;
    }

    public static String byteArrayToHexString(byte[] byteArray, int position, int length) {
        StringBuffer sb = new StringBuffer();
        String tmps = null;

        for(int i = 0; i < length; ++i) {
            tmps = Integer.toHexString(byteArray[position + i] & 255);
            if(tmps.length() == 1) {
                sb.append("0" + tmps);
            } else {
                sb.append(tmps);
            }
        }

        return sb.toString();
    }

    public static String bytes2HexString(byte b) {
        return bytes2HexString(new byte[]{b});
    }

    public static String bytes2HexString(byte[] b) {
        String ret = "";

        for(int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if(hex.length() == 1) {
                hex = '0' + hex;
            }

            ret = ret + hex.toUpperCase();
        }

        return ret;
    }

    public static String byteArrayToString(byte[] byteArray) {
        String str = "";

        for(int j = 0; j < byteArray.length; ++j) {
            int byteNumber = byteArray[j];
            if(byteNumber < 0) {
                byteNumber += 256;
            }

            if(byteNumber < 10) {
                str = str + '0';
            }

            str = str + Integer.toString(byteNumber);
        }

        return str;
    }

    public static byte[] HexStringToByteArray(String str) throws DataTypeChangeException {
        if(str.length() % 2 == 1) {
            throw new DataTypeChangeException("字符串数目不是偶数，不能转化为byte[]", 1);
        } else {
            byte[] byteout = new byte[str.length() / 2];
            //int byteNumber = 0;

            for(int j = 0; j < byteout.length; ++j) {
                int highPart = str.codePointAt(2 * j);
                int lowPart = str.codePointAt(2 * j + 1);
                int byteNumber=0;
                switch(highPart) {
                    case 48:
                        byteNumber = byteNumber + 0;
                        break;
                    case 49:
                        byteNumber = byteNumber + 16;
                        break;
                    case 50:
                        byteNumber = byteNumber + 32;
                        break;
                    case 51:
                        byteNumber = byteNumber + 48;
                        break;
                    case 52:
                        byteNumber = byteNumber + 64;
                        break;
                    case 53:
                        byteNumber = byteNumber + 80;
                        break;
                    case 54:
                        byteNumber = byteNumber + 96;
                        break;
                    case 55:
                        byteNumber = byteNumber + 112;
                        break;
                    case 56:
                        byteNumber = byteNumber + 128;
                        break;
                    case 57:
                        byteNumber = byteNumber + 144;
                        break;
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 79:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    default:
                        throw new DataTypeChangeException("不是十六进制数的string", 2);
                    case 65:
                        byteNumber = byteNumber + 160;
                        break;
                    case 66:
                        byteNumber = byteNumber + 176;
                        break;
                    case 67:
                        byteNumber = byteNumber + 192;
                        break;
                    case 68:
                        byteNumber = byteNumber + 208;
                        break;
                    case 69:
                        byteNumber = byteNumber + 224;
                        break;
                    case 70:
                        byteNumber = byteNumber + 240;
                        break;
                    case 97:
                        byteNumber = byteNumber + 160;
                        break;
                    case 98:
                        byteNumber = byteNumber + 176;
                        break;
                    case 99:
                        byteNumber = byteNumber + 192;
                        break;
                    case 100:
                        byteNumber = byteNumber + 208;
                        break;
                    case 101:
                        byteNumber = byteNumber + 224;
                        break;
                    case 102:
                        byteNumber = byteNumber + 240;
                }

                switch(lowPart) {
                    case 48:
                        byteNumber += 0;
                        break;
                    case 49:
                        ++byteNumber;
                        break;
                    case 50:
                        byteNumber += 2;
                        break;
                    case 51:
                        byteNumber += 3;
                        break;
                    case 52:
                        byteNumber += 4;
                        break;
                    case 53:
                        byteNumber += 5;
                        break;
                    case 54:
                        byteNumber += 6;
                        break;
                    case 55:
                        byteNumber += 7;
                        break;
                    case 56:
                        byteNumber += 8;
                        break;
                    case 57:
                        byteNumber += 9;
                        break;
                    case 58:
                    case 59:
                    case 60:
                    case 61:
                    case 62:
                    case 63:
                    case 64:
                    case 71:
                    case 72:
                    case 73:
                    case 74:
                    case 75:
                    case 76:
                    case 77:
                    case 78:
                    case 79:
                    case 80:
                    case 81:
                    case 82:
                    case 83:
                    case 84:
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91:
                    case 92:
                    case 93:
                    case 94:
                    case 95:
                    case 96:
                    default:
                        throw new DataTypeChangeException("不是十六进制数的string'", 2);
                    case 65:
                        byteNumber += 10;
                        break;
                    case 66:
                        byteNumber += 11;
                        break;
                    case 67:
                        byteNumber += 12;
                        break;
                    case 68:
                        byteNumber += 13;
                        break;
                    case 69:
                        byteNumber += 14;
                        break;
                    case 70:
                        byteNumber += 15;
                        break;
                    case 97:
                        byteNumber += 10;
                        break;
                    case 98:
                        byteNumber += 11;
                        break;
                    case 99:
                        byteNumber += 12;
                        break;
                    case 100:
                        byteNumber += 13;
                        break;
                    case 101:
                        byteNumber += 14;
                        break;
                    case 102:
                        byteNumber += 15;
                }

                byteout[j] = (byte)byteNumber;
            }

            return byteout;
        }
    }

    public static byte[] stringToByteArray(String str) throws DataTypeChangeException {
        if(str.length() % 2 == 1) {
            throw new DataTypeChangeException("字符串长度不是偶数，不能转化为byte[]", 3);
        } else {
            int length = str.length() / 2;
            byte[] byteOut = new byte[length];

            for(int i = 0; i < length; ++i) {
                String subStr = str.substring(i * 2, i * 2 + 2);

                try {
                    byteOut[i] = Byte.valueOf(subStr).byteValue();
                } catch (NumberFormatException var6) {
                    throw new DataTypeChangeException("不是数值的string", 3);
                }
            }

            return byteOut;
        }
    }

    public static String byteToString(byte byteIn) {
        String strOut = "";

        for(int i = 7; i >= 0; --i) {
            int bitNumber = byteIn >> i & 1;
            strOut = strOut + String.valueOf(bitNumber);
        }

        return strOut;
    }

    public static byte stringToByte(String strIn) {
        int bitNumber = 0;

        for(int i = 0; i <= strIn.length() - 1; ++i) {
            String strBit = strIn.substring(i, i + 1);
            bitNumber *= 2;
            bitNumber += Integer.valueOf(strBit).intValue();
        }

        byte c = (byte)bitNumber;
        return c;
    }

    public static byte stringReverseToByte(String strIn) throws DataTypeChangeException {
        int bitNumber = 0;

        for(int i = 0; i <= strIn.length() - 1; ++i) {
            String strBit = strIn.substring(strIn.length() - i - 1, strIn.length() - i);
            bitNumber *= 2;

            try {
                bitNumber += Integer.valueOf(strBit).intValue();
            } catch (NumberFormatException var5) {
                throw new DataTypeChangeException("字符不是0或者1", 4);
            }
        }

        byte c = (byte)bitNumber;
        return c;
    }

    public static String ipByteToString(byte[] byteIP) throws DataTypeChangeException {
        String strIP = "";
        if(byteIP.length < 4) {
            throw new DataTypeChangeException("长度小于4，不能转化为ip字符串", 5);
        } else {
            for(int i = 0; i < 4; ++i) {
                int intIP = Integer.valueOf(byteIP[i]).intValue();
                if(intIP < 0) {
                    intIP += 256;
                }

                strIP = strIP + String.valueOf(intIP);
                strIP = strIP + ".";
            }

            strIP = strIP.substring(0, strIP.length() - 1);
            return strIP;
        }
    }

    public static byte[] ipStringToByte(String strIP) throws DataTypeChangeException {
        String[] subIP = strIP.split("\\.");
        byte[] byteIP = new byte[4];
        for(int i = 0; i < 4; ++i) {
            int byteNumber;
            try {
                byteNumber = Integer.valueOf(subIP[i]).intValue();
            } catch (Exception var6) {
                throw new DataTypeChangeException("不是符合标准的ip字符串", 6);
            }

            byteIP[i] = (byte)byteNumber;
        }

        return byteIP;
    }

    public static String intArrayToString(int[] intIn) {
        String strOut = "";

        for(int i = 0; i < intIn.length; ++i) {
            strOut = strOut + String.valueOf(intIn[i]);
        }

        return strOut;
    }

    public static int[] stringToIntArray(String strIn) throws DataTypeChangeException {
        int[] intOut = new int[strIn.length()];

        for(int i = 0; i < strIn.length(); ++i) {
            try {
                intOut[i] = Integer.valueOf(strIn.substring(i, i + 1)).intValue();
            } catch (NumberFormatException var4) {
                throw new DataTypeChangeException("不是数字，不能转化为整数", 7);
            }
        }

        return intOut;
    }

    public static byte[] byteAddToBytePosition(byte[] byteParent, int position, byte[] byteSon) {
        for(int i = position; i < position + byteSon.length; ++i) {
            byteParent[i] = byteSon[i - position];
            if(i == byteParent.length - 1) {
                break;
            }
        }

        return byteParent;
    }

    public static byte[] byteAddToByte(byte[] byteParent, byte[] byteSon) {
        byte[] byteOut;
        if(byteParent == null) {
            byteOut = byteSon;
        } else {
            byteOut = new byte[byteParent.length + byteSon.length];

            int j;
            for(j = 0; j < byteParent.length; ++j) {
                byteOut[j] = byteParent[j];
            }

            for(j = 0; j < byteSon.length; ++j) {
                byteOut[byteParent.length + j] = byteSon[j];
            }
        }

        return byteOut;
    }

    public static byte[] byteAddToByte(byte[] byteParent, byte byteSon) {
        byte[] byteOut;
        if(byteParent == null) {
            byteOut = new byte[]{byteSon};
        } else {
            byteOut = new byte[byteParent.length + 1];

            for(int i = 0; i < byteParent.length; ++i) {
                byteOut[i] = byteParent[i];
            }

            byteOut[byteParent.length] = byteSon;
        }

        return byteOut;
    }

    public static byte[] getSubByte(byte[] byteParent, int pointStart, int pointEnd) throws DataTypeChangeException {
        byte[] subByte = new byte[pointEnd - pointStart + 1];

        for(int i = 0; i < pointEnd - pointStart + 1; ++i) {
            try {
                subByte[i] = byteParent[pointStart + i];
            } catch (Exception var6) {
                throw new DataTypeChangeException("超出了数组界限", 8);
            }
        }

        return subByte;
    }

    public static void putShort(byte[] b, short s, int index) {
        b[index] = (byte)(s >> 8);
        b[index + 1] = (byte)(s >> 0);
    }

    public static short getShort(byte[] b, int index) {
        return (short)(b[index] << 8 | b[index + 1] & 255);
    }

    public static short getDescShort(byte[] b, int index) {
        return (short)(b[index] & 255 | (b[index + 1] & 255) << 8);
    }

    public static void putString(byte[] b, String str, int index) {
        byte[] stringByte = str.getBytes();

        for(int i = 0; i < stringByte.length; ++i) {
            b[index + i] = stringByte[i];
        }

    }

    public static String getString(byte[] b, int index) {
        int length = b.length - index;
        byte[] idByte = new byte[length];

        for(int i = 0; i < length; ++i) {
            idByte[i] = b[index + i];
        }

        return new String(idByte);
    }

    public static void putInt(byte[] bb, int x, int index) {
        bb[index + 0] = (byte)(x >> 24);
        bb[index + 1] = (byte)(x >> 16);
        bb[index + 2] = (byte)(x >> 8);
        bb[index + 3] = (byte)(x >> 0);
    }

    public static int getInt(byte[] bb, int index) {
        return (bb[index + 0] & 255) << 24 | (bb[index + 1] & 255) << 16 | (bb[index + 2] & 255) << 8 | (bb[index + 3] & 255) << 0;
    }

    public static int getDescInt(byte[] bb, int index) {
        return bb[index + 0] & 255 | (bb[index + 1] & 255) << 8 | (bb[index + 2] & 255) << 16 | (bb[index + 3] & 255) << 24;
    }

    public static void putLong(byte[] bb, long x, int index) {
        bb[index + 0] = (byte)((int)(x >> 56));
        bb[index + 1] = (byte)((int)(x >> 48));
        bb[index + 2] = (byte)((int)(x >> 40));
        bb[index + 3] = (byte)((int)(x >> 32));
        bb[index + 4] = (byte)((int)(x >> 24));
        bb[index + 5] = (byte)((int)(x >> 16));
        bb[index + 6] = (byte)((int)(x >> 8));
        bb[index + 7] = (byte)((int)(x >> 0));
    }

    public static long getLong(byte[] bb, int index) {
        return ((long)bb[index + 0] & 255L) << 56 | ((long)bb[index + 1] & 255L) << 48 | ((long)bb[index + 2] & 255L) << 40 | ((long)bb[index + 3] & 255L) << 32 | ((long)bb[index + 4] & 255L) << 24 | ((long)bb[index + 5] & 255L) << 16 | ((long)bb[index + 6] & 255L) << 8 | ((long)bb[index + 7] & 255L) << 0;
    }

    public static long getDescLong(byte[] bb, int index) {
        long back = 0L;

        for(int i = 0; i < 8; ++i) {
            back |= ((long)bb[i + 0] & 255L) << i * 8;
        }

        return back;
    }

    public static String utf8ToGBK(String str) {
        return Unicode2GBK(utf8ToUnicode(str));
    }

    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < inStr.length(); ++i) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if(ub == Character.UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if(ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = myBuffer[i] - 'ﻠ';
                sb.append((char)j);
            } else {
                short s = (short)myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }

        return sb.toString();
    }

    public static String Unicode2GBK(String dataStr) {
        try {
            String aa = new String(dataStr.getBytes("unicode"), "GB2312");
            return aa;
        } catch (UnsupportedEncodingException var6) {
            int index = 0;
            StringBuffer buffer = new StringBuffer();
            int li_len = dataStr.length();

            while(true) {
                while(index < li_len) {
                    if(index < li_len - 1 && "\\u".equals(dataStr.substring(index, index + 2))) {
                        String charStr = "";
                        charStr = dataStr.substring(index + 2, index + 6);
                        char letter = (char)Integer.parseInt(charStr, 16);
                        buffer.append(letter);
                        index += 6;
                    } else {
                        buffer.append(dataStr.charAt(index));
                        ++index;
                    }
                }

                return buffer.toString();
            }
        }
    }
    public static String getBit(byte by){
        StringBuffer sb = new StringBuffer();
        sb.append((by>>7)&0x1).append((by>>6)&0x1).append((by>>5)&0x1).append((by>>4)&0x1).append((by>>3)&0x1).append((by>>2)&0x1).append((by>>1)&0x1).append((by>>0)&0x1);
        return sb.toString();
    }
    //获取一个字节的高四位
    public static int getHeight4(byte data){
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }
    //获取一个字节的低四位
    public static int getLow4(byte data){
        int low;
        low = (data & 0x0f);
        return low;
    }


}
