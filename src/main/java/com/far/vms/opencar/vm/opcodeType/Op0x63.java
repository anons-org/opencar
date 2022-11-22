package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.inst.OpBne;
import com.far.vms.opencar.vm.inst.OpJalr;

/*
 * @description: bne
 * @author mike/Fang.J
 * @data 2022/11/17
*/
public class Op0x63 implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);

        if(n==0x1){//bne
            OpBne opBne = new OpBne();
            opBne.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
            System.out.println("op opBne");
        }


    }

}
