package com.far.vms.opencar.board;

import com.far.vms.opencar.hardware.interf.IExternalDeviceMemory;
import com.far.vms.opencar.hardware.uart.Uart;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Uart0 implements IExternalDeviceMemory {


    /*
     * POWER UP DEFAULTS
     * IER = 0: TX/RX holding register interrupts are both disabled
     * ISR = 1: no interrupt penting
     * LCR = 0
     * MCR = 0
     * LSR = 60 HEX
     * MSR = BITS 0-3 = 0, BITS 4-7 = inputs
     * FCR = 0
     * TX = High
     * OP1 = High
     * OP2 = High
     * RTS = High
     * DTR = High
     * RXRDY = High
     * TXRDY = Low
     * INT = Low
     */
    /*

        #define RHR 0	// Receive Holding Register (read mode)
        #define THR 0	// Transmit Holding Register (write mode)
        #define DLL 0	// LSB of Divisor Latch (write mode)
        #define IER 1	// Interrupt Enable Register (write mode)
        #define DLM 1	// MSB of Divisor Latch (write mode)
        #define FCR 2	// FIFO Control Register (write mode)
        #define ISR 2	// Interrupt Status Register (read mode)
        #define LCR 3	// Line Control Register
        #define MCR 4	// Modem Control Register
        #define LSR 5	// Line Status Register
        #define MSR 6	// Modem Status Register
        #define SPR 7	// ScratchPad Register

     */


    //UART0内存布局
    // UART0 0x10000000L
    private final long START_ADDR = 0x10000000L;

    //寄存器偏移
    private final int THR_OFFSET = 0;
    private final int DLL_OFFSET = 0;
    private final int IER_OFFSET = 1;
    private final int ISR_OFFSET = 2;
    private final int LCR_OFFSET = 3;
    private final int LSR_OFFSET = 5;

    //地址映射 用于判断
    private Integer[] regsMap = new Integer[10];
    //用于存储
    private byte[] regs = new byte[10];

    //创建锁
    ReentrantLock reentrantLock = new ReentrantLock();

    //临时用的
    private Long v1;


    public Uart0() {
        //设置初始值
        regsMap[IER_OFFSET] = IER_OFFSET;
        regsMap[ISR_OFFSET] = ISR_OFFSET;
        regsMap[THR_OFFSET] = THR_OFFSET;
        regsMap[DLL_OFFSET] = DLL_OFFSET;
        regsMap[LCR_OFFSET] = LCR_OFFSET;
        regsMap[LSR_OFFSET] = LSR_OFFSET;

        //初始化寄存器数据
        regs[IER_OFFSET] = 0;
        regs[ISR_OFFSET] = 0;

        regs[DLL_OFFSET] = 0;
        regs[THR_OFFSET] = 0;
        regs[LCR_OFFSET] = 0;
        regs[LSR_OFFSET] = 0;

    }


    @Override
    public boolean monitorMemoryChange(long addr, byte val) {
        Optional<Integer> findData = Arrays.stream(regsMap).filter(e -> {
            if (e == null) {
                return false;
            }
            return START_ADDR + e == addr;
        }).findFirst();

        if (findData.isPresent()) {
            //偏移号
            int rOfst = findData.get();
            try {
                //多核情况下会抢占资源
                reentrantLock.lock();
                processWriteData(rOfst, val);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                reentrantLock.unlock();
            }
        }

        //不管设备是否关注这个地址，只要设备 监控了 内存写入 都要返回true,   true 表示当前设备已处理了。。
        return true;
    }

    @Override
    public byte monitorMemoryRead(long addr) {
        //计算差值 得到需要读取的寄存器的偏移
        v1 = addr - START_ADDR;


        processReadData(v1.intValue());


        return regs[v1.intValue()];
    }

    @Override
    public boolean isMonitorRead() {
        return true;
    }

    @Override
    public boolean isMeMonitorAddr(long addr) {
        Optional<Integer> findData = Arrays.stream(regsMap).filter(e -> {
            if (e == null) {
                return false;
            }
            return START_ADDR + e == addr;
        }).findFirst();
        return findData.isPresent();
    }

    //处理数据
    private void processWriteData(int rOfst, byte val) {

        if (rOfst == 0) {
            regs[rOfst] = val;
            System.out.println(String.format("uart0 on write to ofs=%d val=%d<%s>", rOfst, val, val));
        } else if (rOfst == 1) {
            regs[rOfst] = val;
        } else if (rOfst == 2) {
            regs[rOfst] = val;
        } else if (rOfst == 3) {// LCR 设置就行，无需做什么操作
            regs[rOfst] = val;
        } else if (rOfst == 5) {// LSR
            regs[rOfst] = val;
        }

    }

    private void processReadData(int rOfst) {

        if (rOfst == 0) {
            System.out.println(String.format("uart0 on read to ofs=%d", rOfst));
        } else if (rOfst == 1) {
            System.out.println(String.format("uart0 on read to ofs=%d", rOfst));
        } else if (rOfst == 2) {
            System.out.println(String.format("uart0 on read to ofs=%d", rOfst));
        } else if (rOfst == 3) {// LCR 设置就行，无需做什么操作
            System.out.println(String.format("uart0 on read to ofs=%d", rOfst));
        } else if (rOfst == 5) {// LSR
            System.out.println(String.format("uart0 on read to ofs=%d", rOfst));
        }

    }


}
