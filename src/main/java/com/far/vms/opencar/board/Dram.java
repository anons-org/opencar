package com.far.vms.opencar.board;

import sun.misc.Unsafe;

import java.lang.reflect.Field;


/*
 * @description:
 * 物理内存块
 * @author mike/Fang.J
 * @data 2022/11/17
 */
public class Dram implements IBus {

    //java堆外内存的起始地址
    private long memoryAddress;

    // 获取Unsafe属性值
    private Unsafe unsafe;


    /**
     * @param
     * @description: 初始化内存 在JAVA中申请的内存地址的编号不是从0x0开始的，需要做转换
     * @return: void
     * @author mike/Fang.J
     * @date: 2022/10/26 3:44
     */
    public void init() {

        try {
            mallocMemory();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param addr
     * @param val
     * @description: 存储的最小单元是字节...
     * @return: void
     * @author mike/Fang.J
     * @date: 2022/10/26 3:57
     */
    @Override
    public void storeByte(long addr, byte val) {
        //此处只能是写一个字节的数据
        //+addr 表示是在什么位置写入数据 比如addr=0x00 就是在0位置写数据

        unsafe.putByte(memoryAddress + addr, val);
    }

    @Override
    public byte loadByte(long addr) {
        return unsafe.getByte(memoryAddress + addr);
    }


    //双字
    @Override
    public void storeDw(long addr, int val) {
        unsafe.putInt(memoryAddress + addr, val);
    }

    @Override
    public void storeDDw(long addr, long val) {
        unsafe.putLong(memoryAddress + addr, val);
    }

    @Override
    public long loadDDw(long addr) {
        return unsafe.getLong(memoryAddress + addr);
    }

    @Override
    public int loadDw(long addr) {
        return unsafe.getInt(memoryAddress + addr);
    }

    @Override
    public short storeSw(long addr, short val) {
        return 0;
    }

    @Override
    public int loadSw(long addr) {
        return 0;
    }


    @Override
    public long load(long addr) {
        return 0;
    }

    public void mallocMemory() throws NoSuchFieldException, IllegalAccessException {

        // https://blog.csdn.net/ioteye/article/details/109343532
        // 获取Unsafe类theUnsafe属性的Field
        Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        // 设置访问权限
        theUnsafeField.setAccessible(true);
        // 获取Unsafe属性值
        unsafe = (Unsafe) theUnsafeField.get(null);


        // Integer类型长度
        int intByteLength = Integer.BYTES;
        int longSize = 8;
        // 分配内存块大小, 分配的内存块必须是类型的整数倍
        int allocateMemorySize = intByteLength * 2;
        //默认1G
        long dramSize = 1024 * 1024 * 1024 * 1L;
        memoryAddress = unsafe.allocateMemory(dramSize);

//        for (long i = 0; i < dramSize ; i+=8) {
//            unsafe.putLong(memoryAddress + i, 100L);
//        }
//
//        // 内存地址数据赋值
//        unsafe.putInt(memoryAddress, 15);
//        unsafe.putInt(memoryAddress + intByteLength, 20);
//
//        // 读取内存数据
//        int value1 = unsafe.getInt(memoryAddress);
//        int value2 = unsafe.getInt(memoryAddress + intByteLength);
//        // 未被初始化, 返回内存地址
//        int value3 = unsafe.getInt(memoryAddress + intByteLength * 2);

        // 打印数据
        //  System.out.printf("allocate value1=%d %d %d\n", value1, value2, value3);


    }

    public static void main(String[] args) {

        Dram dram = new Dram();
        dram.init();
        dram.storeDw(0x00, 1024);
        int vals = dram.loadDw(0x00);

    }

}
