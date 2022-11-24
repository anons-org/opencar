package com.far.vms.opencar.instruct.inst;

import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.instruct.StaticRes;

public class OpJal extends OpBase<OpJal> {


    //源寄存器
    private int rs1;

    private int imm;
    private int rd;

    //func4
    private int func3;


    public int getFunc3() {
        return func3;
    }

    public OpJal setFunc3(int func3) {
        this.func3 = func3;
        return this;
    }


    public void process() {
        //目标寄存器
        rd = 0b11111 & (code >> 7);
        //20b 带符号
        /*
            1、右移21位拿到20b的低10b,然后高22位清零
            2、右移20位拿到20b的低11b的的最高位(1b) ,然后高31位清零后左移11b再和 第一步取到10b 做或操作 得到低20b中的低11b
            3、右移12b拿到20b的8b,高位清零,左移12b留出11b的空间存储上一步的11b
            4、code最高1b是符号位,code的低31b清零


         */
        int tmp;
        //处理低10b
        imm = 0b1111_1111_11 & (code >> 21);
        //处理第11b
        tmp = 0b1 & (code >> 20);
        //此处要理解成,留下低10b的空间 放置前一步的10b数据
        tmp = tmp << 10;
        imm = tmp | imm;
        tmp = 0;
        tmp = code >> 12;
        tmp = 0xff & tmp;
        tmp = tmp << 11;
        imm = tmp | imm;
        imm = ((code >> 31) << 31) | imm;
        imm = imm << 1;
        //存储当前PC
        long curPc = ctx.getPC();
        //当前PC+imm
        long targetAddr = curPc + imm;
        //设置跳转地址
        ctx.setPC(targetAddr);
        //存储跳转指令之后的地址,因为jal 常被用做调用子程序，子程序返回时，需要从
        //jal指令之后的指令开始执行
        ctx.register.setRegVal(rd, curPc + 4);
        if (StaticRes.debugger.isOpcMonitor()) {
            String s = String.format("jal target addr to %x", targetAddr);
            System.out.println(s);
        }

    }
}
