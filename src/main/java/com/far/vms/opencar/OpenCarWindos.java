package com.far.vms.opencar;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.far.vms.opencar.complier.Parser;
import com.far.vms.opencar.debugger.server.DServer;
import com.far.vms.opencar.protocol.debug.mode.QuestPcBreak;
import com.far.vms.opencar.ui.entity.SettingDatas;
import com.far.vms.opencar.ui.main.DebugBtns;
import com.far.vms.opencar.ui.main.RightTablePanle.RegData;
import com.far.vms.opencar.ui.main.RightTablePanle.RightTablePanle;
import com.far.vms.opencar.ui.main.TopToolBar;
import com.far.vms.opencar.ui.DchUtil;
import com.far.vms.opencar.utils.EnvUtil;
import com.far.vms.opencar.ui.ShellUtil;
import com.far.vms.opencar.utils.exception.FarException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class OpenCarWindos extends Application {


    Button btnBinSelect = null;
    Button btnGccPath = null;


    TextArea buildConsole = null;
    TextArea sysConsole = null;

    //底部信息提示
    Label txtAlertMessage;


    private TopToolBar topToolBar;

    //调式按钮区域
    private DebugBtns debugBtns;


    /**
     * @description: 调试工具
     * @param null
     * @return:
     * @author mike/Fang.J
     * @data 2022/12/3
     */
    private DchUtil dchUtil;


    /**
     * 用于存储设置
     *
     * @data 2022/12/3
     */
    private SettingDatas settingDatas;


    //代码行编号
    private short codeLineNumber = 1;


    private Parent rootMain;


    Stage primaryStage;


    TableView tvCodeEditor = null;

    //右边TAB
    RightTablePanle rightTablePanle;

    public RightTablePanle getRightTablePanle() {
        return rightTablePanle;
    }

    public void setRightTablePanle(RightTablePanle rightTablePanle) {
        this.rightTablePanle = rightTablePanle;
    }

    /**
     * @description: 用于代码和行的映射关系
     * @param null
     * @return:
     * @author mike/Fang.J
     * @data 2022/12/5
     */
    Map<String, Short> codeLineRef = new HashMap<>();


    private List<QuestPcBreak> pcBreakList = new ArrayList<>();


    ObservableList<CodeData> editorData = FXCollections.observableArrayList();


    public DchUtil getDchUtil() {
        return dchUtil;
    }

    public void setDchUtil(DchUtil dchUtil) {
        this.dchUtil = dchUtil;
    }


    public Map<String, Short> getCodeLineRef() {
        return codeLineRef;
    }

    public void setCodeLineRef(Map<String, Short> codeLineRef) {
        this.codeLineRef = codeLineRef;
    }

    public TableView getTvCodeEditor() {
        return tvCodeEditor;
    }

    public void setTvCodeEditor(TableView tvCodeEditor) {
        this.tvCodeEditor = tvCodeEditor;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }


    public DebugBtns getDebugBtns() {
        return debugBtns;
    }

    public void setDebugBtns(DebugBtns debugBtns) {
        this.debugBtns = debugBtns;
    }

    public Parent getRootMain() {
        return rootMain;
    }

    public void setRootMain(Parent rootMain) {
        this.rootMain = rootMain;
    }

    public SettingDatas getSettingDatas() {
        return settingDatas;
    }

    public void setSettingDatas(SettingDatas settingDatas) {
        this.settingDatas = settingDatas;
    }

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
//        buildConsole = (TextArea) tabGroupConsole.lookup("#txtBuild");
//        sysConsole = (TextArea) tabGroupConsole.lookup("#txtSys");
    }


    @Override
    public void start(Stage primaryStage) {

        primaryStage = primaryStage;

        EnvUtil.initEvn();

        EnvUtil.loadConf(this);


        DServer.startDserver();


        Thread sThread = new Thread(() -> {
            try {
                //等待server启动完成
                while (true) {
                    if (DServer.farSockServer.isStartComplete()) break;
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                throw new FarException(FarException.Code.CRASH, "等待启动时异常", e);
            }
        });
        sThread.start();
        try {
            sThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        InputStream is = this.getClass().getResourceAsStream("/fxml/main.fxml");
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        String s = "";
//        try {
//            while ((s = br.readLine()) != null)
//                System.out.println(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//       URL path = this.getClass().getClassLoader().getResource("fxml/main.fxml"); // 注意路径不带/开头
//        var tttt =path.getFile();


        String mainfxml = EnvUtil.appAtPath + "/config/fxml/main.fxml";

        try {
            File file = new File(mainfxml);
            rootMain = FXMLLoader.load(file.toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        }


        topToolBar = new TopToolBar();

        topToolBar.setCtx(this).initControl();

        rightTablePanle = new RightTablePanle();
        rightTablePanle.setCtx(this).initControl();


        debugBtns = new DebugBtns();
        debugBtns.setCtx(this).initControl();


        VBox vBox = (VBox) rootMain.lookup("#vbox");//#ta是textarea的id号

        vBox.getChildren().forEach(e -> {
            if (e instanceof HBox) {
                txtAlertMessage = (Label) e.lookup("#txtAlertMssage");
            }
        });


        SplitPane splitPane = (SplitPane) rootMain.lookup("#spbox");//#ta是textarea的id号
        var scrollPane = splitPane.getItems();


        for (var e : scrollPane) {
            if (e instanceof ScrollPane) {
                ScrollPane scrollPane1 = ((ScrollPane) e);

//                btnBinSelect = (Button) ((AnchorPane) scrollPane1.getContent()).lookup("#btnBinSelect");
//                btnGccPath = (Button) ((AnchorPane) scrollPane1.getContent()).lookup("#btnGccPath");
                //   rightTabPane = (TabPane) ((AnchorPane) scrollPane1.getContent()).lookup("#tabRightGroup");
                int x = 1;
            } else if (e instanceof AnchorPane) {
                tvCodeEditor = (TableView) e.lookup("#text-edit");
                int xx = 1;
            }
        }


//        initSelectFileGroup(primaryStage);
//
//        initTabGroupConsole();


        TableView finalTv = tvCodeEditor;
        //隐藏表头
        tvCodeEditor.widthProperty().addListener(new ChangeListener<Number>() {
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
        tvCodeEditor.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        //设置点击方法
        tvCodeEditor.setRowFactory(tview -> {
            TableRow<CodeData> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                //此处只能选择一个 也就是当前选中的第一个单元格
                TablePosition tp = (TablePosition) tvCodeEditor.getSelectionModel().getSelectedCells().get(0);
                //只处理 断点标记的列
                if( tp.getColumn()!=1 ){
                    return;
                }

                if (event.getClickCount() % 1 == 0 && (!row.isEmpty())) {
                    CodeData rowData = row.getItem();
                    if (!Parser.canBreakForCode(rowData.getCodeLine())) {
                        setTxtAlertMessage("此处不能设置断点,因为该行代码不是指令");
                        //清空
                        Parser.getCodeForPc();
                        return;
                    }

                    String pc = "";
                    if (rowData.getCircle().isVisible()) {
                        //删除断点
                        rowData.getCircle().setVisible(false);
                        pc = Parser.getCodeForPc();
                        if (this.getDchUtil() != null) {
                            this.getDchUtil().removePcBreakLine(pc, row.getIndex() + 1);
                        } else {
                            String finalPc = pc;
                            this.pcBreakList.removeIf(v -> {
                                return v.getPc().equals(finalPc);
                            });
                        }
                    } else {

                        rowData.getCircle().setVisible(true);
                        pc = Parser.getCodeForPc();

                        if (this.getDchUtil() != null) {
                            this.getDchUtil().addPcBreakLine(pc, row.getIndex() + 1);
                        } else {
                            QuestPcBreak questPcBreak = new QuestPcBreak();
                            questPcBreak.setLine(row.getIndex() + 1);
                            questPcBreak.setPc(pc);
                            this.pcBreakList.add(questPcBreak);
                        }
                    }
                }
            });
            return row;
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
        tvCodeEditor.getColumns().addAll(lineNum, lineCol, codeCol);
        tvCodeEditor.setItems(editorData);
        lineNum.setPrefWidth(tvCodeEditor.getPrefWidth() * 0.05);
        lineCol.setPrefWidth(tvCodeEditor.getPrefWidth() * 0.02);
        codeCol.setPrefWidth(tvCodeEditor.getPrefWidth() * 1.2);

        final OpenCarWindos self = this;

//        lineCol.setCellFactory(new Callback<TableColumn, TableCell>() {
//            @Override
//            public TableCell call(TableColumn tableColumn) {
//                final TableCell<CodeData, Circle> cell = new TableCell<>();
//
//
//                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                    @Override
//                    public void handle(MouseEvent mouseEvent) {
//                        TableRow<CodeData> rowData = cell.getTableRow();
//                        CodeData codeData = rowData.getItem();
//                        codeData.getCircle().setVisible(true);
//                        cell.itemProperty().set(codeData.getCircle());
//                        cell.itemProperty().setValue(codeData.getCircle());
//                        cell.getTableRow().getItem().setCircle(codeData.getCircle());
//
//                        tvCodeEditor.getSelectionModel().getSelectedCells().forEach(e->{
//                            TablePosition tp = (TablePosition) e;
//                            int x,y;
//                            x = tp.getRow();
//                            y = tp.getColumn();
//
//                            CodeData xxx = (CodeData) tvCodeEditor.getSelectionModel().getSelectedItem();
//                            xxx.getCircle().setVisible(true);
//                            System.out.println(e);
//                        });
//
//
//
//
//
//                        if (!Parser.canBreakForCode(rowData.getItem().getCodeLine())) {
//                            setTxtAlertMessage("此处不能设置断点,因为该行代码不是指令");
//                            //清空
//                            Parser.getCodeForPc();
//                            return;
//                        }
//
//                        String pc = "";
//                        if (codeData.getCircle().isVisible()) {
//                            //删除断点
//                            codeData.getCircle().setVisible(false);
//                            pc = Parser.getCodeForPc();
//                            if (self.getDchUtil() != null) {
//                                self.getDchUtil().removePcBreakLine(pc, rowData.getIndex() + 1);
//                            } else {
//                                String finalPc = pc;
//                                self.pcBreakList.removeIf(v -> {
//                                    return v.getPc().equals(finalPc);
//                                });
//                            }
//                        } else {
//
//                            codeData.getCircle().setVisible(true);
//                            pc = Parser.getCodeForPc();
//
//                            if (self.getDchUtil() != null) {
//                                self.getDchUtil().addPcBreakLine(pc, rowData.getIndex() + 1);
//                            } else {
//                                QuestPcBreak questPcBreak = new QuestPcBreak();
//                                questPcBreak.setLine(rowData.getIndex() + 1);
//                                questPcBreak.setPc(pc);
//                                self.pcBreakList.add(questPcBreak);
//                            }
//                        }
//                        cell.itemProperty().setValue(codeData.getCircle());
//                        cell.updateSelected(true);
//
//                    }
//                });
//
//                return cell;
//            }
//        });


        //  var ok = tvCodeEditor.getColumns().size();


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
        primaryStage.setScene(new Scene(rootMain, 1440, 840));

        KeyCombination ctrl_c = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
        KeyCombination kb_f6 = new KeyCodeCombination(KeyCode.F6);
        KeyCombination kb_f5 = new KeyCodeCombination(KeyCode.F5);
        primaryStage.getScene().getAccelerators().put(kb_f6, () -> {
            keyF6();
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


    public void keyF6() {
        this.getDchUtil().step();
        System.out.println("快捷键F6");
        System.out.println(Thread.currentThread().getName());
    }


    public void testSim() {
        this.getDchUtil().test();
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


    public void setTxtAlertMessage(String message) {
        this.txtAlertMessage.setText(message);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        txtAlertMessage.setText("");
                    }
                });
            }
        }, 2600);

    }

    /**
     * @param
     * @description: 保存配置
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/1
     */
    public void saveSettingData() {
        String filePath = EnvUtil.appAtPath + "/config/setting.json";
        String data = JSONUtil.toJsonStr(settingDatas);
        FileUtil.writeString(data, filePath, StandardCharsets.UTF_8);
    }


    public boolean addCode(String filePath) {

        if (!EnvUtil.hasGccPath(this)) {
            setTxtAlertMessage("需要先设置gcc的目录!");
            return false;
        }

        String buildTool = settingDatas.getBuild().getGccPath() + "\\riscv64-unknown-elf-objdump.exe";

        ShellUtil.runShell(buildTool, new String[]{buildTool, filePath, "-d"}, cmdInf -> {

            short line = getCodeLineNumber();

            //通过此处来处理 PC和行的关系
            if (Parser.canBreakForCode(cmdInf)) {
                String pc = Parser.getCodeForPc();
                codeLineRef.put(pc, line);
            }
            editorData.add(new CodeData(line, getCircle(), cmdInf));
            //  System.out.println(cmdInf);
            return 0;
        });

        return true;

    }


    public String buildElfToBin() {


        if (Objects.isNull(settingDatas) || Objects.isNull(settingDatas.getBuild())) {
            setTxtAlertMessage("还没有设置编译配置!");
            return "";
        }


        if (StrUtil.isEmpty(settingDatas.getBuild().getProgFilePath())) {
            setTxtAlertMessage("需要先选择调式程序!");
            return "";
        }


        String buildTool = settingDatas.getBuild().getGccPath() + "\\riscv64-unknown-elf-objcopy.exe";
        String elfFile = settingDatas.getBuild().getProgFilePath() + "/" + settingDatas.getBuild().getProgName() + "." + settingDatas.getBuild().getProgSufix();
        String outFile = settingDatas.getBuild().getProgFilePath() + "/" + settingDatas.getBuild().getProgName() + ".bin";


        ShellUtil.runShell(buildTool, new String[]{buildTool,
                "-I",
                "elf64-littleriscv",
                elfFile,
                "-g",
                "-S",
                "-O",
                "binary",
                outFile

        }, cmdInf -> {
            System.out.println(cmdInf);
            return 0;
        });

        setTxtAlertMessage("bin编译完成!");
        return outFile;

    }

    /**
     * @param
     * @description: 由于模拟器是个无限循环，所以需要在线程中开启模拟器
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/4
     */
    public void startSimulator(final String progFile) {
        System.out.println("连接调式器服务端....");
        setDchUtil(DchUtil.create(this));

        System.out.println("连接调式器服务端 ok....");
        Thread simulatorThread = new Thread(() -> {
            OpenCarApplication.run(progFile);
        });
        simulatorThread.setName("startSimulator");
        simulatorThread.start();


        //发送没启动模拟器之前的断点信息
        this.pcBreakList.forEach(e -> {
            dchUtil.addPcBreakLine(e.getPc(), e.getLine());
        });


    }


    public void codeEditorScrollTo(int i) {
        tvCodeEditor.scrollTo(i);
    }


}
