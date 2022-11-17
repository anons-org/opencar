package com.far.vms.opencar.debugger;

import com.far.vms.opencar.board.Register;
import com.far.vms.opencar.vm.StaticRes;

import java.util.Arrays;

/*
 * @description: TODO
 * @author mike/Fang.J
 * @data 2022/11/17
*/
public class Debug {

    //打印所有寄存器内的数据
    public static void printRegister(int id) {
        System.out.println("all Register");
        Arrays.stream(StaticRes.cpus).forEach(e -> {
            if (e != null) {
                e.register.regs.forEach((k, v) -> {
                    String s = String.format("%s=%s", Register.regNames.get(k), v);
                    System.out.println(s);
                });
            }
        });
    }

    /**
     * @param addr
     * @description:打印指定的内存地址的值
     * @return: void
     * @author mike/Fang.J
     * @date: 2022/11/1 19:28
     */
    public static void printMemoryVal(long addr) {

        long mVal = StaticRes.bus.loadDDw(addr);
        String addrs = Long.toHexString(addr);
        String s = String.format("0x%s %s", addrs, mVal);
        System.out.println(s);
    }


}
