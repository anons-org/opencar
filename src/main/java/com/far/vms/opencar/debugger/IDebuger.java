package com.far.vms.opencar.debugger;

import com.far.vms.opencar.board.Cpu;

/*
 * @description: 此处定义的接口主要是给CPU调用
 * @author mike/Fang.J
 * @data 2022/12/4
 */
public interface IDebuger {

    //寄存器写入事件
    public void onWriteReg(Cpu ctx, int rid, long val);

    public boolean isOpcMonitor();

    public void monitor();

    public void setStat(short stat);

    //设置是否监视PC变化
    public void setOpcMonitor(boolean opcMonitor);

    //是否需要需要检测PC断点
    public boolean simIsChkPcBreak();

    // 发送PC断点
    public void simInformPcBreak(final Cpu ctx, final long pc);
    //发送一下单步触发 具体的事件由调式器完成
    public void simInformStep(final Cpu ctx, final long pc);

    public boolean simIsStep();


}
