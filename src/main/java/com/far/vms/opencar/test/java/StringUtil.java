package com.far.vms.opencar.test.java;

import cn.hutool.core.util.StrUtil;

public class StringUtil {

    public static void main(String[] args) {
        String s = "fffffff3";
        s = s.substring(s.length() - 2);
        s = StrUtil.sub(s, s.length() - 2, -2);
        System.out.println(s);
    }

}
