package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.inst.OpAddi;
import com.far.vms.opencar.vm.inst.OpSlli;

public class Op0x13 implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b011 & (code >> 12);
        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
        System.out.println(fn3s);
        if (n == 0x0) {
            OpAddi opAddi = new OpAddi();
            opAddi.setCtx(cpu).setCode(code).process();
            System.out.println("op addi");
        } else if (n == 0x1) {//slli 左移多少位
            OpSlli opSlli = new OpSlli();
            opSlli.setCtx(cpu).setCode(code).process();
            System.out.println("op slli");
        }
    }

}
