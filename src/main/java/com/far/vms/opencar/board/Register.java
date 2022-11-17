package com.far.vms.opencar.board;

import java.util.HashMap;
import java.util.Map;

public class Register {

    public static Map<Integer, String> regNames = new HashMap<>();


    public static class RegAddr {
        public static int
                PC = 999999999,
                SP = 0b00_010,
                S0 = 0b01_000,
                A4 = 0b01_110,
                A5 = 0b01_111,
                X0 = 0b00_000;


    }


    //所有的寄存器
    public Map<Integer, Long> regs = new HashMap<>();

    public Register() {
        //寄存器的值
        //sp 初始化为32 以为栈是向地址方向开辟的
        regs.put(0b00_010, 32L);
        //x0?
        regs.put(RegAddr.X0, 0x0L);
        regs.put(0x78000000, 0x0L);
        //s0 谢特那这个是啥？
        regs.put(0b01_000, 0x0L);
        //a4
        regs.put(RegAddr.A4, 0x0L);

        //a5
        regs.put(0b01_111, 0x0L);
        //PC
        regs.put(RegAddr.PC, 0x0L);


        //寄存器名称
        regNames.put(RegAddr.X0, "x0");
        regNames.put(RegAddr.SP, "sp");
        regNames.put(RegAddr.S0, "s0");
        regNames.put(RegAddr.A4, "a4");
        //a5
        regNames.put(RegAddr.A5, "a5");
        regNames.put(RegAddr.PC, "pc");
    }

    public long getValOfRid(int rid) {
        //取出rs寄存器的数据... 此处要改...
        long val = regs.get(rid);
        return val;
    }

    public void setValOfRid(int rid, long val) {
        //x0不能修改
        if (rid == 0) return;
        regs.put(rid, val);
        int x = 01;
    }


}
