package com.far.vms.opencar.vm.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.vm.StaticRes;

/*
 * @description:
 * @author mike/Fang.J
 * @data 2022/11/19
 */
public class OpAuipc {

    //操作码
    private int opcode;
    //code
    private int code;


    //目标寄存器
    private int rd;

    //func4
    private int func3;

    //立即数 20b
    private int imm;


    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpAuipc setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpAuipc setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpAuipc setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpAuipc setCode(int code) {
        this.code = code;
        return this;
    }


    public void process() {
        //5b
        rd = 0b11111 & (code >> 7);
        //20b
        imm = 0xFFFFF & (code >> 12);
        //左移12位 低位补0 注意此处没有补0的操作，左移的时候自动填充0了
        imm = imm << 12;
        //这个会有BUG...
        long pc = ctx.getPC();
        long result = pc + imm;

        ctx.register.setRegVal(rd, result);
        if (Debugger.Stat.DEBUG == StaticRes.debugger.getOpcMonitor()) {

        }

    }
}
