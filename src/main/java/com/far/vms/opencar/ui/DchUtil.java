package com.far.vms.opencar.ui;

import cn.hutool.json.JSONUtil;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.protocol.debug.QuestData;
import com.far.vms.opencar.protocol.debug.mode.QuestStep;
import com.far.vms.opencar.ui.net.DbgClient;
import com.far.vms.opencar.protocol.debug.mode.QuestPcBreak;
import com.far.vms.opencar.protocol.debug.QuestType;

/*
 * @description: 调试器工具
 * @author mike/Fang.J
 * @data 2022/12/3
 */
public class DchUtil {


    /**
     * 调试器
     *
     * @data 2022/12/3
     */
    DbgClient dbgClient;


    public DbgClient getDbgClient() {
        return dbgClient;
    }

    public void setDbgClient(DbgClient dbgClient) {
        this.dbgClient = dbgClient;
    }


    public static DchUtil create(OpenCarWindos ctx) {
        DchUtil dchUtil = new DchUtil();
        dchUtil.setDbgClient(new DbgClient());
        dchUtil.getDbgClient().setOnMessageCall((s) -> {
            System.out.println("调试服务端的信息 ->" + s);
            QuestData questData = JSONUtil.toBean(s, QuestData.class);
            if (questData.getDt() == QuestType.BPC) {

                ctx.getDebugBtns().activeBtnStepIn();
                QuestPcBreak questPcBreak = JSONUtil.toBean(questData.getData(), QuestPcBreak.class);
                if (questPcBreak.getLine() > 0) {
                    ctx.getTvCodeEditor().getSelectionModel().select(questPcBreak.getLine() - 1);
                } else {
                    short line = ctx.getCodeLineRef().get(questPcBreak.getPc());
                    line = (short) (line - 1);
                    ctx.getTvCodeEditor().getSelectionModel().select(line);
                    //调整显示的位置
                    ctx.codeEditorScrollTo(line);
                }
            }
            return true;
        });
        dchUtil.getDbgClient().start();
        return dchUtil;
    }

    /**
     * @description: 单步调试 遇到函数 进入函数
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/4
     */
    public void step() {
        String dpt = JSONUtil.toJsonStr(QuestData.create().setDt(QuestType.STEP).setData(""));
        dbgClient.sendMessage(dpt);
    }

    public void addPcBreakLine(String pc, int line) {
        QuestPcBreak dgbptPcBreak = new QuestPcBreak();
        dgbptPcBreak.setLine(line);
        dgbptPcBreak.setPc(pc);
        String data = JSONUtil.toJsonStr(dgbptPcBreak);
        String dpt = JSONUtil.toJsonStr(QuestData.create().setDt(QuestType.BPC).setData(data));
        dbgClient.sendMessage(dpt);
    }


    public void removePcBreakLine(String pc, int line) {
        QuestPcBreak dgbptPcBreak = new QuestPcBreak();
        dgbptPcBreak.setLine(line);
        dgbptPcBreak.setPc(pc);
        String data = JSONUtil.toJsonStr(dgbptPcBreak);
        String dpt = JSONUtil.toJsonStr(QuestData.create().setDt(QuestType.RBPC).setData(data));
        dbgClient.sendMessage(dpt);
    }

}


