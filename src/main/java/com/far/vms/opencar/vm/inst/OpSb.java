package com.far.vms.opencar.vm.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.utils.ByteUtil;
import com.far.vms.opencar.vm.StaticRes;

public class OpSb {

    //操作码
    private int opcode;
    //code
    private int code;

    //源寄存器
    private int rs1;

    private int rs2;
    //func4
    private int func3;

    private int ofst1;

    private int ofst2;

    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpSb setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpSb setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpSb setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpSb setCode(int code) {
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

        //5b imm
        ofst1 = 0b11111 & (code >> 7);
        //7b imm
        ofst2 = 0b1111111 & (code >> 25);

        ofst2 = (ofst2 << 5) | ofst1;

        //只保留低位一个字节
        byte[] vals = ByteUtil.longToBytes(ctx.register.getRegVal(rs2));

        byte rs2Val = vals[Long.BYTES - 1];

        long tAddr = ctx.register.getRegVal(rs1) + ofst2;

        StaticRes.bus.storeByte(tAddr, rs2Val);

        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {
            String info = String.format("sb : write mem 0x%x", tAddr);
            System.out.println(info);
        }

    }
}
