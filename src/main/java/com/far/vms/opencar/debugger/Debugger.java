package com.far.vms.opencar.debugger;

import com.far.vms.opencar.board.Cpu;

import java.util.*;




/*
 * @description: 本来想晚点写调试器的，没法，因为太多的寄存器和内存值需要查看和监视
 * @author mike/Fang.J
 * @data 2022/11/17
 */

public class Debugger implements IDebuger {


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

    //监视opcode执行
    private boolean opcMonitor = false;


    //pc中断

    private List<Integer> pcBreaks = new ArrayList<>();


    //断点所在的行 有哪些？ 记录 调式器发送过来的断点
    List<Integer> breakLines;


    //寄存器写断点，用于监视寄存器写入
    List<String> monitorRegWrite = new ArrayList<>();


    //检查断点
    public boolean chkPcBreak(int pc) {

        Optional<Integer> chk = pcBreaks.stream().filter(e -> {
            return pc == e;
        }).findFirst();
        return chk.isPresent();
    }


    private String[] cmdsDesc = new String[]{
            "next \t\t\t'single step to next line",
            "disable \t\t'set debuger to stat=none",
            "b pc 0xcccc \t set pc break",
    };


    public Debugger() {
        this.setStat(Stat.NONE);
        breakLines = new ArrayList<>();



    }


    public short getStat() {
        return stat;
    }

    public void setStat(short stat) {
        this.stat = stat;
    }


    public boolean isOpcMonitor() {
        return opcMonitor;
    }

    public void setOpcMonitor(boolean opcMonitor) {
        this.opcMonitor = opcMonitor;
    }

    //设置断点
    public void setBreak(int line) {
        breakLines.add(line);
    }


    public void addBreak(int line) {
        breakLines.add(line);
    }


    public void removeBreak(int line) {
        breakLines.remove(line);
    }


    //单步执行
    public void singleStep() {
        //?思路？
    }


    //添加需要监视写操作的寄存器
    public void addRegWriteOf(String rName) {
        monitorRegWrite.add(rName);
    }

    //删除监视写操作的寄存器
    public void removeRegWriteOf(String rname) {

    }


    @Override
    public void onWriteReg(Cpu ctx, int rid, long val) {

        //找出name
        String rname = ctx.getRegister().getRegNames().get(rid);

        if (monitorRegWrite.contains(rname)) {
            int x = 11;
            System.out.println(x + "有修改");
        }

        int xxx = 1;
    }


    public void onDbgCmd(String startInf) {
        String cmd = "";

        if (startInf.equals("")) {
            System.out.println("onDbgCmd is triggered to enter debug mode! Enter 'h to view all debug instructions");
        } else {
            System.out.println(startInf + ", Enter 'h to view all debug instructions");
        }


        while (stat == Stat.DEBUG){

        }




//        Scanner scanner = new Scanner(System.in);
//        while (scanner.hasNext()) {
//            String[] cmds = scanner.nextLine().split(" ");
//            cmd = cmds[0];
//            if ("next".equals(cmd)) {
//                //跳出循环 执行下一段代码
//                break;
//            } else if ("delete".equals(cmd)) {//清除所有断点
//
//            } else if ("disable".equals(cmd)) {
//                stat = Stat.NONE;
//                break;
//            } else if ("info".equals(cmd)) {
//                if ("all-reg".equals(cmds[1])) {//显示所有寄存器的信息
//                    Debug.printRegister(1);
//                }
//            } else if ("x".equals(cmds[0])) {// x /nfu <addr>
//                String fmt = cmds[1];
//                long addr = Long.decode(cmds[2]);
//                Debug.printMemoryVal(addr);
//            } else if ("h".equals(cmd)) {
//                Arrays.stream(this.cmdsDesc).forEach(e -> {
//                    System.out.println(e);
//                });
//            } else if ("b".equals(cmd)) {
//                if ("pc".equals(cmds[1])) {
//
//                }
//            }
//        }
//


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

            onDbgCmd("Start pause to enter debugging mode");

//            System.out.println("调试模式开启!");
//            Scanner scanner = new Scanner(System.in);
//            while (scanner.hasNext()) {
//                String[] cmds = scanner.nextLine().split(" ");
//                cmd = cmds[0];
//                if ("next".equals(cmd)) {
//                    //跳出循环 执行下一段代码
//                    break;
//                } else if ("delete".equals(cmd)) {//清除所有断点
//
//                } else if ("disable".equals(cmd)) {
//                    stat = Stat.NONE;
//                    break;
//                } else if ("info".equals(cmd)) {
//                    if ("all-reg".equals(cmds[1])) {//显示所有寄存器的信息
//                        Debug.printRegister(1);
//                    }
//                } else if ("x".equals(cmds[0])) {// x /nfu <addr>
//                    String fmt = cmds[1];
//                    long addr = Long.decode(cmds[2]);
//                    Debug.printMemoryVal(addr);
//                } else if ("h".equals(cmd)) {
//                    Arrays.stream(this.cmdsDesc).forEach(e -> {
//                        System.out.println(e);
//                    });
//                } else if ("b".equals(cmd)) {
//                    if ("pc".equals(cmds[1])) {
//
//                    }
//                }
//            }
        }
    }


}
