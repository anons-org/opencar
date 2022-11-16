package com.far.demo.opencar.vm;

import com.far.demo.opencar.board.Bus;
import com.far.demo.opencar.board.Cpu;
import com.far.demo.opencar.board.Dram;
import com.far.demo.opencar.board.IBus;
import com.far.demo.opencar.debugger.Debugger;

public class StaticRes {

    public static IBus bus;

    public static Cpu[] cpus = new Cpu[10];

    public static Debugger debugger;

    public static void init(){

        debugger = new Debugger();
        //开启调试模式
        debugger.setStat( Debugger.Stat.DEBUG );
        bus = new Bus();
        cpus[0] = new Cpu();
        Dram dram = new Dram();
        dram.init();
        ((Bus)bus).setDram(dram);
    }

}
