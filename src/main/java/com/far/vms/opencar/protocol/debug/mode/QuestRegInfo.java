package com.far.vms.opencar.protocol.debug.mode;

import java.util.ArrayList;
import java.util.List;

public class QuestRegInfo {
    //all or appoint
    int tag;
    // tag 和 slaveTag组成一个完整的类型
    //为什么？因为寄存器分状态寄存器通用寄存器等...
    // all
    String slaveTag;


    public static class Tag{
        public final static int
                //显示单个
                S=2,
                //显示所有
                A=1;
    }


    public static class InnerInfo {
        String name;
        int addr;
        long val;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAddr() {
            return addr;
        }

        public void setAddr(int addr) {
            this.addr = addr;
        }

        public long getVal() {
            return val;
        }

        public void setVal(long val) {
            this.val = val;
        }
    }

    List<InnerInfo> regInfoLists;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public String getSlaveTag() {
        return slaveTag;
    }

    public void setSlaveTag(String slaveTag) {
        this.slaveTag = slaveTag;
    }

    public List<InnerInfo> getRegInfoLists() {
        return regInfoLists;
    }

    public void setRegInfoLists(List<InnerInfo> regInfoLists) {
        this.regInfoLists = regInfoLists;
    }

    public List<InnerInfo> getRegInfoList() {
        return regInfoLists;
    }

    public void setRegInfoList(List<InnerInfo> regInfoList) {
        this.regInfoLists = regInfoList;
    }
}
