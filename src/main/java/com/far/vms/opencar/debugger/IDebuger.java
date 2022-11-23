package com.far.vms.opencar.debugger;

import com.far.vms.opencar.board.Cpu;

public interface IDebuger {

    //寄存器写入事件
    public void onWriteReg(Cpu ctx, int rid,long val);


}
