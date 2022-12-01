package com.far.vms.opencar.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.entity.SettingDatas;

public class EnvUtil {
    //jar所在的目录
    public static String appAtPath;

    public static void initEvn() {
        String path = PathUtil.getJarPath(EnvUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println("appAtPath = " + path);
        appAtPath = path;
    }

    /**
     * @param ctx
     * @description: 读取设置文件
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/1
     */
    public static void loadConf(final OpenCarWindos ctx) {
        String data = FileUtil.readUtf8String(EnvUtil.appAtPath + "/config/setting.json");
        SettingDatas cnf = JSONUtil.toBean(data, SettingDatas.class);
        ctx.setSettingDatas(cnf);
    }

}
