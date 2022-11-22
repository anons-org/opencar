package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.inst.OpJal;


/*
 * @description:
 * @author mike/Fang.J
 * @data 2022/11/17
*/
public class Op0x6F implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);

        OpJal opJal = new OpJal();
        opJal.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
        System.out.println("op jal");
    }

}
