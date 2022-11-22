package com.far.vms.opencar.vm.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.Debugger;
import com.far.vms.opencar.utils.ByteUtil;
import com.far.vms.opencar.vm.StaticRes;
import com.far.vms.opencar.vm.inst.OpLd;
import com.far.vms.opencar.vm.inst.OpLw;

public class Op0x03 implements IOpcodeTypes {

    @Override
    public void eval(Cpu ctx, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);

        int[] ops = ctx.getInstParser().decode(opcode, code);

        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
        System.out.println(fn3s);
        if (n == 0x02) {//lw
            OpLw lw = new OpLw();
            lw.setCode(code).setCtx(ctx).setFunc3(n).setOpcode(opcode).process();
            System.out.println("op lw");
        } else if (n == 0x03) {
            OpLd ld = new OpLd();
            ld.setCode(code).setCtx(ctx).setFunc3(n).setOpcode(opcode).process();
            System.out.println("op ld");
        } else if (n == 0x04) {//I 型
            int rd, rs1, imm;
            rd = ops[1];
            rs1 = ops[3];
            imm = ops[4];

            long loadAdr = ctx.register.getRegVal(rs1) + imm;
            System.out.println(String.format("lbu from mem addr 0x%x", loadAdr));
            byte v = StaticRes.bus.loadByte(loadAdr);
            long tv = v >>> 56;
            ctx.register.setRegVal(rd, tv);
            System.out.println("op lbu......");
        }
    }


}
