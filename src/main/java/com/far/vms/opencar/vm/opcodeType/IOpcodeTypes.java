package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;

public interface IOpcodeTypes {

    public void eval(Cpu cpu, int code, int opcode);
}
