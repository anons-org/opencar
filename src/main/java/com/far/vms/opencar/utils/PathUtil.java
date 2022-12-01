package com.far.vms.opencar.utils;

import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.util.Arrays;

public class PathUtil {

    public static String getJarPath(String filePath) {
        String s = filePath.substring(1, filePath.length());
        String[] v = s.split("\\/");
        v = ArrayUtil.remove(v, v.length-1);
        s = ArrayUtil.join(v, "/");
        return s;
    }

}
