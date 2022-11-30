package com.far.vms.opencar.ui;

import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.entity.SettingDatas;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class SettingUI {


    private Parent settingUIRoot;

    private Stage stage;

    private SettingDatas settingDatas;


    //gccbin 选择目录
    Button btnGccPathSelect;

    OpenCarWindos ctx;


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
    public void createFxmlStage(final OpenCarWindos ctx) throws IOException {
        stage = new Stage();
        URL path = this.getClass().getClassLoader().getResource("fxml/settingUI.fxml"); // 注意路径不带/开头
        File file = new File(path.getFile());
        settingUIRoot = FXMLLoader.load(file.toURI().toURL());

        this.ctx = ctx;
        settingDatas = new SettingDatas();
        initControl();

        stage.setTitle("new Stage");
        stage.setScene(new Scene(settingUIRoot, 640, 480));
        stage.show();
    }


    public void initControl() {

        SplitPane splitPane = (SplitPane) settingUIRoot.lookup("#splitwin");
        splitPane.getItems().forEach(e -> {
            if (e instanceof TabPane) {
                btnGccPathSelect = (Button) e.lookup("#tabBuild").lookup("#btnGccPathSelect");
                System.out.println(e);
            }
        });

        OpenCarWindos openCarWindos = ctx;

        btnGccPathSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //https://baijiahao.baidu.com/s?id=1719456091310997930&wfr=spider&for=pc
                DirectoryChooser directoryChooser = new DirectoryChooser();
                File selectedDirectory = directoryChooser.showDialog(stage);

                if (Objects.isNull(selectedDirectory)) return;
                //查找GCC相关的文件 需要注意linux和windonws的情况
                Optional<String> binPath = Arrays.stream(selectedDirectory.list()).filter(findFile -> {
                    return findFile.equals("riscv64-unknown-elf-objdump.exe");
                }).findFirst();

                if (!binPath.isPresent()) {
                    openCarWindos.setTxtAlertMessage("没有找到gcc编译组件...");
                    return;
                }

                openCarWindos.setTxtAlertMessage("选择完成");

                settingDatas.setGccBinPath(selectedDirectory.getPath());
//                String sl = settingDatas.getGccBinPath() + "\\riscv64-unknown-elf-objdump.exe";

            }
        });


    }


}
