package com.far.vms.opencar.instruct.opcodeType;

import com.far.vms.opencar.board.Cpu;

/*
 * @description:
 *
 * @author mike/Fang.J
 * @data 2022/11/19
 */
public class Op0x1B implements IOpcodeTypes {

    @Override
    public void eval(Cpu ctx, int code, int opcode) {
        int n;
        String fn3s;
        //func3
        n = 0b111 & (code >> 12);
        int[] ops = ctx.getInstParser().decode(opcode, code);
        fn3s = String.format("fn3  0x%s ", Integer.toHexString(n));
        System.out.println(fn3s);

        if (n == 0x0) {//addiw I型
            System.out.println("op addiw");
        }


    }

}
