package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.opcode.OpAddi;
import com.far.vms.opencar.vm.opcode.OpAddw;

public class Op0x3b implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b011 & (code >> 12);
        OpAddw opAddw = new OpAddw();
        opAddw.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
        System.out.println("op addw");
    }

}
