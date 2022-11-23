package com.far.vms.opencar.board;

import com.far.vms.opencar.instruct.StaticRes;

import java.util.HashMap;
import java.util.Map;

public class Register {

    //当前寄存器所属的cpu；
    private Cpu ctx;

    private Map<Integer, String> regNames = new HashMap<>();


    public void setCtx(Cpu ctx) {
        this.ctx = ctx;
    }

    public static class RegAddr {
        public static int
                PC = 999999999,
        //用于返回的寄存器
        RA = 0b00_001,
                SP = 0b00_010,
        //x0
        T0 = 0b00_101,
                S0 = 0b01_000,
                A4 = 0b01_110,
                A5 = 0b01_111,
                X0 = 0b00_000;

    }

    public static class CsrRegAddr {
        public static int
                mstatus = 0x300,//机器状态
                mhartid = 0xF14;//0xF14;

    }


    //所有的寄存器
    public Map<Integer, Long> regs = new HashMap<>();
    //控制寄存器
    public long[] csrRegs = new long[4096];

    public String[] csrRegNames = new String[4096];


    public Register() {
        //寄存器的值

        regs.put(RegAddr.RA, 0L);

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
        //t0|x5
        regs.put(RegAddr.T0, 0x0L);

        //PC
        regs.put(RegAddr.PC, 0x0L);


        //-------------
        //默认1
        csrRegs[CsrRegAddr.mhartid] = 0x00L;

        //寄存器名称
        regNames.put(RegAddr.X0, "x0|zero");
        regNames.put(RegAddr.SP, "sp");
        regNames.put(RegAddr.S0, "s0");
        regNames.put(RegAddr.A4, "a4");
        //a5
        regNames.put(RegAddr.A5, "a5");
        regNames.put(RegAddr.PC, "pc");

        regNames.put(RegAddr.T0, "t0|x5");

        regNames.put(RegAddr.RA, "ra");

        //----------
        csrRegNames[CsrRegAddr.mhartid] = "mhartid";

    }

//    public long getValOfRid(int rid) {
//        //取出rs寄存器的数据... 此处要改...
//        long val = regs.get(rid);
//        return val;
//    }

    public long getRegVal(int rid) {

        if (!regs.containsKey(rid)) {
            System.out.println("The register is not initialized");
            return 0;
        }

        long val = regs.get(rid);
        return val;
    }

    public void setRegVal(int rid, long val) {
        //x0不能修改
        if (rid == 0) return;
        StaticRes.debugger.onWriteReg(ctx, rid, val);
        regs.put(rid, val);
        int x = 01;
    }


//    public void setValOfRid(int rid, long val) {
//        //x0不能修改
//        if (rid == 0) return;
//        regs.put(rid, val);
//        int x = 01;
//    }

    //读取csr寄存器
    public long getCSRVal(int rid) {
        long val = csrRegs[rid];
        return val;
    }

    //设置控制寄存器
    public void setCSRVal(int raddr, long val) {
        csrRegs[raddr] = val;
    }


    public Map<Integer, String> getRegNames() {
        return regNames;
    }

    public void setRegNames(Map<Integer, String> regNames) {
        this.regNames = regNames;
    }
}
