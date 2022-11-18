package com.far.vms.opencar.vm.inst;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.vm.StaticRes;
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
    private int rd;

    //func4
    private int func3;
    //立即数 11:0
    private int imm;

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

    public OpBne setCtx(Cpu ctx){
        this.ctx = ctx;
        return this;
    }

    public OpBne setCode(int code) {
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
        rd = 0b11111 & (code >> 7);
        func3 = 0b111 & (code >> 12);
        //保留低12位
        imm = 0b111_111_111_111 & (code >> 20);

        //取源寄存器的值
        long mAddr = ctx.register.getValOfRid( rs1 );
        //内存地址
        mAddr = mAddr + imm;
        int memVal = StaticRes.bus.loadDw(mAddr);

        ctx.register.setValOfRid( rd,memVal );

        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {

        }

    }
}
