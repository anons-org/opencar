package com.far.demo.opencar.board;


import com.far.demo.opencar.vm.BinFile;
import com.far.demo.opencar.vm.StaticRes;
import com.far.demo.opencar.vm.opcode.*;

public class Cpu {


    //所有的寄存器
    public Register register;

    private BinFile codes;
    //当前执行到的行 该字段后续还需要优化，因为执行的code需要确定是哪个文件的多少行？
    //默认第一行开始
    public int curLine = 1;

    //流水线上的刷新信号
    private short flushReq;

    private long PC;

    public Cpu() {
        register = new Register();
    }

    public BinFile getCodes() {
        return codes;
    }

    public void setCodes(BinFile codes) {
        this.codes = codes;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }


    //取指阶段
    public long ifu() {
        return bpu();
    }

    //分支预测模块 返回指令地址
    public long bpu() {
        if (flushReq == 1) {//收到流水线指令刷新信号
            //无条件跳转会刷新流水线
            PC = this.register.regs.get(Register.RegAddr.PC);
        } else {//静态预测为当前指令地址+4
            PC += 4;
        }
        return 0;
    }



    //时序电路
    public void execute() {

        try {
            while (codes.available() > 0) {
                int code = codes.readInt();
                //左移25位 将opcode移到最高位
                //再右移会带符号 将高25b全部置0
                int opcode = 0b1111111 & ((code << 25) >> 25);

                String str = String.format("parseing code 0x%s, opcode 0x%s ", Integer.toHexString(code), Integer.toHexString(opcode));
                System.out.print(str);
                String fn3s = "";
                int n;
                switch (opcode) {
                    case 0x03:
                        //func3
                        n = 0b011 & (code >> 12);
                        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
                        System.out.println(fn3s);
                        if (n == 0x02) {//lw
                            OpLw lw = new OpLw();
                            lw.setCode(code).setCtx(this).setFunc3(n).setOpcode(opcode).process();
                            System.out.println("op lw");
                        } else if (n == 0x03) {
                            OpLd ld = new OpLd();
                            ld.setCode(code).setCtx(this).setFunc3(n).setOpcode(opcode).process();
                            System.out.println("op ld");
                        }
                        break;

                    case 0x13://I型
                        //func3
                        n = 0b011 & (code >> 12);
                        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
                        System.out.println(fn3s);
                        if (n == 0x0) {
                            OpAddi opAddi = new OpAddi();
                            opAddi.setCtx(this).setCode(code);
                            System.out.println("op addi");
                        }
                        break;
                    case 0x23://r64I sd
                        //忽略前面28位...
                        n = 0b011 & (code >> 12);
                        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
                        System.out.println(fn3s);
                        if (n == 0x3) {//sd
                            OpSd opSd = new OpSd();
                            opSd.setCtx(this).setFunc3(n).setCode(code).setOpcode(opcode).process();
                            System.out.println("op sd");
                        } else if (n == 0x2) {//sw
                            n = 0b011 & (code >> 12);
                            OpSw opSw = new OpSw();
                            opSw.setCtx(this).setFunc3(n).setCode(code).setOpcode(opcode).process();
                            System.out.println("op sw");
                        }
                        break;
                    case 0x3b:
                        n = 0b011 & (code >> 12);
                        OpAddw opAddw = new OpAddw();
                        opAddw.setCtx(this).setFunc3(n).setCode(code).setOpcode(opcode).process();
                        System.out.println("op addw");
                        break;

                    case 0x67:
                        n = 0b011 & (code >> 12);
                        OpJalr opJalr = new OpJalr();
                        opJalr.setCtx(this).setFunc3(n).setCode(code).setOpcode(opcode).process();
                        System.out.println("op jalr");
                        break;

                    default:
                }

                curLine++;
                register.pc++;
                //监测断点
                StaticRes.debugger.monitor();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


}
