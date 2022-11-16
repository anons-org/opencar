package com.far.demo.opencar.vm.opcode;

import com.far.demo.opencar.board.Cpu;

public class OpAddi {
    //操作码
    private int opcode;
    //code
    private int code;
    //目标寄存器
    private int rd;
    //源寄存器
    private int rs;
    //func4
    private int func3;
    //立即数
    private int imm;

    private Cpu ctx;

    public int getCode() {
        return code;
    }


    public OpAddi setCtx(Cpu ctx){
        this.ctx = ctx;
        return this;
    }

    public void setCode(int code) {
        this.code = code;
        process();
    }


    public void process(){

        //左移27位 将opcode移到最高位
        opcode = code << 27;
        this.func3 = ((code >> 12) << 29) >> 29;
        //rs 5bit

        this.rs = 0b11111 & (code >> 15) ;
        //rd 5bit
        this.rd = 0b11111 & (code >> 7) ;


        System.out.println(String.format("rs:%s,rd:%s",Integer.toBinaryString(rs),Integer.toBinaryString(rd)));

        //去掉右边20位数据
        imm = (code >> 20) << 20;
        //立即数移到最低位
        long immd = imm >> 20;
        long rsVal = ctx.register.getValOfRid( rs);
        immd = rsVal + immd;
        //写入rd 64bit
        ctx.register.setValOfRid(rd,immd);

    }
}
