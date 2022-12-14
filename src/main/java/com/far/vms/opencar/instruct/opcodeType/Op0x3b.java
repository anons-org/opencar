package com.far.vms.opencar.instruct.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.instruct.inst.OpAddw;

public class Op0x3b implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);
        OpAddw opAddw = new OpAddw();
        opAddw.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
        System.out.println("op addw");
    }

}
