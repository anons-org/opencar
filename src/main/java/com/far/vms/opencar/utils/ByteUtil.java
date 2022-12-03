package com.far.vms.opencar.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.LongBuffer;

public class ByteUtil {

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

//    public static long bytesToLong(byte[] bytes) {
//        buffer.put(bytes, 0, bytes.length);
//        buffer.flip();//need flip
//        return buffer.getLong();
//    }


    public static int bytesToInt(byte[] a) {
        int ans = 0;
        for (int i = 0; i < 4; i++) {
            ans <<= 8;
            ans |= (a[3 - i] & 0xff);
            /* 这种写法会看起来更加清楚一些：
            int tmp=a[3-i];
            tmp=tmp&0x000000ff;
            ans|=tmp;*/
            //intPrint(ans);
        }
        return ans;
    }





    public static long bytes2long(byte[] bs) {
        int bytes = bs.length;

        switch (bytes) {
            case 0:
                return 0;
            case 1:
                return (long) ((bs[0] & 0xff));
            case 2:
                return (long) ((bs[0] & 0xff) << 8 | (bs[1] & 0xff));
            case 3:
                return (long) ((bs[0] & 0xff) << 16 | (bs[1] & 0xff) << 8 | (bs[2] & 0xff));
            case 4:
                return (long) ((bs[0] & 0xffL) << 24 | (bs[1] & 0xffL) << 16 | (bs[2] & 0xffL) << 8 | (bs[3] & 0xffL));
            case 5:
                return (long) ((bs[0] & 0xffL) << 32 | (bs[1] & 0xffL) << 24 | (bs[2] & 0xffL) << 16 | (bs[3] & 0xffL) << 8 | (bs[4] & 0xffL));
            case 6:
                return (long) ((bs[0] & 0xffL) << 40 | (bs[1] & 0xffL) << 32 | (bs[2] & 0xffL) << 24 | (bs[3] & 0xffL) << 16 | (bs[4] & 0xffL) << 8 | (bs[5] & 0xffL));
            case 7:
                return (long) ((bs[0] & 0xffL) << 48 | (bs[1] & 0xffL) << 40 | (bs[2] & 0xffL) << 32 | (bs[3] & 0xffL) << 24 | (bs[4] & 0xffL) << 16 | (bs[5] & 0xffL) << 8 | (bs[6] & 0xffL));
            case 8:
                return (long) ((bs[0] & 0xffL) << 56 | (bs[1] & 0xffL) << 48 | (bs[2] & 0xffL) << 40 | (bs[3] & 0xffL) << 32 |
                        (bs[4] & 0xffL) << 24 | (bs[5] & 0xffL) << 16 | (bs[6] & 0xffL) << 8 | (bs[7] & 0xffL));
            default:
                return 0;
        }
    }







}
