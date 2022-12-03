package com.far.vms.opencar.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
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
        SettingDatas cnf;
        try {
            cnf = JSONUtil.toBean(data, SettingDatas.class);
            ctx.setSettingDatas(cnf);
        } catch (JSONException e) {
            //设置一个空的配置
            cnf = new SettingDatas();
            cnf.setBuild(SettingDatas.getModeBuild());
            ctx.setSettingDatas(cnf);
            e.printStackTrace();
        }

    }


    public static boolean hasGccPath(final OpenCarWindos ctx) {

        //检查是否配置了gcc的路径

        return !(ObjectUtil.isNull(ctx.getSettingDatas())
                || ObjectUtil.isEmpty(ctx.getSettingDatas().getBuild())
                || ObjectUtil.isEmpty(ctx.getSettingDatas().getBuild().getGccPath())
        );

    }


}
