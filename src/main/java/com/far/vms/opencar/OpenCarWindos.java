package com.far.vms.opencar;

import com.far.vms.opencar.complier.Parser;
import com.far.vms.opencar.ui.SettingDatas;
import com.far.vms.opencar.utils.ShellUtil;
import com.sun.javafx.event.EventDispatchChainImpl;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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


    private short codeLineNumber = 1;

    public class CodeData {

        private short lineNum;
        private Circle circle;
        private String codeLine;

        public CodeData(short lineNum, Circle circle, String codeLine) {
            this.circle = circle;
            this.codeLine = codeLine;
            this.lineNum = lineNum;
        }

        public short getLineNum() {
            return lineNum;
        }

        public void setLineNum(short lineNum) {
            this.lineNum = lineNum;
        }

        public Circle getCircle() {
            return circle;
        }

        public void setCircle(Circle circle) {
            this.circle = circle;
        }

        public String getCodeLine() {
            return codeLine;
        }

        public void setCodeLine(String codeLine) {
            this.codeLine = codeLine;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }

    public short getCodeLineNumber() {
        return this.codeLineNumber++;
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


//
//                btnBinSelect = (Button) ((AnchorPane) scrollPane1.getContent()).lookup("#btnBinSelect");
//                btnGccPath = (Button) ((AnchorPane) scrollPane1.getContent()).lookup("#btnGccPath");
//                tabGroupConsole = (TabPane) ((AnchorPane) scrollPane1.getContent()).lookup("#groupConsole");
            } else if (e instanceof AnchorPane) {
                tv = (TableView) e.lookup("#text-edit");
                int xx = 1;

            }
        }


//        initSelectFileGroup(primaryStage);
//
//        initTabGroupConsole();

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
        //设置为只能单选
        tv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //设置点击方法


        tv.setRowFactory(tview -> {
            TableRow<CodeData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                //  System.out.println(event.getClickCount() % 2 + "次");
                // 点击两次 且 row不为空
//                ObservableList<TablePosition> cells = finalTv1.getSelectionModel().;
//                for (TablePosition<?, ?> cell : cells) {
//                    System.out.println(cell.getColumn());
//                }// for


                if (event.getClickCount() % 1 == 0 && (!row.isEmpty())) {
                    CodeData rowData = row.getItem();

                    if (!Parser.canBreakForCode(rowData.getCodeLine())) {
                        System.out.println("这数据不能下断点!" + rowData.getCodeLine());
                        return;
                    }

                    if (rowData.getCircle().isVisible()) {
                        rowData.getCircle().setVisible(false);
                    } else {
                        rowData.getCircle().setVisible(true);
                    }
                }
            });
            return row;
        });


        ObservableList<CodeData> data = FXCollections.observableArrayList();
        String kr = "D:\\AAAA_WORK\\RISC-V-Tools\\os\\riscv-operating-system-mooc\\code\\os\\01-helloRVOS\\build\\kernel-img.img";
        String buildTool = "D:\\AAAA_WORK\\RISC-V-Tools\\riscv64-unknown-elf-toolchain-10.2.0-2020.12.8-x86_64-w64-mingw32\\bin\\riscv64-unknown-elf-objdump.exe";


        ShellUtil.runShell(buildTool, new String[]{buildTool, kr, "-d"}, cmdInf -> {
            data.add(new CodeData(getCodeLineNumber(), getCircle(), cmdInf));
            //  System.out.println(cmdInf);
            return 0;
        });


        // https://blog.csdn.net/m0_58015306/article/details/123033003
        //创建表格的列
        TableColumn lineNum = new TableColumn<>("lineNum");
        TableColumn lineCol = new TableColumn<>("circle");
        TableColumn codeCol = new TableColumn<>("codeLine");


        //将表格的列和类的属性进行绑定

        lineNum.setCellValueFactory(new PropertyValueFactory<>("lineNum"));
        lineCol.setCellValueFactory(new PropertyValueFactory<>("circle"));
        codeCol.setCellValueFactory(new PropertyValueFactory<>("codeLine"));
        //添加到tableview
        tv.getColumns().addAll(lineNum, lineCol, codeCol);
        tv.setItems(data);
        lineNum.setPrefWidth(tv.getPrefWidth() * 0.05);
        lineCol.setPrefWidth(tv.getPrefWidth() * 0.02);
        codeCol.setPrefWidth(tv.getPrefWidth() * 1.2);
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


        //禁止调整窗口大小
        primaryStage.setResizable(false);
        primaryStage.setTitle("opencar riscv64 emulation - dev version");
        primaryStage.setScene(new Scene(root, 1280, 840));

        KeyCombination ctrl_c = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        KeyCombination kb_f6 = new KeyCodeCombination(KeyCode.F6);
        KeyCombination kb_f5 = new KeyCodeCombination(KeyCode.F5);
        primaryStage.getScene().getAccelerators().put(kb_f6, () -> {
            System.out.println("快捷键F6");
            System.out.println(Thread.currentThread().getName());
        });

        primaryStage.getScene().getAccelerators().put(kb_f5, () -> {
            System.out.println("快捷键F5");
            System.out.println(Thread.currentThread().getName());
        });



        //primaryStage.setMaximized(true);
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

    public Circle getCircle() {
        Color c = Color.rgb(199, 84, 80);
        Circle circle = new Circle();
        //Setting the properties of the circle
        circle.setCenterX(1.0f);
        circle.setCenterY(1.0f);
        circle.setRadius(5.0f);
        circle.setFill(c);
        //新圆形默认是隐藏的
        circle.setVisible(false);
        return circle;

    }
}
