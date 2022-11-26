package com.far.vms.opencar.instruct.opcodeType;

import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.instruct.inst.OpBne;

/*
 * @description: bne
 * @author mike/Fang.J
 * @data 2022/11/17
 */
public class Op0x63 implements IOpcodeTypes {

    @Override
    public void eval(Cpu ctx, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);

        int[] ops = ctx.getInstParser().decode(opcode, code);

        if (n == 0x1) {//bne
            int rs1, rs2, imm1;
            imm1 = ops[1];
            rs1 = ops[3];
            rs2 = ops[4];
            if (ctx.getRegister().getRegVal(rs1) != ctx.getRegister().getRegVal(rs2)) {
                ctx.setPC(ctx.getPC() + imm1);
            }
            System.out.println("op bne");
        } else if (n == 0x0) {//beq
            int rs1, rs2, imm1;
            imm1 = ops[1];
            rs1 = ops[3];
            rs2 = ops[4];
            if (ctx.getRegister().getRegVal(rs1) == ctx.getRegister().getRegVal(rs2)) {
                long pc = ctx.getPC() + imm1;
                ctx.setPC(pc);
            }
            System.out.println("op beq");
        }


    }

}
