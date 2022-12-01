package com.far.vms.opencar.ui.entity;

/*
 * @description: 用于保存UI界面需要保存的记录
 * @author mike/Fang.J
 * @data 2022/11/29
 */
public class SettingDatas {

    private Build build;


    public static class Build {
        String gccPath;
        String progFile;
        //调试程序的目录
        String progFilePath;

        String progName;
        //后缀
        String progSufix;

        public String getProgName() {
            return progName;
        }

        public void setProgName(String progName) {
            this.progName = progName;
        }

        public String getProgSufix() {
            return progSufix;
        }

        public void setProgSufix(String progSufix) {
            this.progSufix = progSufix;
        }

        public String getProgFilePath() {
            return progFilePath;
        }

        public void setProgFilePath(String progFilePath) {
            this.progFilePath = progFilePath;
        }

        public String getGccPath() {
            return gccPath;
        }

        public void setGccPath(String gccPath) {
            this.gccPath = gccPath;
        }

        public String getProgFile() {
            return progFile;
        }

        public void setProgFile(String progFile) {
            this.progFile = progFile;
        }
    }


    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }

    public static Build getModeBuild() {
        return new Build();
    }

}
