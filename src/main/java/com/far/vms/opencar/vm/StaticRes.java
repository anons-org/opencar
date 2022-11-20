package com.far.vms.opencar.vm;

import com.far.vms.opencar.board.Bus;
import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.board.Dram;
import com.far.vms.opencar.board.IBus;
import com.far.vms.opencar.debugger.Debug;
import com.far.vms.opencar.debugger.Debugger;

/*
 * @description:
 *
 * 对于资源，因为存在并发问题，先统一管理
 * 后续再调整
 *
 * @author mike/Fang.J
 * @data 2022/11/18
 */
public class StaticRes {

    //内核被加载到的地址
    public static int krStart = 0x80000;

    public static IBus bus;

    public static Cpu[] cpus = new Cpu[10];

    public static Debugger debugger;

    static {
        debugger = new Debugger();

        //开启调试模式
        debugger.setStat(Debugger.Stat.DEBUG);
        //开启指令执行监视
        debugger.setOpcMonitor(Debugger.Stat.DEBUG);

        bus = new Bus();
        cpus[0] = new Cpu();
        Dram dram = new Dram();
        dram.init();
        ((Bus) bus).setDram(dram);

        System.out.println("init to StaticRes");

    }

    public static void init() {

    }


//    public static void init() {
//
//        debugger = new Debugger();
//        //开启调试模式
//        debugger.setStat(Debugger.Stat.DEBUG);
//        bus = new Bus();
//        cpus[0] = new Cpu();
//        Dram dram = new Dram();
//        dram.init();
//        ((Bus) bus).setDram(dram);
//    }

}
