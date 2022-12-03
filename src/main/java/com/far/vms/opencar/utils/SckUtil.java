package com.far.vms.opencar.utils;

import cn.hutool.core.util.ByteUtil;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class SckUtil {

    /**
     * @param msg
     * @description: 生成支持带4字节包长的数据包
     * @return: byte[]
     * @author mike/Fang.J
     * @data 2022/12/3
     */
    public static byte[] builderOfHeaderLen(String msg) {
        byte[] sendData = msg.getBytes(StandardCharsets.UTF_8);
        int allLen = 4 + sendData.length;
        byte[] allData = new byte[allLen];
        byte[] packtHead = ByteUtil.intToBytes(allLen, ByteOrder.BIG_ENDIAN);
        System.arraycopy(packtHead, 0, allData, 0, 4);
        System.arraycopy(sendData, 0, allData, 4, sendData.length);
        return allData;
    }


}
