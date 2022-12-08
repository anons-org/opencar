package com.far.vms.opencar.ui.main.RightTablePanle;

public class RegData {


    String regName;
    int addr;
    long val;
    //用于显示的值，方便格式化
    String viewVal;

    String viewAddr;


    public String getViewAddr() {
        return viewAddr;
    }


    public String toHexStr() {
        return "0x" + Long.toHexString(val);
    }

    public String toBinStr() {
        return "0b" + Long.toBinaryString(val);
    }


    public String getViewVal() {
        return viewVal;
    }

    public void setViewVal(String viewVal) {
        this.viewVal = viewVal;
    }

    public String getRegName() {
        return regName;
    }

    public void setRegName(String regName) {
        this.regName = regName;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
        this.viewAddr = String.valueOf(addr);
    }

    public void setViewAddr(String viewAddr) {
        this.viewAddr = viewAddr;
    }

    public long getVal() {
        return val;
    }

    public void setVal(long val) {
        this.val = val;

    }

    @Override
    public String toString() {
        return regName + " " + " " + viewAddr + " " + viewVal;
    }
}
