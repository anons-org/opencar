package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.opcode.OpLd;
import com.far.vms.opencar.vm.opcode.OpLw;

public class Op0x03 implements IOpcodeTypes{

    @Override
    public void eval(Cpu cpu, int code, int opcode){
        int n;
        String fn3s;
        //func3
        n = 0b011 & (code >> 12);
        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
        System.out.println(fn3s);
        if (n == 0x02) {//lw
            OpLw lw = new OpLw();
            lw.setCode(code).setCtx(cpu).setFunc3(n).setOpcode(opcode).process();
            System.out.println("op lw");
        } else if (n == 0x03) {
            OpLd ld = new OpLd();
            ld.setCode(code).setCtx(cpu).setFunc3(n).setOpcode(opcode).process();
            System.out.println("op ld");
        }

    }

}
