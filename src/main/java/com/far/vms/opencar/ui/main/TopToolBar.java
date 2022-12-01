package com.far.vms.opencar.ui.main;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.SettingUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class TopToolBar {

    private OpenCarWindos ctx;


    public OpenCarWindos getCtx() {
        return ctx;
    }

    public TopToolBar setCtx(OpenCarWindos ctx) {
        this.ctx = ctx;
        return this;
    }


    /**
     * @param
     * @description: 初始化
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/1
     */
    public void initControl() {
        //绑定菜单事件
        MenuBar menuBar = (MenuBar) ctx.getRootMain().lookup("#mBar");//#ta是textarea的id号
        OpenCarWindos ctxmain = ctx;
        menuBar.getMenus().forEach(e -> {
            e.getItems().forEach(e2 -> {
                if ("open".equals(e2.getId())) {
                    e2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("选择文件");
                            //打开选择镜像的窗口
                            FileChooser fileChooser = new FileChooser();
                            if(!StrUtil.isEmpty(ctx.getSettingDatas().getBuild().getProgFilePath())){
                                fileChooser.setInitialDirectory(new File(ctx.getSettingDatas().getBuild().getProgFilePath()));
                            }

                            fileChooser.getExtensionFilters().addAll(
                                    new FileChooser.ExtensionFilter("elf or image file", "*.img")
//                ,new FileChooser.ExtensionFilter("HTML Files", "*.htm")
                            );
                            File selectedFile = fileChooser.showOpenDialog(ctx.getPrimaryStage());

                            if (selectedFile == null) return;
                            String pt = selectedFile.getParent();
                            ctx.getSettingDatas().getBuild().setProgFilePath(pt);
                            ctx.saveSettingData();
                        }
                    });
                } else if ("mFileSet".equals(e2.getId())) {
                    e2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            try {
                                SettingUI.create().createFxmlStage(ctx);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
        });

    }

}
