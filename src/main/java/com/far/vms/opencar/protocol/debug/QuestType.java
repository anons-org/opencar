package com.far.vms.opencar.protocol.debug;

public class QuestType {


    public final static int
            //读取内存
            READ_MEMORY = 7,
    //删除寄存器断点
    R_REG_BREAK = 6,
    //添加寄存器断点
    A_REG_BREAK = 5,
    //
    REG = 4,
    //单步调试,遇到函数会进入函数
    STEP = 3,
    //删除PC断点
    RBPC = 2,
    // 新增PC断点
    BPC = 1,
    //测试用
    TEST = 0;
}
