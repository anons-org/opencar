package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;

import com.far.vms.opencar.vm.inst.OpLui;

public class Op0x37 implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);
        OpLui opLui = new OpLui();
        opLui.setCtx(cpu).setCode(code).setOpcode(opcode).process();
        System.out.println("op opLui");
    }

}
