package com.far.vms.opencar.hardware.interf;

/*
 * @description: TODO
 *  外设需要实现的关于内存相关的接口
 * @author mike/Fang.J
 * @data 2022/11/21
 */
public interface IExternalDeviceMemory {

    //被内存dram调用
    public boolean monitorMemoryChange(long addr, byte val);

    public byte monitorMemoryRead(long addr);

    //是否需要观察内存的读
    public boolean isMonitorRead();

    //是否是设备监视的地址
    public boolean isMeMonitorAddr(long addr);

}
