package com.far.vms.opencar.board;

import com.far.vms.opencar.hardware.interf.IExternalDeviceMemory;
import com.far.vms.opencar.hardware.uart.ParamConfig;
import com.far.vms.opencar.hardware.uart.SerialPortManager;

import com.far.vms.opencar.hardware.uart.Uart;

import java.io.BufferedInputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Uart0 implements IExternalDeviceMemory{

    //UART0内存布局0x10000000L
    private final long START_ADDR = 0x10000000L;
    //串口工具
    // private SerialPortUtils serialPortUtils;


    private SerialPortManager serialPortManager;


    //寄存器偏移
    private final int THR_OFFSET = 0;
    private final int DLL_OFFSET = 0;
    private final int RBR_OFFSET = 0;
    private final int IER_OFFSET = 1;
    private final int ISR_OFFSET = 2;
    private final int LCR_OFFSET = 3;
    private final int LSR_OFFSET = 5;

    //发送缓冲
    private byte[] writeBuffer;


    //地址映射 用于判断
    private Integer[] regsMap = new Integer[10];


    //创建锁
    ReentrantLock reentrantLock = new ReentrantLock();

    //临时用的
    private Long v1;



    //dlab状态
    private static class DLAB_STAT {
        public final static short ZEOR = 0;
        public final static short ONE = 1;
    }

    //默认0
    private short DLAB = DLAB_STAT.ZEOR;

    private RegMode regData;


    public class RegMode {

        public byte DLL;
        public byte DLM;
        //默认为可写状态 也就是THR就绪
        public byte LSR = 0b00100000;
        public byte LCR;
        public byte RBR;
        public byte FCR;
        public byte IER;
        public byte IIR;
        //发送缓冲 32个字节
        public byte[] THR = new byte[32];
    }


    public Uart0() {


        // serialPortUtils = new SerialPortUtils();

        serialPortManager = new SerialPortManager();


        System.out.println("Start Connecting the serial port. COM1");

        serialPortManager.openPort("COM1", 9600);
        System.out.println("Serial port connected successfully");
        //发送缓冲只有一字节，多字节发送 接口还没写好
        writeBuffer = new byte[1];
        regData = new RegMode();

        //设置初始值 这些值用于计算偏移
        regsMap[IER_OFFSET] = IER_OFFSET;
        regsMap[ISR_OFFSET] = ISR_OFFSET;
        regsMap[THR_OFFSET] = THR_OFFSET;
        regsMap[DLL_OFFSET] = DLL_OFFSET;
        regsMap[LCR_OFFSET] = LCR_OFFSET;
        regsMap[LSR_OFFSET] = LSR_OFFSET;

    }


    @Override
    public boolean monitorMemoryChange(long addr, byte val) {
        Optional<Integer> findData = Arrays.stream(regsMap).filter(e -> {
            if (e == null) return false;
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
        return processReadData(v1.intValue());
    }

    @Override
    public boolean isMonitorRead() {
        return true;
    }

    @Override
    public boolean isMeMonitorAddr(long addr) {
        Optional<Integer> findData = Arrays.stream(regsMap).filter(e -> {
            if (e == null) return false;
            return START_ADDR + e == addr;
        }).findFirst();
        return findData.isPresent();
    }

    /**
     * @description: LCR 被修改时 需要检测是否DLAB字段被修改
     * @return: void
     * @author mike/Fang.J
     * @data 2022/11/26
     */


    private void processWriteData(int rOfst, byte val) {

        if (rOfst == 0) {
            if (DLAB == DLAB_STAT.ZEOR) {
                //应先设置LSR状态为不可继续发送数据
                regData.LSR = 0;
                //此处执行完了，再修改状态LSR状态
                writeBuffer[0] = val;
                serialPortManager.sendToPort(writeBuffer);
                regData.LSR = 1 << 5;
            } else {
                regData.DLL = val;
            }
        } else if (rOfst == 1) {
            if (DLAB == DLAB_STAT.ZEOR) {
                regData.IER = val;
            } else {
                regData.DLM = val;
            }
        } else if (rOfst == 2) {
            if (DLAB == DLAB_STAT.ZEOR) {
                regData.FCR = val;
            } else {
                regData.FCR = val;
            }

        } else if (rOfst == 3) {// LCR 被设置时，修改DLAB状态
            regData.FCR = val;
            if ((0b1 & (regData.FCR >> 7)) == 0x1) {
                DLAB = DLAB_STAT.ONE;
            } else {
                DLAB = DLAB_STAT.ZEOR;
            }
        } else if (rOfst == 5) {// LSR
            regData.LSR = val;
        }
        System.out.println(String.format("uart0 on write to ofs=%d val=0x%x<%s>", rOfst, val, val));

    }

    private byte processReadData(int rOfst) {

        System.out.println(String.format("uart0 on read to ofs=%d", rOfst));
        if (rOfst == 0) {
            if (DLAB == DLAB_STAT.ZEOR) {
                return regData.RBR;
            } else {
                return regData.DLL;
            }
        } else if (rOfst == 1) {
            if (DLAB == DLAB_STAT.ZEOR) {
                return regData.IER;
            } else {
                return regData.DLM;
            }
        } else if (rOfst == 2) {
            if (DLAB == DLAB_STAT.ZEOR) return regData.IIR;
            return regData.IIR;
        } else if (rOfst == 3) {// LCR 设置就行，无需做什么操作
            return regData.LCR;
        } else if (rOfst == 5) {// LSR
            return regData.LSR;
        }
        throw new RuntimeException("uart0 addr error");
    }


    public static void main(String[] args) {

    }


}
