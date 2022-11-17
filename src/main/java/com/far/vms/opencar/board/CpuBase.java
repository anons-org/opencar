package com.far.vms.opencar.board;


import com.far.vms.opencar.vm.BinFile;
import com.far.vms.opencar.vm.StaticRes;

public class CpuBase {


    //所有的寄存器
    public Register register;

    protected BinFile codes;
    //当前执行到的行 该字段后续还需要优化，因为执行的code需要确定是哪个文件的多少行？
    //默认第一行开始
    public int curLine = 1;

    //流水线上的刷新信号
    public short flushReq;
    //预测地址
    protected long predict = 0;
    protected long PC = 0;
    //IFU模块的PC寄存器
    public long ifuPcReg;

    //上电状态
    protected boolean power = false;
    //32b代码
    private int code;

    public long getPredict() {
        return predict;
    }

    public void setPredict(long predict) {
        this.predict = predict;
    }

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

            if (ifuPcReg < 0) {
                //出现故障
                return 0;
            }

            PC = ifuPcReg;
            //清楚刷新流水线信号
            flushReq = 0;
            //ifu模块的PC寄存器清0
            ifuPcReg = 0;
            //下一个地址
            predict = PC+4;


        } else {//静态预测为当前指令地址+4
            predict += 4;
        }

        if (power) {
            if (PC + 4 == predict - 4) {
                PC = predict - 4;
            } else {//预测地址不一致

            }
        } else {
            power = true;
        }
        return PC;

    }


    protected int getCode() {
        this.code = StaticRes.bus.loadDw(ifu());
        return this.code;
    }


    protected int getOpCode() {
        //左移25位 将opcode移到最高位
        //再右移会带符号 将高25b全部置0
        return 0b1111111 & ((code << 25) >> 25);
    }

}
