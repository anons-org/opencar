package com.far.vms.opencar.protocol.debug;

public class QuestType {


    public final static int
            //单步调试,遇到函数会进入函数
            STEP = 3,
            //删除PC断点
            RBPC = 2,
            // 新增PC断点
            BPC = 1;
}
