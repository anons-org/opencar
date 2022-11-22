package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.inst.OpSb;
import com.far.vms.opencar.vm.inst.OpSd;
import com.far.vms.opencar.vm.inst.OpSw;

public class Op0x23 implements IOpcodeTypes {

    @Override
    public void eval(Cpu cpu, int code, int opcode) {
        int n;
        String fn3s;
        //忽略前面28位...
        n = 0b111 & (code >> 12);

        int[] ops = cpu.getInstParser().decode(opcode, code);
        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
        System.out.println(fn3s);
        if (n == 0x3) {//sd
            OpSd opSd = new OpSd();
            opSd.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
            System.out.println("op sd");
        } else if (n == 0x2) {//sw
            n = 0b011 & (code >> 12);
            OpSw opSw = new OpSw();
            opSw.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
            System.out.println("op sw");
        } else if (n == 0x0) {
            OpSb opSb = new OpSb();
            opSb.setCtx(cpu).process(ops);
//            opSb.setCtx(cpu).setFunc3(n).setCode(code).setOpcode(opcode).process();
            System.out.println("op sb");
        }
    }

}
