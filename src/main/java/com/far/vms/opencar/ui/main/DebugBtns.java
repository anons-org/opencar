package com.far.vms.opencar.ui.main;

import cn.hutool.core.util.StrUtil;
import com.far.vms.opencar.OpenCarApplication;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.SettingUI;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class DebugBtns {

    private OpenCarWindos ctx;


    public OpenCarWindos getCtx() {
        return ctx;
    }

    public DebugBtns setCtx(OpenCarWindos ctx) {
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
        SplitPane splitPane = (SplitPane) ctx.getRootMain().lookup("#spbox");//#ta是textarea的id号

        splitPane.getItems().forEach(e -> {
            if (e instanceof AnchorPane) {
                AnchorPane ap = (AnchorPane) e;
                ap.getChildren().forEach(e2 -> {
                    if (e2 instanceof HBox) {
                        ((HBox) e2).getChildren().forEach(e3 -> {
                            if ("buildBinAndRun".equals(e3.getId())) {
                                ((Button) e3).setOnAction(new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        String pFile = ctx.buildElfToBin();
                                        if (!"".equals(pFile)) {
                                            //启动模拟器
                                            ctx.startSimulator(pFile);
                                        }

                                    }
                                });
                            }
                        });
                    }
                });

            }
        });

//        Button buildBinAndRun = (Button) anchorPane.lookup("#hbox").lookup("#buildBinAndRun");

    }

}
