package com.far.vms.opencar.board;


import com.far.vms.opencar.vm.BinFile;
import com.far.vms.opencar.vm.StaticRes;
import com.far.vms.opencar.vm.opcode.*;

public class CpuBase {


    //所有的寄存器
    public Register register;

    protected BinFile codes;
    //当前执行到的行 该字段后续还需要优化，因为执行的code需要确定是哪个文件的多少行？
    //默认第一行开始
    public int curLine = 1;

    //流水线上的刷新信号
    protected short flushReq;
    //预测地址
    protected long predict = 0;
    protected long PC = 0;
    //上电状态
    protected boolean power = false;



    public BinFile getCodes() {
        return codes;
    }

    public void setCodes(BinFile codes) {
        this.codes = codes;
    }

    public Register getRegister() {
        return register;
    }

    public void setRegister(Register register) {
        this.register = register;
    }


    /*
    指令是32位为什么返回Long?
    因为long指的内存的地址...
    如果要取指，实际是从当前的PC地址获取32位数据作为指令使用
 */
    public long ifu() {
        return bpu();
    }

    //分支预测模块 返回指令地址
    public long bpu() {
        if (flushReq == 1) {//收到流水线指令刷新信号
            //无条件跳转会刷新流水线
            PC = this.register.regs.get(Register.RegAddr.PC);
        } else {//静态预测为当前指令地址+4
            predict += 4;
        }

        if (power == true) {
            if (PC + 4 == predict - 4) {
                PC = predict - 4;
            } else {//预测地址不一致

            }
        } else {
            power = true;
        }
        return PC;

    }


    protected int getCode(){

        return  StaticRes.bus.loadDw(ifu());
    }

}
