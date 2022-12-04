package com.far.vms.opencar.instruct.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.debugger.server.DServer;
import com.far.vms.opencar.instruct.StaticRes;

/*
 * @description:
 * lui
 * @author mike/Fang.J
 * @data 2022/11/19
 */
public class OpLui  {

    //操作码
    private int opcode;
    //code
    private int code;

    //目标寄存器
    private int rd;

    //移多少位 该字段6b
    private int imm;


    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpLui setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public OpLui setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpLui setCode(int code) {
        this.code = code;
        return this;
    }


    public void process() {

        //5b
        rd = 0b11111 & (code >> 7);
        //左移12 去掉低12b 再右移12b 腾出低位12b
        imm = (code >> 12) << 12;

        long v = 0xFFFFFFFFL & imm;
        ctx.register.setRegVal(rd, v);


        if (DServer.iDebugQuest.getDebugger().isOpcMonitor()) {

        }

    }


}
