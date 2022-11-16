package com.far.demo.opencar.board;


import java.util.Map;

//总线
public class Bus implements IBus{

    //所有在总线注册了地址的设备
    Map<Long,IBus> dvrMap;

    //内存
    Dram dram;

    public Dram getDram() {
        return dram;
    }

    public void setDram(Dram dram) {
        this.dram = dram;
    }

    @Override
    public void storeByte(long addr, byte val) {

    }

    @Override
    public void storeDw(long addr, int val) {
        dram.storeDw(addr,val);
    }

    @Override
    public void storeDDw(long addr, long val) {
     dram.storeDDw(addr,val);
    }

    @Override
    public long loadDDw(long addr) {
        return dram.loadDDw(addr);
    }

    @Override
    public int loadDw(long addr) {
        return dram.loadDw(addr);
    }

    @Override
    public short storeSw(long addr, short val) {
        return 0;
    }

    @Override
    public int loadSw(long addr) {
        return 0;
    }

    @Override
    public long load(long addr) {
        return 0;
    }
}
