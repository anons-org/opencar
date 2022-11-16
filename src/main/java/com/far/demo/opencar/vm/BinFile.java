package com.far.demo.opencar.vm;

import com.far.demo.opencar.utils.ByteUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class BinFile {


    private ByteArrayInputStream data;
    private int curReadIdx = 0;

    private byte[] buf;

    private int defaultBufferSize = 0;

    public int available() {
        return data.available();
    }


    public int getBufSize() {
        return defaultBufferSize;
    }

    public void setBufSize(int bufSize) {
        this.defaultBufferSize = bufSize;
    }

    public byte[] readByte4() {
        data.read(buf, curReadIdx, 4);
        curReadIdx += 4;
        return buf;
    }

    public int readInt() {
        //自动 偏移Pos
        data.read(buf, 0, 4);
        curReadIdx += 4;
        return ByteUtil.bytesToInt(buf);
    }


    public long readLong() {
        //自动 偏移Pos
        data.read(buf, 0, 8);
        curReadIdx += 8;
        return ByteUtil.bytes2long(buf);
    }


    public void setData(byte[] d) {
        if (defaultBufferSize <= 0) {
            System.out.println("You need to set the buffer size first.");
            return;
        }
        buf = new byte[defaultBufferSize];
        this.data = new ByteArrayInputStream(d);
    }

    public int getCurReadIdx() {
        return curReadIdx;
    }

    public void setCurReadIdx(int curReadIdx) {
        this.curReadIdx = curReadIdx;
    }
}
