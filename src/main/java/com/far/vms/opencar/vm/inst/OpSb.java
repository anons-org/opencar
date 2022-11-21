package com.far.vms.opencar.vm.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
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
    //立即数 4:0
    private int imm1;
    //立即数 11:5
    private int imm2;

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

    public OpSb setCtx(Cpu ctx){
        this.ctx = ctx;
        return this;
    }

    public OpSb setCode(int code) {
        this.code = code;
        return this;
    }

    public void process(){
        /**
         * rs1 5bit
           1、移除低15位
           2、左移后高位的5位是寄存器编号
           3、高5位移动低5位
           4、保留低5位其余全部置零
         */
        rs1 = 0b11111 & (code >> 15);

        rs2 = 0b11111 & (code >> 20);

        //1、移除低7为的opcode
        //2、保留低5位的数据，高位全部设置0
        // 0b0000...11111
        imm1 = 0b11111 & (code >> 7);
        /**
            1、右移25位 保留最高的7位
            2、保留低7位数据 其余为全部设0
            3、右移5位，让低5位全为0
            0b...111111100000
         */
        imm2 = (0b1111111 & (code >> 25)) << 5;
        //最终立即数
        int immRes = imm2 | imm1;
        //立即数加上寄存器中的基地址 得到内存地址

        long mAddr = ctx.register.getRegVal( rs1 );
        //内存地址
        mAddr = mAddr + immRes;

        //rs2的数据
        Long val = ctx.register.getRegVal( rs2 );
        /*
            1、高32b置0
         */
        val = 0xffffffff & val;

        StaticRes.bus.storeByte(0x10000000L,1);
        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {
            String info = String.format("sw : write mem 0x%x",mAddr);
            System.out.println(info);
        }

    }
}
