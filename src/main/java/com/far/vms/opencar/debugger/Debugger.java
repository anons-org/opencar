package com.far.vms.opencar.debugger;

import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.far.net.interf.IProcessAgent;
import com.far.vms.opencar.board.Bus;
import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.server.DServer;
import com.far.vms.opencar.debugger.server.SessionManager;
import com.far.vms.opencar.instruct.StaticRes;
import com.far.vms.opencar.protocol.debug.Opportun;
import com.far.vms.opencar.protocol.debug.QuestData;
import com.far.vms.opencar.protocol.debug.QuestType;
import com.far.vms.opencar.protocol.debug.mode.QuestMemoryData;
import com.far.vms.opencar.protocol.debug.mode.QuestPcBreak;
import com.far.vms.opencar.protocol.debug.mode.QuestRegBreak;
import com.far.vms.opencar.protocol.debug.mode.QuestRegInfo;
import com.far.vms.opencar.ui.main.RightTablePanle.MemoryData;
import com.far.vms.opencar.utils.ByteUtil;
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

    //需要考虑多核的情况
    private Cpu ctx;


    //状态
    private volatile short stat;
    //是否进入单步状态
    private boolean step = false;


    //监视opcode执行
    private boolean opcMonitor = false;

    //pc断点
    private Map<Long, QuestPcBreak> pcBreaks = new HashMap<>();

    //寄存器写前断点

    private Map<Integer, QuestData> regWriteBefore = new HashMap<>();

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

    public Cpu getCpu() {
        return ctx;
    }

    public void setCpu(Cpu cpu) {
        this.ctx = cpu;
    }

    public short getStat() {
        return stat;
    }

    @Override
    public boolean simIsStep() {
        return stat == Stat.STEP;
    }

    @Override
    public void setCtx(Cpu ctx) {
        this.ctx = ctx;
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


    public QuestData getPcData() {
        QuestPcBreak questPcBreak = new QuestPcBreak();
        questPcBreak.setPc(Long.toHexString(ctx.getCurPC()));
        String data = JSONUtil.toJsonStr(questPcBreak);
        QuestData questData = new QuestData();
        questData.setData(data);
        questData.setDt(QuestType.BPC);
        return questData;
    }

    public QuestData getAllGeneralRegData() {

        QuestRegInfo generalRegInfo = new QuestRegInfo();
        generalRegInfo.setTag(QuestRegInfo.Tag.A);
        generalRegInfo.setSlaveTag("general");
        generalRegInfo.setRegInfoList(new ArrayList<>());
        //普通寄存器
        ctx.getRegister().regs.entrySet().stream().forEach(e -> {
            String rName = "";
            long rVal = 0;
            if (ctx.getRegister().getRegNames().containsKey(e.getKey())) {
                QuestRegInfo.InnerInfo regInfo = new QuestRegInfo.InnerInfo();
                rName = ctx.getRegister().getRegNames().get(e.getKey());
                rVal = e.getValue();
                regInfo.setAddr(e.getKey());
                regInfo.setName(rName);
                regInfo.setVal(rVal);
                generalRegInfo.getRegInfoList().add(regInfo);
            }
        });
        QuestData questData = new QuestData();
        questData.setData(JSONUtil.toJsonStr(generalRegInfo));
        questData.setDt(QuestType.REG);
        return questData;
    }


    public QuestData getAllCrsRegData() {

        QuestRegInfo csrRegInfo = new QuestRegInfo();
        csrRegInfo.setTag(QuestRegInfo.Tag.A);
        csrRegInfo.setSlaveTag("csr");
        csrRegInfo.setRegInfoList(new ArrayList<>());
        long[] csrReg = ctx.getRegister().getCsrRegs();
        String[] csrRegName = ctx.getRegister().getCsrRegNames();
        for (int i = 0; i < csrRegName.length; i++) {
            if (csrRegName[i] == null || "".equals(csrRegName[i])) continue;
            QuestRegInfo.InnerInfo regInfo = new QuestRegInfo.InnerInfo();
            regInfo.setAddr(i);
            regInfo.setVal(csrReg[i]);
            regInfo.setName(csrRegName[i]);
            csrRegInfo.getRegInfoLists().add(regInfo);
        }
        QuestData questData = new QuestData();
        questData.setData(JSONUtil.toJsonStr(csrRegInfo));
        questData.setDt(QuestType.REG);
        return questData;
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

            DServer.toClient(JSONUtil.toJsonStr(questData));


            QuestRegInfo generalRegInfo = new QuestRegInfo();
            generalRegInfo.setTag(QuestRegInfo.Tag.A);
            generalRegInfo.setSlaveTag("general");
            generalRegInfo.setRegInfoList(new ArrayList<>());

            //普通寄存器
            ctx.getRegister().regs.entrySet().stream().forEach(e -> {
                String rName = "";
                long rVal = 0;
                if (ctx.getRegister().getRegNames().containsKey(e.getKey())) {
                    QuestRegInfo.InnerInfo regInfo = new QuestRegInfo.InnerInfo();
                    rName = ctx.getRegister().getRegNames().get(e.getKey());
                    rVal = e.getValue();
                    regInfo.setAddr(e.getKey());
                    regInfo.setName(rName);
                    regInfo.setVal(rVal);
                    generalRegInfo.getRegInfoList().add(regInfo);
                }
            });

            questData.setData(JSONUtil.toJsonStr(generalRegInfo));
            questData.setDt(QuestType.REG);
            DServer.toClient(JSONUtil.toJsonStr(questData));


            //csr寄存器-------------------------------------------------
            QuestRegInfo csrRegInfo = new QuestRegInfo();
            csrRegInfo.setTag(QuestRegInfo.Tag.A);
            csrRegInfo.setSlaveTag("csr");
            csrRegInfo.setRegInfoList(new ArrayList<>());

            long[] csrReg = ctx.getRegister().getCsrRegs();
            String[] csrRegName = ctx.getRegister().getCsrRegNames();


            for (int i = 0; i < csrRegName.length; i++) {
                if (csrRegName[i] == null || "".equals(csrRegName[i])) continue;
                QuestRegInfo.InnerInfo regInfo = new QuestRegInfo.InnerInfo();
                regInfo.setAddr(i);
                regInfo.setVal(csrReg[i]);
                regInfo.setName(csrRegName[i]);
                csrRegInfo.getRegInfoLists().add(regInfo);
            }
            questData.setData(JSONUtil.toJsonStr(csrRegInfo));
            questData.setDt(QuestType.REG);
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

    /**
     * @param regAddr
     * @param val
     * @description: 寄存器写之前暂停
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/9
     */
    @Override
    public void simCallerRegWriteBefore(int regAddr, long val) {

        if (regWriteBefore.size() < 0 || !regWriteBefore.containsKey(regAddr)) return;
        //此处发送消息的目的，是让客户端接收到信息后更新UI的数据
        DServer.toClient(JSONUtil.toJsonStr(regWriteBefore.get(regAddr)));
        //暂停
        this.stat = Stat.PAUSE;
    }


    //添加需要监视写操作的寄存器
    public void addRegWriteOf(String rName) {
        monitorRegWrite.add(rName);
    }

    //删除监视写操作的寄存器
    public void removeRegWriteOf(String rname) {

    }

    /**
     * @param questData
     * @description: 寄存器断点信号
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/9
     */
    private void OnQuestAddRegBreak(QuestData questData) {
        QuestRegBreak questRegBreak = JSONUtil.toBean(questData.getData(), QuestRegBreak.class);
        if (Objects.isNull(questRegBreak.getInnerDataList()) || questRegBreak.getInnerDataList().size() <= 0) return;
        questRegBreak.getInnerDataList().forEach(e -> {
            if (e.getOpportun() == Opportun.W_BEFORE) {
                //写之前
                regWriteBefore.put(e.getAddr(), questData);
            } else if (e.getOpportun() == Opportun.R_BEFORE) {
                //读之前
            }
        });
    }

    /**
     * @param questData
     * @description:删除寄存器断点
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/9
     */
    private void OnQuestRemoveRegBreak(QuestData questData) {
        QuestRegBreak questRegBreak = JSONUtil.toBean(questData.getData(), QuestRegBreak.class);
        if (Objects.isNull(questRegBreak.getInnerDataList()) || questRegBreak.getInnerDataList().size() <= 0) return;
        questRegBreak.getInnerDataList().forEach(e -> {
            if (e.getOpportun() == Opportun.W_BEFORE) {
                //写之前
                regWriteBefore.remove(e.getAddr());
            } else if (e.getOpportun() == Opportun.R_BEFORE) {
                //读之前
            }
        });
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

    //读取指定内存的地址
    private void onQuestReadMemory(QuestData questData) {
        QuestMemoryData questMemoryData = JSONUtil.toBean(questData.getData(), QuestMemoryData.class);
        long addr = questMemoryData.getFindAddr();
        //从指定的地址开始读取多少个字节
        int offset = questMemoryData.getUnit();
        Map<String, MemoryData.InnerMemVal> viewTable = null;

        for (int i = 0, j =0; i < offset; i++,j++) {

            if (i % 16 == 0) {
                j=0;
                viewTable = new HashMap<>();
                questMemoryData.getMapList().add(viewTable);
                MemoryData.InnerMemVal innerMemVal = new MemoryData.InnerMemVal();
                innerMemVal.setViewVal(Long.toHexString(addr+i));
                viewTable.put("offset", innerMemVal);

                int noffset = i;
                byte mval = ((Bus) StaticRes.bus).getDram().loadByte(addr + noffset);
                MemoryData.InnerMemVal innerMemVal2 = new MemoryData.InnerMemVal();
                innerMemVal2.setVal(mval);
                innerMemVal2.setViewVal(Integer.toHexString(0xF & mval));
                viewTable.put(String.valueOf(j), innerMemVal2);
            } else {
                MemoryData.InnerMemVal innerMemVal = new MemoryData.InnerMemVal();
                int noffset = i;
                byte mval = ((Bus) StaticRes.bus).getDram().loadByte(addr + noffset);
                innerMemVal.setVal(mval);
                innerMemVal.setViewVal(Integer.toHexString(0xF & mval));
                viewTable.put(String.valueOf(j), innerMemVal);
            }
        }
        questData.setData( JSONUtil.toJsonStr(questMemoryData) );
        DServer.toClient(JSONUtil.toJsonStr(questData));
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


        while (stat == Stat.PAUSE) ;


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
            case QuestType.READ_MEMORY:
                onQuestReadMemory(quest);
                break;
            case QuestType.TEST:

                System.out.println(JSONUtil.toJsonStr(this.ctx));

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
