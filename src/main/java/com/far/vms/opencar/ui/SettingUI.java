package com.far.vms.opencar.ui;

import cn.hutool.core.util.StrUtil;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.entity.SettingDatas;
import com.far.vms.opencar.ui.event.Notify;
import com.far.vms.opencar.utils.EnvUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class SettingUI {


    private Parent settingUIRoot;

    private Stage stage;

    //gccbin 选择目录
    Button btnGccPathSelect;

    OpenCarWindos ctx;

    TextField txtGccPath;



    //gccpath用
    DirectoryChooser directoryChooser = new DirectoryChooser();


    public static SettingUI create() {
        return new SettingUI();
    }

//    public void createStage(ActionEvent actionEvent) {
//        stage = new Stage();
//        //create root node of scene, i.e. group
//        Group rootGroup = new Group();
//        //create scene with set width, height and color
//        Scene scene = new Scene(rootGroup, 200, 200, Color.WHITESMOKE);
//        //set scene to stage
//        stage.setScene(scene);
//
//        //set title to stage
//        stage.setTitle("New stage");
//
//        //center stage on screen
//        stage.centerOnScreen();
//
//        //show the stage
//        stage.show();
//
//        //add some node to scene
//        Text text = new Text(20, 110, "JavaFX");
//        text.setFill(Color.DODGERBLUE);
//        text.setEffect(new Lighting());
//        text.setFont(Font.font(Font.getDefault().getFamily(), 50));
//
//        //add text to the main root group
//        rootGroup.getChildren().add(text);
//    }

    /**
     * 创建一个Stage，这个Stage使用fxml描绘
     */
    public SettingUI createFxmlStage(final OpenCarWindos ctx) throws IOException {
        stage = new Stage();
        //   URL path = this.getClass().getClassLoader().getResource("fxml/settingUI.fxml"); // 注意路径不带/开头
        File file = new File(EnvUtil.appAtPath + "/config/fxml/settingUI.fxml");
        settingUIRoot = FXMLLoader.load(file.toURI().toURL());
        this.ctx = ctx;
        initControl();
        stage.setTitle("new Stage");
        stage.setScene(new Scene(settingUIRoot, 640, 480));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                stage.hide();
            }
        });
        return this;
    }

    public void show(){
        stage.show();
    }

    public void hide(){
        stage.hide();
    }


    public void setGccPathToTxt(String path) {

        txtGccPath.setText(path);
    }

    public void initControl() {

        SplitPane splitPane = (SplitPane) settingUIRoot.lookup("#splitwin");
        splitPane.getItems().forEach(e -> {
            if (e instanceof TabPane) {
                btnGccPathSelect = (Button) e.lookup("#tabBuild").lookup("#btnGccPathSelect");
                txtGccPath = (TextField) e.lookup("#tabBuild").lookup("#txtGccPath");
            }
        });

        OpenCarWindos openCarWindos = ctx;
        //设置默认

        if (!StrUtil.isEmpty(openCarWindos.getSettingDatas().getBuild().getGccPath())) {
            File defaultGccPathFile = new File(openCarWindos.getSettingDatas().getBuild().getGccPath());
            directoryChooser.setInitialDirectory(defaultGccPathFile);
        }

        btnGccPathSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                //https://baijiahao.baidu.com/s?id=1719456091310997930&wfr=spider&for=pc
                // DirectoryChooser directoryChooser = new DirectoryChooser();

                //directoryChooser.setInitialDirectory();
                File selectedDirectory = directoryChooser.showDialog(stage);

                if (Objects.isNull(selectedDirectory)) return;
                //查找GCC相关的文件 需要注意linux和windonws的情况
                Optional<String> binPath = Arrays.stream(selectedDirectory.list()).filter(findFile -> {
                    return findFile.equals("riscv64-unknown-elf-objdump.exe");
                }).findFirst();

                openCarWindos.setTxtAlertMessage("选择完成");
                setGccPathToTxt(selectedDirectory.getPath());
                ctx.getSettingDatas().getBuild().setGccPath(selectedDirectory.getPath());
                ctx.saveSettingData();
            }
        });


        //接受配置文件加载完成的通知
        Notify.on(Notify.SysType.LOAD_CONF_AFTER, new Notify.INotifyHandler<SettingDatas>() {
            @Override
            public void onEvent(int evtType, Notify.NotifyData<?> data) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        SettingDatas d = (SettingDatas) data.getData();
                        setGccPathToTxt(d.getBuild().getGccPath());
                    }
                });
            }
        });


    }


}
