package com.far.vms.opencar.board;


import com.far.vms.opencar.hardware.interf.IExternalDeviceMemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * @description: 总线
 * @author mike/Fang.J
 * @data 2022/11/17
 */
public class Bus implements IBus {


    //所有在总线注册了地址的设备
    Map<Long, IBus> dvrMap;
    //内存
    Dram dram;

    List<IExternalDeviceMemory> edmms = new ArrayList<>();

    public void addEdmm(IExternalDeviceMemory edmm) {
        edmms.add(edmm);
    }

    public Dram getDram() {
        return dram;
    }

    public void setDram(Dram dram) {
        this.dram = dram;
    }

    @Override
    public void storeByte(long addr, byte val) {

        //是否有监控存在 有监控存在表示有设备注册到了该地址
        boolean hasmm = false;
        for (IExternalDeviceMemory e : edmms) {
            if (e.monitorMemoryChange(addr, val)) {
                hasmm = true;
            }
        }
        //没有监控存在就要写数据到内存
        if (!hasmm) {

            dram.storeByte(addr, val);
        }
    }

    @Override
    public byte loadByte(long addr) {

        //是否有监控存在 有监控存在表示有设备注册到了该地址
        boolean hasmm = false;

        for (IExternalDeviceMemory e : edmms) {
            if (e.isMeMonitorAddr(addr)) {
                return e.monitorMemoryRead(addr);
            }
        }
        return dram.loadByte(addr);

    }


    @Override
    public void storeDw(long addr, int val) {
        dram.storeDw(addr, val);
    }

    @Override
    public void storeDDw(long addr, long val) {
        dram.storeDDw(addr, val);
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
