package com.far.vms.opencar.vm.inst;


import com.far.vms.opencar.board.Cpu;

public class OpBase<T> {

    //操作码
    protected int opcode;
    //code
    protected int code;

    protected Cpu ctx;

    public int getOpcode() {
        return opcode;
    }

    public T setOpcode(int opcode) {
        this.opcode = opcode;
        return (T) this;
    }

    public int getCode() {
        return code;
    }



    public T setCtx(Cpu ctx) {
        this.ctx = ctx;
        return (T) this;
    }

    public T setCode(int code) {
        this.code = code;
        return (T)this;
    }



}
