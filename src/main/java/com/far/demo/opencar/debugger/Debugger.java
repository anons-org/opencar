package com.far.demo.opencar.debugger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * @author mike/Fang.J
 * @description: 本来想晚点写调试器的，没法，因为太多的寄存器和内存值需要查看和监视
 * 只能先写一部分了
 * @return:
 * @date: 2022/11/2 11:36
 */
public class Debugger {

    public static class Stat {
        //没有开启调试
        public static short NONE = 0x00;
        //调试态
        public static short DEBUG = 0x01;
        //单步执行态
        public static short SSTEP = 0x05;
    }

    //状态
    private short stat;

    //断点所在的行 有哪些？
    List<Integer> breakLines;


    private String[] cmdsDesc = new String[]{
            "next \t\t'single step to next line",
            "disable \t'set debuger to stat=none",
    };


    public Debugger() {
        stat = Stat.NONE;
        breakLines = new ArrayList<>();
    }


    public short getStat() {
        return stat;
    }

    public void setStat(short stat) {
        this.stat = stat;
    }

    //设置断点
    public void setBreak(int line) {
        breakLines.add(line);
    }


    //单步执行
    public void singleStep() {
        //?思路？
    }

    /**
     * @param
     * @description: 代码实现简单，后期优化..
     * @return: void
     * @author mike/Fang.J
     * @date: 2022/11/2 15:40
     */
    public void monitor() {
        String cmd = "";
        if (stat == Stat.DEBUG) {
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String[] cmds = scanner.nextLine().split(" ");
                cmd = cmds[0];
                if ("next".equals(cmd)) {
                    //跳出循环 执行下一段代码
                    break;
                } else if ("delete".equals(cmd)) {//清除所有断点

                } else if ("disable".equals(cmd)) {
                    stat = Stat.NONE;
                    break;
                }  else if ("info".equals(cmd)) {
                    if ("all-reg".equals(cmds[1])) {//显示所有寄存器的信息
                        Debug.printRegister(1);
                    }
                } else if ("x".equals(cmds[0])) {// x /nfu <addr>
                    String fmt = cmds[1];
                    long addr = Long.decode(cmds[2]);
                    Debug.printMemoryVal(addr);
                }else if("h".equals(cmd)){
                    Arrays.stream(this.cmdsDesc).forEach(e->{
                        System.out.println(e);
                    });
                }
            }
        }
    }


}
