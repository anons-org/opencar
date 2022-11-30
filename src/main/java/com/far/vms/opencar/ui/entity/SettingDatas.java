package com.far.vms.opencar.ui.entity;

/*
 * @description: 用于保存UI界面需要保存的记录
 * @author mike/Fang.J
 * @data 2022/11/29
 */
public class SettingDatas {

    private String gccBinPath;
    //程序文件
    private String progBinFile;

    public String getGccBinPath() {
        return gccBinPath;
    }

    public void setGccBinPath(String gccBinPath) {
        this.gccBinPath = gccBinPath;
    }

    public String getProgBinFile() {
        return progBinFile;
    }

    public void setProgBinFile(String progBinFile) {
        this.progBinFile = progBinFile;
    }
}
