package com.far.vms.opencar.instruct.opcodeType;

import com.far.vms.opencar.board.Cpu;

public interface IOpcodeTypes {

    public void eval(Cpu cpu, int code, int opcode);
}
