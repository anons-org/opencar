package com.far.vms.opencar.board;

public interface IBus {
    //存储数据
    public void storeByte(long addr,byte val);



    //双字32位
    public void storeDw(long addr,int val);

    //4字64位
    public void storeDDw(long addr,long val);
    //读取64位数据
    public long loadDDw(long addr);

    public int loadDw(long addr);

    //单字 16位
    public short storeSw(long addr,short val);

    public int loadSw(long addr);


    //加载数据
    public long load(long addr);



}
