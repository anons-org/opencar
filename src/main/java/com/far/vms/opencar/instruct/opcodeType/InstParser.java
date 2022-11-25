package com.far.vms.opencar.instruct.opcodeType;

public class InstParser {


    private int[] INST_TYPES = new int[256];

    //包装解码后的code
    private int[] opcodeWrappers;

    public static class INST_TYPE_ENUM {
        public final static int I = 0,
                R = 1,
                U = 2,
                J = 3,
                B = 4,
                S = 5;
    }


    public InstParser() {

        opcodeWrappers = new int[10];

        //0011
        INST_TYPES[0x03] = INST_TYPE_ENUM.I;
        //10011
        INST_TYPES[0x13] = INST_TYPE_ENUM.I;
        //10111
        INST_TYPES[0x17] = INST_TYPE_ENUM.U;
        //11011
        INST_TYPES[0x1B] = INST_TYPE_ENUM.I;
        //100011
        INST_TYPES[0x23] = INST_TYPE_ENUM.S;

        INST_TYPES[0x37] = INST_TYPE_ENUM.U;
        //0111011
        INST_TYPES[0x3b] = INST_TYPE_ENUM.R;
        //bne 1100011
        INST_TYPES[0x63] = INST_TYPE_ENUM.B;
        INST_TYPES[0x67] = INST_TYPE_ENUM.I;
        INST_TYPES[0x6F] = INST_TYPE_ENUM.J;
        INST_TYPES[0x73] = INST_TYPE_ENUM.I;

    }

    public void clearWrapper() {
        for (int i = 0; i < opcodeWrappers.length; i++) {
            opcodeWrappers[i] = 0;
        }
    }

    public int[] decode(int opcode, int code) {

        clearWrapper();

        opcodeWrappers[0] = opcode;

        switch (INST_TYPES[opcode]) {
            case INST_TYPE_ENUM.I:
                //rd 右移7 保留低5b 高位全清0;
                opcodeWrappers[1] = 0b11111 & code >> 7;
                //rs1
                opcodeWrappers[3] = 0b11111 & code >> 15;
                //immm 12 b
                opcodeWrappers[4] = 0xFFF & code >> 20;

                break;
            case INST_TYPE_ENUM.U:
                break;
            case INST_TYPE_ENUM.S:

                //imm1
                opcodeWrappers[1] = 0b11111 & code >> 7;
                //rs1
                opcodeWrappers[3] = 0b11111 & code >> 15;
                //rs2
                opcodeWrappers[4] = 0b11111 & code >> 20;
                //imm
                opcodeWrappers[5] = 0b1111111 & code >> 25;

                break;
            case INST_TYPE_ENUM.R:
                break;
            case INST_TYPE_ENUM.B://1100011

                //imm1 先只拿4b
                opcodeWrappers[1] = 0b1111 & code >> 8;

                //rs1 右移15位 留5位
                opcodeWrappers[3] = 0b11111 & code >> 15;

                //rs2
                opcodeWrappers[4] = 0b11111 & code >> 20;


//                //imm2 右移25b 保留6b
//                opcodeWrappers[5] = 0b111_111 & code >> 25;
//                //存储了10:1
//                opcodeWrappers[1] = (opcodeWrappers[5] << 4) | opcodeWrappers[1];
//                //清空备用
//                opcodeWrappers[5] = 0;
//                //右移7b保留1b 再左移10b 留出10个空间 再和之前的imm合并
//                opcodeWrappers[5] = ((0b1 & code >> 7) << 10) | opcodeWrappers[1];
//                opcodeWrappers[1] = opcodeWrappers[5];
//                //保留1b 再保留11个空间
//                opcodeWrappers[5] = ((0b1 & code >> 31) << 11) | opcodeWrappers[1];
//                //完成imm1
//                opcodeWrappers[1] = opcodeWrappers[5];


                //左移25b 保留6b 再右移 4b 留出低4位的4个空间
                opcodeWrappers[5] = (0b111_111 & code >> 25) << 4;     // ((code >> 25) & 0x3f) << 5
                //合并为10b
                opcodeWrappers[1] = opcodeWrappers[1] | opcodeWrappers[5];
                //合并为11b
                opcodeWrappers[1] = opcodeWrappers[1] | (((code >> 7) & 0b1) << 10);
                opcodeWrappers[5] = 0;
                //移到12b的位置
                opcodeWrappers[5] = (0b1 & (code >> 31)) << 11;
                //0b00....1  合并为12b立即数
                opcodeWrappers[1] = opcodeWrappers[1] | opcodeWrappers[5];
                //移到最高处，再带符号移回来 <如果12b的最高位不是1 前面20位也还都是0>
                opcodeWrappers[1] = (opcodeWrappers[1] << 20) >> 20;
                //保证地址是2 的倍数
                opcodeWrappers[1] = opcodeWrappers[1] << 1;


             //  opcodeWrappers[1] = (((((0b1 & (code >> 31)) << 11) | opcodeWrappers[1]) << 20) >> 20) << 1;

//https://www.cnblogs.com/gaozhenyu/p/14166473.html 借鉴 此博客
//                opcodeWrappers[1] = ((0b1 & code >> 7) << 11) | opcodeWrappers[1];
//                int test = (((code >> 8) & 0xf) << 1) | (((code >> 25) & 0x3f) << 5) | (((code >> 7) & 0x1) << 11) | ((code >> 20) & 0xfffff000);
//                test = 0x82160 + 0xff;
                break;
            case INST_TYPE_ENUM.J:
                break;
            default:
        }
        return opcodeWrappers;
    }


}
