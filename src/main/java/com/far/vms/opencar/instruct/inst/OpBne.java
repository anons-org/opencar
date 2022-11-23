package com.far.vms.opencar.instruct.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.instruct.StaticRes;

/*
 * @description:
 * 不等于
 * @author mike/Fang.J
 * @data 2022/11/19
 */
public class OpBne {

    //操作码
    private int opcode;
    //code
    private int code;

    //源寄存器
    private int rs1;


    //目标寄存器
    private int rs2;

    //func4
    private int func3;


    private int ofst1;


    private int ofst2;


    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpBne setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpBne setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpBne setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpBne setCode(int code) {
        this.code = code;
        return this;
    }


    public void process() {
        /**
         * rs1 5bit
         1、移除低15位
         2、左移后高位的5位是寄存器编号
         3、高5位移动低5位
         4、保留低5位其余全部置零
         */
        rs1 = 0b11111 & (code >> 15);
        rs2 = 0b11111 & (code >> 20);

        ofst1 = 0b11111 & (code >> 7);
        //需要保留符号位
        /**
         右移25位保留 将高7位移动到低7为
         左移5位留出空间
         */

        int imm = ((code >> 25) << 5) | (0b11111 & (code >> 7));
        if (ctx.register.getRegVal(rs1) != ctx.register.getRegVal(rs2)) {
            long addr = ctx.getPC() + imm;
            ctx.setPC(addr);
        }


        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {

        }

    }
}
