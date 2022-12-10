package com.far.vms.opencar.protocol.debug.mode;

import com.far.vms.opencar.ui.main.RightTablePanle.MemoryData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestMemoryData {



    //指定的查找的内存地址
    private long findAddr;
    //读取多少个单元
    private int unit;
    //每个单元的大小 1字节 4字节 8字节
    private int type;


    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public long getFindAddr() {
        return findAddr;
    }

    public void setFindAddr(long findAddr) {
        this.findAddr = findAddr;
    }

    /**
     * offset    1  2  3......15
     * 0x00000010 0e 0a 03......
     * 0x00000020 0e 0a 03......
     */
    List<Map<String, MemoryData.InnerMemVal>> mapList = new ArrayList<>();

    public List<Map<String, MemoryData.InnerMemVal>> getMapList() {
        return mapList;
    }

    public void setMapList(List<Map<String, MemoryData.InnerMemVal>> mapList) {
        this.mapList = mapList;
    }


}
