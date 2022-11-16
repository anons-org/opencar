package com.far.demo.opencar.vm.opcode;

import com.far.demo.opencar.board.Cpu;
import com.far.demo.opencar.vm.StaticRes;

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
        long mAddr = rs1 + imm;

        long mVal = StaticRes.bus.loadDDw(mAddr);

        ctx.register.setValOfRid(rd, mVal);
//
//        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {
//            String info = String.format("addw : write reg val %x",v );
//            System.out.println(info);
//        }

    }
}
