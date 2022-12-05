package com.far.vms.opencar.complier;

import cn.hutool.core.util.StrUtil;

import java.util.LinkedList;
import java.util.Queue;

public class Parser {


    private String code;

    private int idx;

    private static String pc = "";




    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void run() {


    }

    /**
     * @param code
     * @description: 根据代码行的代码看改行能不能下断点
     * @return: boolean
     * @author mike/Fang.J
     * @data 2022/11/30
     */
    public static boolean canBreakForCode(String code) {
        int idx = 0;
        //当前捕获的代码
        pc="";
        String prevChar = "", testChar = "";
        while (idx < code.length()) {
            testChar = code.substring(idx, ++idx);
            pc += testChar;
            if (":".equals(testChar)) {

                String nchar = "";
                if( idx < code.length()-1  ){
                     nchar = code.substring(idx, idx+1);
                }
                return !">".equals(prevChar) && !"\\".equals(nchar);
            } else {
                prevChar = testChar;
            }
        }
        return false;
    }

    public static String getCodeForPc() {
        String s = pc;
        pc = "";
        s = StrUtil.trim(s);
        s = s.substring(0, s.length() - 1);
        return s;
    }


}
