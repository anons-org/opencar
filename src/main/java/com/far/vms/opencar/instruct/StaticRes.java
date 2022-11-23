package com.far.vms.opencar.instruct;

import com.far.vms.opencar.board.Bus;
import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.board.Dram;
import com.far.vms.opencar.board.IBus;

import com.far.vms.opencar.board.iml.Uart0;
import com.far.vms.opencar.debugger.Debugger;

/*
 * @description:
 *
 * 对于资源，因为存在并发问题，先统一管理
 * 后续再调整
 *
 * 本质上这其实就是一个开发板了...
 *
 * @author mike/Fang.J
 * @data 2022/11/18
 */
public class StaticRes {

    //内核被加载到的地址
    public static int krStart = 0x80000;

    public static IBus bus;

    public static Cpu[] cpus = new Cpu[10];


    public static Uart0 uart0;

    public static Debugger debugger;

    static {

        debugger = new Debugger();
        //开启调试模式
        debugger.setStat(Debugger.Stat.DEBUG);
        //开启指令执行监视
        debugger.setOpcMonitor(true);
        //关注ra寄存器的写
        debugger.addRegWriteOf("ra");


        //串口
        uart0 = new Uart0();
        bus = new Bus();


        cpus[0] = new Cpu();

        Dram dram = new Dram();

        dram.init();

        ((Bus) bus).setDram(dram);
        //添加需要监控内存地址的设备
        ((Bus) bus).addEdmm(uart0);

        System.out.println("init to StaticRes");

    }

    public static void init() {

    }



}
