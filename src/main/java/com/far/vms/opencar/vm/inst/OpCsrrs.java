package com.far.vms.opencar.vm.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.vm.StaticRes;

/*
 * @description:
 * CSRR 指令翻译为CSRRS
 * @author mike/Fang.J
 * @data 2022/11/18
 */
public class OpCsrrs {

    //操作码
    private int opcode;
    //code
    private int code;

    //源寄存器
    private int rs1;
    //csr寄存器
    private int csr;
    //func4
    private int func3;
    //立即数 4:0
    private int rd;


    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpCsrrs setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpCsrrs setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpCsrrs setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpCsrrs setCode(int code) {
        this.code = code;
        return this;
    }

    public void process() {

        rd = 0b11111 & (code >> 7);
        rs1 = 0b11111 & (code >> 15);
        csr = 0xFFF & (code >> 20);
        long t = ctx.register.getCsrReg(csr);
        long t1 = ctx.register.getValOfRid(rs1);
        if (rs1 != 0) {//只有rs1不为0时，才会发生写操作
            ctx.register.setCsrReg(csr, t | t1);
        }

        ctx.register.setValOfRid(rd, t);
        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {


        }
    }
}
