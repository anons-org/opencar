package com.far.vms.opencar.instruct.inst;

import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.debugger.server.DServer;
import com.far.vms.opencar.instruct.StaticRes;

public class OpJal extends OpBase<OpJal> {

    //源寄存器
    private int rs1;
    private int imm;
    private int uimm;
    private int rd;

    //func4
    private int func3;

    public int getFunc3() {
        return func3;
    }

    public OpJal setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }


    public void process() {
        //目标寄存器
        rd = 0b11111 & (code >> 7);

        //https://www.cnblogs.com/gaozhenyu/p/14166473.html
        imm =  ( (code >> 11) & 0xfff00000 ) | ( code & 0xff000 ) | ( (code >> 9) & 0x800 ) | ( ((code >> 21) & 0x3ff) << 1 );

        //uimm = ( (code >> 11) & 0x100000 ) | ( code & 0xff000 ) | ( (code >> 9) & 0x800 ) | ( ((code >> 21) & 0x3ff) << 1 );
        //存储当前PC
        long curPc = ctx.getPC();
        //当前PC+imm
        long targetAddr = curPc + imm;
        //设置跳转地址
        ctx.setPC(targetAddr);
        //存储跳转指令之后的地址,因为jal 常被用做调用子程序，子程序返回时，需要从
        //jal指令之后的指令开始执行
        ctx.register.setRegVal(rd, curPc + 4);
        if (DServer.iDebugQuest.getDebugger().isOpcMonitor()) {
            String s = String.format("jal target addr to %x, the imm 0x%x", targetAddr, imm);
            System.out.println(s);
        }

    }
}
