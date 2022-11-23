package com.far.vms.opencar.instruct.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.instruct.inst.OpAuipc;

/*
 * @description:
 * auipc
 * @author mike/Fang.J
 * @data 2022/11/19
*/
public class Op0x17 implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);
        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
        System.out.println(fn3s);
        OpAuipc opAuipc = new OpAuipc();
        opAuipc.setCtx(cpu).setCode(code).process();
        System.out.println("op addi");
    }

}
