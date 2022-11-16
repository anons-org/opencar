package com.far.demo.opencar.vm.opcode;

import com.far.demo.opencar.board.Cpu;

public class OpAddw {

    //操作码
    private int opcode;
    //code
    private int code;

    //源寄存器
    private int rs1;

    private int rs2;

    private int rd;

    //func4
    private int func3;

    private Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public OpAddw setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }

    public int getCode() {
        return code;
    }


    public int getFunc3() {
        return func3;
    }

    public OpAddw setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }

    public OpAddw setCtx(Cpu ctx) {
        this.ctx = ctx;
        return this;
    }

    public OpAddw setCode(int code) {
        this.code = code;
        return this;
    }

    public void process() {

        rd = 0b11111 & (code >> 7);
        rs1 = 0b11111 & (code >> 15);
        rs2 = 0b11111 & (code >> 20);

        long v1 = ctx.register.getValOfRid(rs1);
        long v2 = ctx.register.getValOfRid(rs2);

        //保留低32位 高位全部清零
        //如果遇到负数，JAVA类型系统会自动把64位的高32位自动补1
        v1 = 0xffffffff & v1;
        v2 = 0xffffffff & v2;


        //先加 确定符号
        //高32b 0xf3baccc4
        //低32b 0xffffffff
        v1 = v1 + v2;
        /*

         */
//        if (v1 < 0) {//带符号
//            //保留低32的同时
//            v1 = 0xffffffff00000000L | v1;
//        }
//
//

        //long v =  0xffffffffL | v1  ;

//

        ctx.register.setValOfRid( rd, v1 );
//
//        if (Debugger.Stat.DEBUG == StaticRes.debugger.getStat()) {
//            String info = String.format("addw : write reg val %x",v );
//            System.out.println(info);
//        }

    }
}
