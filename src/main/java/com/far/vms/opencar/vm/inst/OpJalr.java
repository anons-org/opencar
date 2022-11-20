package com.far.vms.opencar.vm.inst;

import com.far.vms.opencar.board.Cpu;

public class OpJalr {

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

    public OpJalr setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpJalr setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpJalr setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpJalr setCode(int code) {
        this.code = code;
        return this;
    }

    public void process() {

        rd = 0b11111 & (code >> 7);
        rs1 = 0b11111 & (code >> 15);
        imm = 0b111_111_111_111 & (code >> 20);
        //最低位设置0
        long addr = 0b1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1110L & (rs1 + imm);

        //得到当前PC riscv中不能直接操作PC寄存器,代码上也假装规范一下
        //rd 为0都是丢弃
        ctx.register.setRegVal(rd, ctx.getPredict() - 4);
        //addr和预测地址不一致
        if (ctx.getPredict() != addr) {
            //产生flush信号
            ctx.flushReq = 1;
            ctx.ifuPcReg = addr;
        }


//
//        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {
//            String info = String.format("addw : write reg val %x",v );
//            System.out.println(info);
//        }

    }
}
