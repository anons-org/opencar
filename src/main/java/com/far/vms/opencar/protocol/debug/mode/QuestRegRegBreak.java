package com.far.vms.opencar.protocol.debug.mode;

import java.util.List;

/*
 * @description: 寄存器断点，寄存器断点分 写前断点 和 写后断点
 * @author mike/Fang.J
 * @data 2022/12/9
 */
public class QuestRegRegBreak {


    List<InnerData> innerDataList;

    public List<InnerData> getInnerDataList() {
        return innerDataList;
    }

    public void setInnerDataList(List<InnerData> innerDataList) {
        this.innerDataList = innerDataList;
    }

    public static class InnerData {
        //时机
        private int opportun;
        //寄存器地址
        private int addr;
        //寄存器类型
        public int getOpportun() {
            return opportun;
        }
        public void setOpportun(int opportun) {
            this.opportun = opportun;
        }
        public int getAddr() {
            return addr;
        }
        public void setAddr(int addr) {
            this.addr = addr;
        }
    }




}
