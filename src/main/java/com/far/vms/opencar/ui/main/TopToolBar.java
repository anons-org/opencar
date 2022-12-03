package com.far.vms.opencar.ui.main;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.SettingUI;
import com.far.vms.opencar.ui.DchUtil;
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
                if ("open".equals(e2.getId())) {//调试文件
                    e2.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            System.out.println("选择文件");
                            //打开选择镜像的窗口
                            FileChooser fileChooser = new FileChooser();
                            if (!ObjectUtil.isNull(ctx.getSettingDatas())
                                    && !ObjectUtil.isNull(ctx.getSettingDatas().getBuild())
                                    && !StrUtil.isEmpty(ctx.getSettingDatas().getBuild().getProgFilePath())){
                                fileChooser.setInitialDirectory(new File(ctx.getSettingDatas().getBuild().getProgFilePath()));
                            }

                            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("elf or image file", "*.img")
//                ,new FileChooser.ExtensionFilter("HTML Files", "*.htm")
                            );

                            File selectedFile = fileChooser.showOpenDialog(ctx.getPrimaryStage());
                            if (selectedFile == null) return;
                            String pt = selectedFile.getParent();
                            String[] pathArs = selectedFile.getPath().replaceAll("\\\\", "\\/").split("\\/");
                            String fileName = pathArs[pathArs.length - 1];
                            pathArs = fileName.split("\\.");
                            fileName = pathArs[0];
                            String fileSuifx = pathArs[pathArs.length - 1];
                            ctx.getSettingDatas().getBuild().setProgFilePath(pt);
                            ctx.getSettingDatas().getBuild().setProgName(fileName);
                            ctx.getSettingDatas().getBuild().setProgSufix(fileSuifx);
                            ctx.saveSettingData();

                            if(   ctxmain.addCode(selectedFile.getPath())){
                                ctxmain.setDchUtil(DchUtil.create());
                            }





//                            ObservableList<OpenCarWindos.CodeData> data = FXCollections.observableArrayList();
//                       //     String kr = "D:\\AAAA_WORK\\RISC-V-Tools\\os\\riscv-operating-system-mooc\\code\\os\\01-helloRVOS\\build\\kernel-img.img";
//                            String buildTool = "D:\\AAAA_WORK\\RISC-V-Tools\\riscv64-unknown-elf-toolchain-10.2.0-2020.12.8-x86_64-w64-mingw32\\bin\\riscv64-unknown-elf-objdump.exe";
//
//
//                            ShellUtil.runShell(buildTool, new String[]{buildTool, selectedFile.getPath(), "-d"}, cmdInf -> {
//                                data.add(new OpenCarWindos.CodeData(getCodeLineNumber(), getCircle(), cmdInf));
//                                //  System.out.println(cmdInf);
//                                return 0;
//                            });


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
                } else if ("openProgFile".equals(e2.getId())) {
//                    //打开执行文件 启动模拟器
//                    e2.setOnAction(new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//
//                            FileChooser fileChooser = new FileChooser();
////                            if (!StrUtil.isEmpty(ctx.getSettingDatas().getBuild().getProgFilePath())) {
////                                fileChooser.setInitialDirectory(new File(ctx.getSettingDatas().getBuild().getProgFilePath()));
////                            }
//                            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("elf or image file", "*.*"));
//                            File selectedFile = fileChooser.showOpenDialog(ctx.getPrimaryStage());
//
//                            if (selectedFile == null) return;
////                            String pt = selectedFile.getParent();
////                            ctx.getSettingDatas().getBuild().setProgFilePath(pt);
////                            ctx.saveSettingData();
////
////                            ctxmain.addCode(selectedFile.getPath());
//
//                            new Thread(() -> {
//                                //模拟器内部的通讯和调试器没有任何关系，他们之间的通讯，只能依靠socket
//                                OpenCarApplication.run(selectedFile.getPath());
//                            }).start();
//
//                            //等待模拟器的server启动完成，这个实现很蠢 后面完善
//                            System.out.println("debug  server init......");
//
//                            try {
//                                Thread.sleep(8000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//
//                            System.out.println("debug  server InitComplete .....");
//
//                        }
//                    });

                }
            });
        });

    }

}
