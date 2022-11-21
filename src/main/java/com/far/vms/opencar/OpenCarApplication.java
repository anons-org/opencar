package com.far.vms.opencar;

import com.far.vms.opencar.vm.BinFile;
import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.vm.StaticRes;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;


@SpringBootApplication
public class OpenCarApplication {


    public static void main(String[] args) {
        StaticRes.init();
        readBin();
        //永不停机
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        SpringApplication.run(OpenCarApplication.class, args);
    }


    public static void readBin() {
        String binFile = "D:\\AAAA_WORK\\RISC-V-Tools\\os\\riscv-operating-system-mooc\\code\\os\\01-helloRVOS\\build\\kernel.bin";
        File memBinFile = new File(binFile);
        try {
            byte[] datas = new FileInputStream(memBinFile).readAllBytes();
            BinFile binProcess = new BinFile();
            binProcess.setBufSize(8);
            binProcess.setData(datas);
            //将代码写到内存
            //代码写到内存的8k位置 如果代码很大 如何一次写入?  对unsafae代码熟悉的小伙伴分享下
            //讲代码加载到搞地质 0x80000除执行 0x80000从左往右还剩512K空间 用作硬件、bios编址、以及堆栈空间
            //注意 sbi(bios)必须从0开始
            long startAddr = StaticRes.krStart;
            while (binProcess.available() > 0) {
                long code = Long.reverseBytes(binProcess.readLong());
                StaticRes.bus.storeDDw(startAddr, code);
                startAddr += 8;
            }
            //  int val = StaticRes.bus.loadDw(0x80000);
            Cpu ctx = StaticRes.cpus[0];
            // ctx.setCodes(binProcess);
            ctx.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
