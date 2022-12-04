package com.far.vms.opencar.protocol.debug;

import com.far.vms.opencar.ui.DchUtil;

public class QuestData {

    private int dt;
    private String data;

    public int getDt() {
        return dt;
    }

    public QuestData setDt(int dt) {
        this.dt = dt;
        return this;
    }

    public String getData() {
        return data;
    }

    public QuestData setData(String data) {
        this.data = data;
        return this;
    }
    public static QuestData create() {
        return new QuestData();
    }

    public <T> T getDataBean(){




        return null;
    }

}
