package com.far.vms.opencar.ui;

import cn.hutool.json.JSONUtil;
import com.far.vms.opencar.ui.net.DbgClient;
import com.far.vms.opencar.ui.proto.DGBPTPcBreak;
import com.far.vms.opencar.ui.proto.DbgType;

/*
 * @description: 调试器工具
 * @author mike/Fang.J
 * @data 2022/12/3
 */
public class DchUtil {


    static class SendData {
        private int dt;
        private Object data;

        public int getDt() {
            return dt;
        }

        public SendData setDt(int dt) {
            this.dt = dt;
            return this;
        }

        public Object getData() {
            return data;
        }

        public SendData setData(Object data) {
            this.data = data;
            return this;
        }


        public static SendData create() {
            return new SendData();
        }

    }


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


    public static DchUtil create() {
        DchUtil dchUtil = new DchUtil();
        dchUtil.setDbgClient(new DbgClient());
        dchUtil.getDbgClient().setOnMessageCall((s) -> {
            System.out.println(s);
            return true;
        });
        dchUtil.getDbgClient().start();
        return dchUtil;
    }

    /**
     * @param pc
     * @description: 添加断点 就会让dbgServer存储一个数据包
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/3
     */
    public void addPcBreakLine(String pc, int line) {
        DGBPTPcBreak dgbptPcBreak = new DGBPTPcBreak();
        dgbptPcBreak.setLine(line);
        dgbptPcBreak.setPc(pc);
        String dpt = JSONUtil.toJsonStr(SendData.create().setDt(DbgType.BPC).setData(dgbptPcBreak));
        dbgClient.sendMessage(dpt);
    }
}


