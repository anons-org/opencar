package com.far.vms.opencar.board;


import com.far.vms.opencar.vm.StaticRes;

import com.far.vms.opencar.vm.opcodeType.*;

public class Cpu extends CpuBase {


    public IOpcodeTypes[] opct = new IOpcodeTypes[256];

    public Cpu() {
        register = new Register();
        opct[0x03] = new Op0x03();
        opct[0x13] = new Op0x13();
        opct[0x17] = new Op0x17();
        opct[0x23] = new Op0x23();
        opct[0x3b] = new Op0x3b();
        //bne
        opct[0x63] = new Op0x63();
        opct[0x67] = new Op0x67();
        opct[0x6F] = new Op0x6F();
        opct[0x73] = new Op0x73();

    }

    //时序电路
    public void execute() {

        this.setPC(predict = StaticRes.krStart);
        try {
            while (true) {
                int code = getCode();
                int opcode = getOpCode();

                String str = String.format("parseing code 0x%s, opcode 0x%s ", Integer.toHexString(code), Integer.toHexString(opcode));
                System.out.print(str);

                if (opct[opcode] == null) continue;
                opct[opcode].eval(this, code, opcode);
                curLine++;
                //监测断点
                StaticRes.debugger.monitor();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }


    }


}
