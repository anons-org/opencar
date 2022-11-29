package com.far.vms.opencar.complier;

import java.util.LinkedList;
import java.util.Queue;

public class Parser {


    private String code;

    private int idx;


    private String nextChar() {
        return code.substring(idx++);
    }

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
     * @description:
     * 根据代码行的代码看改行能不能下断点
     * @return: boolean
     * @author mike/Fang.J
     * @data 2022/11/30
     */
    public static boolean canBreakForCode(String code) {
        int idx = 0;
        String prevChar = "", testChar = "";
        while (idx < code.length()) {
            testChar = code.substring(idx, ++idx);
            if (":".equals(testChar)) {
                return !">".equals(prevChar);
            } else {
                prevChar = testChar;
            }
        }
        return false;
    }


}
