package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.inst.OpCsrrs;
import com.far.vms.opencar.vm.inst.OpJalr;

/*
 * @description: csrrs
 * @author mike/Fang.J
 * @data 2022/11/17
*/
public class Op0x73 implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b011 & (code >> 12);

        if(n==0x2){
            OpCsrrs opCsrrs = new OpCsrrs();
            opCsrrs.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
            System.out.println("op csrrs");
        }


    }

}
