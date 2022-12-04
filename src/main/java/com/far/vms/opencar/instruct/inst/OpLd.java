package com.far.vms.opencar.instruct.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.debugger.server.DServer;
import com.far.vms.opencar.instruct.StaticRes;

public class OpLd {

    //操作码
    private int opcode;
    //code
    private int code;

    //源寄存器
    private int rs1;

    private int imm;
    private int rd;

    //func4
    private int func3;

    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpLd setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpLd setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpLd setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpLd setCode(int code) {
        this.code = code;
        return this;
    }

    public void process() {

        rd = 0b11111 & (code >> 7);
        rs1 = 0b11111 & (code >> 15);
        imm = 0b111_111_111_111 & (code >> 20);
        //读rs1寄存器的数据
        long rsVal = ctx.register.getRegVal(rs1);
        //rs1的值+imm
        long mAddr = rsVal + imm;
        long mVal = StaticRes.bus.loadDDw(mAddr);

        ctx.register.setRegVal(rd, mVal);

        if (DServer.iDebugQuest.getDebugger().isOpcMonitor()) {
            String info = String.format("read and load mem 0x%x val:%x", mAddr, mVal);
            System.out.println(info);
        }

    }
}
