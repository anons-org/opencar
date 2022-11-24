package com.far.vms.opencar.instruct.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.instruct.StaticRes;

/*
 * @description:
 * 左移
 * @author mike/Fang.J
 * @data 2022/11/19
 */
public class OpSlli {

    //操作码
    private int opcode;
    //code
    private int code;

    //源寄存器
    private int rs1;


    //目标寄存器
    private int rd;

    //func4
    private int func3;

    //移多少位 该字段6b
    private int shamt;


    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpSlli setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpSlli setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpSlli setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpSlli setCode(int code) {
        this.code = code;
        return this;
    }


    public void process() {
        /**
         * rs1 5bit

         */
        rs1 = 0b11111 & (code >> 15);

        //5b
        rd = 0b11111 & (code >> 7);
        //6b
        shamt = 0b111111 & (code >> 20);
        long v = ctx.register.getRegVal(rs1);
        v = v << shamt;
        ctx.register.setRegVal(rd, v);
        if (StaticRes.debugger.isOpcMonitor()) {

        }

    }
}
