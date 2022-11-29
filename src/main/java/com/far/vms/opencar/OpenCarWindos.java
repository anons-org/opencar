package com.far.vms.opencar;

import com.far.vms.opencar.ui.SettingDatas;
import com.far.vms.opencar.utils.ShellUtil;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.web.HTMLEditor;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class OpenCarWindos extends Application {


    Button btnBinSelect = null;
    Button btnGccPath = null;
    //控制台输出区域
    TabPane tabGroupConsole = null;

    TextArea buildConsole = null;

    TextArea sysConsole = null;


    private SettingDatas settingDatas;


    public class CodeData {


        private String line;


        private String codeLine;


        public CodeData(String line, String codeLine) {
            this.line = line;
            this.codeLine = codeLine;
        }


        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getCodeLine() {
            return codeLine;
        }

        public void setCodeLine(String codeLine) {
            this.codeLine = codeLine;
        }
    }


    public static void main(String[] args) {

        String kr = "D:\\AAAA_WORK\\RISC-V-Tools\\os\\riscv-operating-system-mooc\\code\\os\\01-helloRVOS\\build\\kernel-img.img";
        String buildTool = "D:\\AAAA_WORK\\RISC-V-Tools\\riscv64-unknown-elf-toolchain-10.2.0-2020.12.8-x86_64-w64-mingw32\\bin\\riscv64-unknown-elf-objdump.exe";

        ShellUtil.runShell(buildTool, new String[]{buildTool,kr ,"-d"}, cmdInf -> {
            System.out.println(cmdInf);
            return 0;
        });

        launch(args);
    }


    public void initTabGroupConsole() {
        buildConsole = (TextArea) tabGroupConsole.lookup("#txtBuild");
        sysConsole = (TextArea) tabGroupConsole.lookup("#txtSys");
    }


    public void initSelectFileGroup(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("image Files", "*.img")
//                ,new FileChooser.ExtensionFilter("HTML Files", "*.htm")
        );


        btnBinSelect.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            settingDatas.setProgBinFile(selectedFile.getPath());
        });

        //https://baijiahao.baidu.com/s?id=1719456091310997930&wfr=spider&for=pc
        DirectoryChooser directoryChooser = new DirectoryChooser();

        btnGccPath.setOnAction(e -> {
            File selectedDirectory = directoryChooser.showDialog(primaryStage);

            if (Objects.isNull(selectedDirectory)) return;
            //查找GCC相关的文件 需要注意linux和windonws的情况
            Optional<String> binPath = Arrays.stream(selectedDirectory.list()).filter(findFile -> {
                return findFile.equals("riscv64-unknown-elf-objdump.exe");
            }).findFirst();

            if (!binPath.isPresent()) {
                sysConsole.appendText("没有找到gcc的bin目录!");
                return;
            }

            settingDatas.setGccBinPath(selectedDirectory.getPath());
            String sl = settingDatas.getGccBinPath() + "\\riscv64-unknown-elf-objdump.exe";


//            for (String filer : selectedDirectory.list()) {
//
//
//                sysConsole.appendText(filer + "\n");
//            }
        });
    }


    @Override
    public void start(Stage primaryStage) {

        Parent root = null;
        try {
            File file = new File("C:\\Users\\mike\\Desktop\\jfx-view\\main.fxml");
            root = FXMLLoader.load(file.toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }

        VBox vBox = (VBox) root.lookup("#vbox");//#ta是textarea的id号
        SplitPane splitPane = (SplitPane) root.lookup("#spbox");//#ta是textarea的id号
        var scrollPane = splitPane.getItems();
        TableView tv = null;


        for (var e : scrollPane) {
            if (e instanceof ScrollPane) {
                ScrollPane scrollPane1 = ((ScrollPane) e);
                tv = (TableView) ((AnchorPane) scrollPane1.getContent()).lookup("#text-edit");
                btnBinSelect = (Button) ((AnchorPane) scrollPane1.getContent()).lookup("#btnBinSelect");
                btnGccPath = (Button) ((AnchorPane) scrollPane1.getContent()).lookup("#btnGccPath");
                tabGroupConsole = (TabPane) ((AnchorPane) scrollPane1.getContent()).lookup("#groupConsole");
            }
        }


        initSelectFileGroup(primaryStage);

        initTabGroupConsole();


        settingDatas = new SettingDatas();


        TableView finalTv = tv;
        tv.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                // Get the table header
                Pane header = (Pane) finalTv.lookup("TableHeaderRow");
                if (header != null && header.isVisible()) {
                    header.setMaxHeight(0);
                    header.setMinHeight(0);
                    header.setPrefHeight(0);
                    header.setVisible(false);
                    header.setManaged(false);
                }
            }
        });


        ObservableList<CodeData> data = FXCollections.observableArrayList(
                new CodeData("1", "sd t0,(1)a0"),
                new CodeData("2", "ld t0,(1)a0")
        );

        // https://blog.csdn.net/m0_58015306/article/details/123033003
        //创建表格的列
        TableColumn<String, String> lineCol = new TableColumn<>("line");
        TableColumn<String, String> codeCol = new TableColumn<>("codeLine");

        //将表格的列和类的属性进行绑定
        lineCol.setCellValueFactory(new PropertyValueFactory<>("line"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("codeLine"));
        //添加到tableview
        tv.getColumns().addAll(lineCol, codeCol);
        tv.setItems(data);
        lineCol.setPrefWidth(tv.getPrefWidth() * 0.05);
        codeCol.setPrefWidth(tv.getPrefWidth() * 0.6);
        var ok = tv.getColumns().size();


        //自定义列的数据格式？？

//        codeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
//
//            @Override
//            public ObservableValue<String> call(TableColumn.CellDataFeatures<String, String> param) {
//                // TODO Auto-generated method stub
//
//                SimpleStringProperty simpleStringProperty = new SimpleStringProperty(param.getValue());
//                return simpleStringProperty;
//            }
//        });


//        Pane header = (Pane)tv.lookup("")
//        if(header!=null && header.isVisible()) {
//            header.setMaxHeight(0);
//            header.setMinHeight(0);
//            header.setPrefHeight(0);
//            header.setVisible(false);
//            header.setManaged(false);
//        }

        //禁止调整窗口大小
        primaryStage.setResizable(false);
        primaryStage.setTitle("opencar riscv64 emulation - dev version");
        primaryStage.setScene(new Scene(root, 1280, 768));
        primaryStage.show();

//
//
//
//        primaryStage.setTitle("Hello World!");
//        Button btn = new Button();
//        btn.setText("Say 'Hello World'");
//        btn.setOnAction(new EventHandler<ActionEvent>() {
//
//            @Override
//            public void handle(ActionEvent event) {
//                System.out.println("Hello World!");
//            }
//        });
//
//        StackPane root = new StackPane();
//        root.getChildren().add(btn);
//        primaryStage.setScene(new Scene(root, 300, 250));
//        primaryStage.show();
    }
}
