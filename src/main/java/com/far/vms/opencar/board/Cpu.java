package com.far.vms.opencar.board;


import com.far.vms.opencar.vm.BinFile;
import com.far.vms.opencar.vm.StaticRes;

import com.far.vms.opencar.vm.opcode.*;
import com.far.vms.opencar.vm.opcodeType.*;

public class Cpu extends CpuBase {


    public IOpcodeTypes[] opct = new IOpcodeTypes[256];

    public Cpu() {
        register = new Register();
        opct[0x03] = new Op0x03();
        opct[0x13] = new Op0x13();
        opct[0x23] = new Op0x23();
        opct[0x3b] = new Op0x3b();
        opct[0x67] = new Op0x67();

    }

    //时序电路
    public void execute() {
        PC = predict = 0x80000;
        try {
            while (true) {
                int code = getCode();
                //左移25位 将opcode移到最高位
                //再右移会带符号 将高25b全部置0
                int opcode = 0b1111111 & ((code << 25) >> 25);

                String str = String.format("parseing code 0x%s, opcode 0x%s ", Integer.toHexString(code), Integer.toHexString(opcode));
                System.out.print(str);

                if( opct[opcode]==null ) continue;
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
