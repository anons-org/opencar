package com.far.vms.opencar.debugger;

import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.far.net.interf.IProcessAgent;
import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.server.DServer;
import com.far.vms.opencar.debugger.server.SessionManager;
import com.far.vms.opencar.protocol.debug.QuestData;
import com.far.vms.opencar.protocol.debug.QuestType;
import com.far.vms.opencar.protocol.debug.mode.QuestPcBreak;
import com.far.vms.opencar.utils.exception.FarException;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;

import java.util.*;




/*
 * @description: 本来想晚点写调试器的，没法，因为太多的寄存器和内存值需要查看和监视
 *
 * IDebuger 主要交给模拟器调用
 *
 * @author mike/Fang.J
 * @data 2022/11/17
 */

public class Debugger implements IDebuger, IDebugQuest {


    public static class Stat {
        //没有开启调试
        public static short NONE = 0x00;
        //调试态
        public static short DEBUG = 0x01;
        //关闭调试状态
        public static short SLEEP = 0x02;

        //单步执行态 0 否 1 是
        public static short STEP = 0x03;

        public static short PAUSE = 0x04;

    }

    //状态
    private volatile short stat;
    //是否进入单步状态
    private boolean step = false;


    //监视opcode执行
    private boolean opcMonitor = false;

    //pc断点
    private Map<Long, QuestPcBreak> pcBreaks = new HashMap<>();

    //断点所在的行 有哪些？ 记录 调式器发送过来的断点
    List<Integer> breakLines;


    //寄存器写断点，用于监视寄存器写入
    List<String> monitorRegWrite = new ArrayList<>();


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

    @Override
    public boolean simIsStep() {
        return stat == Stat.STEP;
    }


    public void setStep(boolean step) {
        this.step = step;
    }

    @Override
    public void setStat(short stat) {
        this.stat = stat;
    }

    @Override
    public boolean isOpcMonitor() {
        return opcMonitor;
    }

    @Override
    public void setOpcMonitor(boolean opcMonitor) {
        this.opcMonitor = opcMonitor;
    }

    @Override
    public boolean simIsChkPcBreak() {
        return pcBreaks.size() > 0;
    }

    /**
     * @param ctx
     * @param pc
     * @description: 停止到当前的PC下
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/4
     */
    @Override
    public void simInformPcBreak(final Cpu ctx, final long pc) {
        //System.out.println(ctx.getRegister().regs);
        QuestPcBreak questPcBreak = null;
        if (pcBreaks.containsKey(pc)) {
            stat = Stat.PAUSE;
            questPcBreak = pcBreaks.get(pc);
            String data = JSONUtil.toJsonStr(questPcBreak);
            QuestData questData = new QuestData();
            questData.setData(data);
            questData.setDt(QuestType.BPC);
            System.out.println(  JSONUtil.toJsonStr(ctx.getRegister()) );
            DServer.toClient(JSONUtil.toJsonStr(questData));
        } else {
//            questPcBreak = new QuestPcBreak();
//            questPcBreak.setPc(Long.toHexString(pc));
        }



    }

    /*
     * @description:
     * CPU发送单步后，添加PC断点，调试器客户端收到PC断点时，判断有没有line数据 没有的话就用PC号作为索引 在编辑器中进行查找和标记
     * @author mike/Fang.J
     * @data 2022/12/5
     */
    @Override
    public void simInformStep(Cpu ctx, long pc) {


        if (!pcBreaks.containsKey(pc)) {
            QuestPcBreak questPcBreak = new QuestPcBreak();
            questPcBreak.setPc(Long.toHexString(pc));
            String data = JSONUtil.toJsonStr(questPcBreak);
            QuestData questData = new QuestData();
            questData.setData(data);
            questData.setDt(QuestType.BPC);
            onQuestaddPcBreak(questData);
        }

        this.setStep(false);
        this.stat = Stat.PAUSE;
//        DServer.toClient(JSONUtil.toJsonStr(questData));

    }


    //添加需要监视写操作的寄存器
    public void addRegWriteOf(String rName) {
        monitorRegWrite.add(rName);
    }

    //删除监视写操作的寄存器
    public void removeRegWriteOf(String rname) {

    }

    //添加PC断点
    private void onQuestaddPcBreak(QuestData questData) {
        try {
            QuestPcBreak qpc = JSONUtil.toBean(questData.getData(), QuestPcBreak.class);
            pcBreaks.put(Long.valueOf(HexUtil.hexToLong(qpc.getPc())), qpc);
        } catch (Exception e) {
            throw new FarException(FarException.Code.RUNNABLE, e.getMessage(), e);
        }
    }

    //删除PC断点
    private void onQuestRemovePcBreak(QuestPcBreak questPcBreak) {
        long pcl = Long.valueOf(HexUtil.hexToLong(questPcBreak.getPc()));
        pcBreaks.remove(pcl);
    }

    /**
     * @param
     * @description: 对于以后支持多核的情况下来说, onQuestStep触发时，并不知道，当前是哪个CPU在执行
     * 也不知道具体执行到哪个PC点...
     * 所以 设计为，增加单步调试状态，等具体CPU执行时，判断状态，将下一条指令
     * 添加到PC断点列表中
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/4
     */
    private void onQuestStep() {
        System.out.println("当前线程-->" + Thread.currentThread().getName());
        this.stat = Stat.STEP;
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

//
//        if (stat == Stat.DEBUG) {
//            System.out.println("当前线程-->" + Thread.currentThread().getName());
//        }


        while (stat == Stat.PAUSE) ;


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
    @Override
    public void monitor() {
        String cmd = "";
        if (stat == Stat.DEBUG) {
            System.out.println("当前线程-->" + Thread.currentThread().getName());
        }

        while (stat == Stat.PAUSE) ;


//        if (stat == Stat.DEBUG) {

        //onDbgCmd("Start pause to enter debugging mode");

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
//        }
    }

    /**
     * @param msg
     * @param sessionAgent
     * @description: 走了一些路，终于到这里!
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/4
     */
    @Override
    public void onDebugRequest(String msg, IProcessAgent<SessionManager.SessionAgent> sessionAgent) {
        QuestData quest = JSONUtil.toBean(msg, QuestData.class);
        switch (quest.getDt()) {
            case QuestType.BPC:
                onQuestaddPcBreak(quest);
                break;
            case QuestType.RBPC:
                QuestPcBreak qpc = JSONUtil.toBean(quest.getData(), QuestPcBreak.class);
                onQuestRemovePcBreak(qpc);
                break;
            case QuestType.STEP:
                onQuestStep();
                break;
            default:

        }


        System.out.println("调试信息-> " + msg);
    }

    @Override
    public IDebuger getDebugger() {
        return this;
    }


}
