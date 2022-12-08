package com.far.vms.opencar.ui.main.RightTablePanle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryData {

    /**
                   1  2  3......15
        0x00000010 0e 0a 03......
        0x00000020 0e 0a 03......
     */
    Map<Long,List<Map<String,InnerMemVal>>> memMap = new HashMap<>();


    public static class InnerMemVal{
        //字节数据
        int val;
        String viewVal;

        public int getVal() {
            return val;
        }

        public void setVal(int val) {
            this.val = val;
        }

        public String getViewVal() {
            return viewVal;
        }

        public void setViewVal(String viewVal) {
            this.viewVal = viewVal;
        }
    }

    public Map<Long, List<Map<String, InnerMemVal>>> getMemMap() {
        return memMap;
    }

    public void setMemMap(Map<Long, List<Map<String, InnerMemVal>>> memMap) {
        this.memMap = memMap;
    }
}
