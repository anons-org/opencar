package com.far.vms.opencar.ui.main.RightTablePanle;

import cn.hutool.core.util.StrUtil;
import com.far.vms.opencar.OpenCarWindos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RightTablePanle {
    private OpenCarWindos ctx;

    TabPane rightTabPane;

    //当前PC显示
    TextField txtPcVal;
    //通用寄存器
    TableView tvGeneralRegister;
    //特殊寄存器
    TableView tvCsrRegister;


    TabMemoryInfo tabMemoryInfo;
    Tab tabItemRegInfo ;

    //csr每列的数值显示格式
    Map<String, String> csrColFormat = new HashMap<>();
    //general每列的数值显示格式
    Map<String, String> generalColFormat = new HashMap<>();


    public static class FormatType {
        public static final String HEX = "hex";
        public static final String DEC = "dec";
        public static final String BIN = "bin";
    }

    public static class ColSelect {
        public static final int VIEW_ADDR = 1;
        public static final int VIEW_VAL = 2;
    }


    ObservableList<RegData> tvGeneralRegisterDataWrapper = FXCollections.observableArrayList();

    ObservableList<RegData> tvCsrRegisterDataWrapper = FXCollections.observableArrayList();


    public OpenCarWindos getCtx() {
        return ctx;
    }

    public RightTablePanle setCtx(OpenCarWindos ctx) {
        this.ctx = ctx;
        return this;
    }

    public TabPane getRightTabPane() {
        return rightTabPane;
    }

    public void setRightTabPane(TabPane rightTabPane) {
        this.rightTabPane = rightTabPane;
    }


    public Circle getCircle() {
        Color c = Color.rgb(199, 84, 80);
        Circle circle = new Circle();
        //Setting the properties of the circle
        circle.setCenterX(1.0f);
        circle.setCenterY(1.0f);
        circle.setRadius(4.0f);
        circle.setFill(c);
        //新圆形默认是隐藏的
        circle.setVisible(false);
        return circle;

    }

    public void initControl() {
        tabMemoryInfo = new TabMemoryInfo();
        SplitPane splitPane = (SplitPane) ctx.getRootMain().lookup("#spbox");//#ta是textarea的id号
        var scrollPane = splitPane.getItems();
        for (var e : scrollPane) {
            if (e instanceof ScrollPane) {
                ScrollPane scrollPane1 = ((ScrollPane) e);
                rightTabPane = (TabPane) ((AnchorPane) scrollPane1.getContent()).lookup("#tabRightGroup");
                rightTabPane.getTabs().forEach(item -> {

                    if ("tabItemRegInfo".equals(item.getId())) {

                        item.setGraphic(getCircle());
                        item.getTabPane().setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                item.getGraphic().setVisible(false);
                            }
                        });

                        tabItemRegInfo = item;
                        txtPcVal = (TextField) item.getContent().lookup("#txtPcVal");
                        tvGeneralRegister = (TableView) item.getContent().lookup("#tvGeneralRegister");
                        tvCsrRegister = (TableView) item.getContent().lookup("#tvCsrRegister");
                    } else if ("tabMemory".equals(item.getId())) {
                        tabMemoryInfo.setCtx(ctx);
                        tabMemoryInfo.setTabInstance(item);
                    }
                });
            }
        }


        initCopy(tvCsrRegister);
        initCopy(tvGeneralRegister);
        initGeneralRegisterTv();
        initCsrRegisterTv();

        tabMemoryInfo.initControl();

    }

    public void initCopy(TableView tableView) {
        //copy to clipboard
        tableView.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.C && e.isControlDown()) {
                StringBuilder clipboardString = new StringBuilder();
                //只能复制一个单元格的内容
                TablePosition tp = (TablePosition) tableView.getSelectionModel().getSelectedCells().get(0);
                RegData regData = (RegData) tableView.getItems().get(tp.getRow());
                final Clipboard clipboard = Clipboard.getSystemClipboard();
                final ClipboardContent content = new ClipboardContent();
                content.putString(regData.toString());
                clipboard.setContent(content);
            }
        });

    }

    /**
     * @param vv
     * @param type 1 addr 2 val
     * @description:
     * @return: void
     * @author mike/Fang.J
     * @data 2022/12/8
     */
    public void setColCopy(String vv, int type) {

        TablePosition tp = (TablePosition) tvGeneralRegister.getSelectionModel().getSelectedCells().get(0);
        RegData regData = (RegData) tvGeneralRegister.getItems().get(tp.getRow());
        if (type == 1) {
            regData.setViewAddr(vv);
        } else {
            regData.setViewVal(vv);
        }
    }

    public void setCsrColCopy(String vv, int type) {

        TablePosition tp = (TablePosition) tvCsrRegister.getSelectionModel().getSelectedCells().get(0);
        RegData regData = (RegData) tvCsrRegister.getItems().get(tp.getRow());
        if (type == 1) {
            regData.setViewAddr(vv);
        } else {
            regData.setViewVal(vv);
        }
    }


    public <T> String formmatData(long val, String type) {

        if (type.equals(FormatType.BIN)) {
            return "0b" + Long.toBinaryString(val);
        } else if (type.equals(FormatType.HEX)) {
            return "0x" + Long.toHexString(val);
        } else {
            return String.valueOf(val);
        }
    }

    public void initGeneralRegisterTv() {
        //创建表格的列
        TableColumn regName = new TableColumn<>("寄存器");
        TableColumn viewAddr = new TableColumn<>("地址");
        TableColumn viewVal = new TableColumn<>("值");


        viewAddr.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn tableColumn) {
                final TableCell<RegData, String> cell = new TableCell<>();
                //javafx的api非常奇怪，这行代码的作用是 将新的cell的值 和新的cell的值进行绑定
                //简单说 必须有这句，才会把值显示出来
                cell.textProperty().bind(cell.itemProperty());
                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            //获取选中单元格的值
                            var itemval = cell.getItem();
                            RegData regData = cell.getTableRow().getItem();
                            int xx = 1;
                            //设置菜单
                            //https://blog.csdn.net/qq_26486347/article/details/96600969
                            ContextMenu contextMenu = new ContextMenu();
                            // 菜单项
                            MenuItem hex = new MenuItem("十六进制");
                            hex.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    generalColFormat.put("viewAddr", FormatType.HEX);
                                    String vv = formmatData(regData.getAddr(), FormatType.HEX);
                                    cell.itemProperty().setValue(vv);
                                    setColCopy(vv, ColSelect.VIEW_ADDR);
                                }
                            });

                            MenuItem bin = new MenuItem("二进制");
                            bin.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    generalColFormat.put("viewAddr", FormatType.BIN);
                                    String vv = formmatData(regData.getAddr(), FormatType.BIN);
                                    cell.itemProperty().setValue(vv);
                                    setColCopy(vv, ColSelect.VIEW_ADDR);
                                }
                            });
                            MenuItem orc = new MenuItem("十进制");
                            orc.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    generalColFormat.put("viewAddr", FormatType.DEC);
                                    String vv = formmatData(regData.getAddr(), FormatType.DEC);
                                    cell.itemProperty().setValue(vv);
                                    setColCopy(vv, ColSelect.VIEW_ADDR);
                                }
                            });
                            contextMenu.getItems().addAll(hex, bin, orc);
                            cell.setContextMenu(contextMenu);
                        }
                    }
                });
                return cell;
            }
        });


        viewVal.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn tableColumn) {
                final TableCell<RegData, String> cell = new TableCell<>();
                //javafx的api非常奇怪，这行代码的作用是 将新的cell的值 和新的cell的值进行绑定
                //简单说 必须有这句，才会把值显示出来
                cell.textProperty().bind(cell.itemProperty());

                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            //获取选中单元格的值
                            var itemval = cell.getItem();
                            RegData regData = cell.getTableRow().getItem();
                            int xx = 1;
                            //设置菜单
                            //https://blog.csdn.net/qq_26486347/article/details/96600969
                            ContextMenu contextMenu = new ContextMenu();
                            // 菜单项
                            MenuItem hex = new MenuItem("十六进制");
                            hex.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    generalColFormat.put("viewVal", FormatType.HEX);
                                    String vv = formmatData(regData.getVal(), FormatType.HEX);
                                    cell.itemProperty().setValue(vv);
                                    setColCopy(vv, ColSelect.VIEW_VAL);
                                }
                            });

                            MenuItem bin = new MenuItem("二进制");
                            bin.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    generalColFormat.put("viewVal", FormatType.BIN);
                                    String vv = formmatData(regData.getVal(), FormatType.BIN);
                                    cell.itemProperty().setValue(vv);
                                    setColCopy(vv, ColSelect.VIEW_VAL);
                                }
                            });
                            MenuItem orc = new MenuItem("十进制");
                            orc.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    generalColFormat.put("viewVal", FormatType.DEC);
                                    String vv = formmatData(regData.getVal(), FormatType.DEC);
                                    cell.itemProperty().setValue(vv);
                                    setColCopy(vv, ColSelect.VIEW_VAL);
                                }
                            });
                            contextMenu.getItems().addAll(hex, bin, orc);
                            cell.setContextMenu(contextMenu);
                        }
                    }
                });
                return cell;
            }
        });


        //将表格的列和类的属性进行绑定
        regName.setCellValueFactory(new PropertyValueFactory<>("regName"));
        viewAddr.setCellValueFactory(new PropertyValueFactory<>("viewAddr"));
        viewVal.setCellValueFactory(new PropertyValueFactory<>("viewVal"));
        //添加到tableview
        tvGeneralRegister.getColumns().addAll(regName, viewAddr, viewVal);
        tvGeneralRegister.setItems(tvGeneralRegisterDataWrapper);
    }


    public void initCsrRegisterTv() {
        //创建表格的列
        TableColumn regName = new TableColumn<>("寄存器");
        TableColumn viewAddr = new TableColumn<>("地址");
        TableColumn viewVal = new TableColumn<>("值");


        viewAddr.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn tableColumn) {
                final TableCell<RegData, String> cell = new TableCell<>();
                //javafx的api非常奇怪，这行代码的作用是 将新的cell的值 和新的cell的值进行绑定
                //简单说 必须有这句，才会把值显示出来
                cell.textProperty().bind(cell.itemProperty());


                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            //获取选中单元格的值
                            var itemval = cell.getItem();
                            RegData regData = cell.getTableRow().getItem();
                            int xx = 1;
                            //设置菜单
                            //https://blog.csdn.net/qq_26486347/article/details/96600969
                            ContextMenu contextMenu = new ContextMenu();
                            // 菜单项
                            MenuItem hex = new MenuItem("十六进制");
                            hex.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {

                                    csrColFormat.put("viewAddr", FormatType.HEX);
                                    String vv = formmatData(regData.getAddr(), FormatType.HEX);
                                    cell.itemProperty().setValue(vv);
                                    setCsrColCopy(vv, ColSelect.VIEW_ADDR);


                                }
                            });

                            MenuItem bin = new MenuItem("二进制");
                            bin.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    csrColFormat.put("viewAddr", FormatType.BIN);
                                    String vv = formmatData(regData.getAddr(), FormatType.BIN);
                                    cell.itemProperty().setValue(vv);
                                    setCsrColCopy(vv, ColSelect.VIEW_ADDR);
                                }
                            });
                            MenuItem orc = new MenuItem("十进制");
                            orc.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    csrColFormat.put("viewAddr", FormatType.DEC);
                                    String vv = formmatData(regData.getAddr(), FormatType.DEC);
                                    cell.itemProperty().setValue(vv);
                                    setCsrColCopy(vv, ColSelect.VIEW_ADDR);
                                }
                            });
                            contextMenu.getItems().addAll(hex, bin, orc);
                            cell.setContextMenu(contextMenu);
                        }
                    }
                });
                return cell;
            }
        });


        viewVal.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn tableColumn) {
                final TableCell<RegData, String> cell = new TableCell<>();
                //javafx的api非常奇怪，这行代码的作用是 将新的cell的值 和新的cell的值进行绑定
                //简单说 必须有这句，才会把值显示出来
                cell.textProperty().bind(cell.itemProperty());

                cell.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {
                        if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                            //获取选中单元格的值
                            var itemval = cell.getItem();
                            RegData regData = cell.getTableRow().getItem();
                            int xx = 1;
                            //设置菜单
                            //https://blog.csdn.net/qq_26486347/article/details/96600969
                            ContextMenu contextMenu = new ContextMenu();
                            // 菜单项
                            MenuItem hex = new MenuItem("十六进制");
                            hex.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    csrColFormat.put("viewVal", FormatType.HEX);
                                    String vv = formmatData(regData.getVal(), FormatType.HEX);
                                    cell.itemProperty().setValue(vv);
                                    setCsrColCopy(vv, ColSelect.VIEW_VAL);
                                }
                            });

                            MenuItem bin = new MenuItem("二进制");
                            bin.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    csrColFormat.put("viewVal", FormatType.BIN);
                                    String vv = formmatData(regData.getVal(), FormatType.BIN);
                                    cell.itemProperty().setValue(vv);
                                    setCsrColCopy(vv, ColSelect.VIEW_VAL);
                                }
                            });
                            MenuItem orc = new MenuItem("十进制");
                            orc.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    csrColFormat.put("viewVal", FormatType.DEC);
                                    String vv = formmatData(regData.getVal(), FormatType.DEC);
                                    cell.itemProperty().setValue(vv);
                                    setCsrColCopy(vv, ColSelect.VIEW_VAL);
                                }
                            });
                            contextMenu.getItems().addAll(hex, bin, orc);
                            cell.setContextMenu(contextMenu);
                        }
                    }
                });
                return cell;
            }
        });

        //将表格的列和类的属性进行绑定
        regName.setCellValueFactory(new PropertyValueFactory<>("regName"));
        viewAddr.setCellValueFactory(new PropertyValueFactory<>("viewAddr"));
        viewVal.setCellValueFactory(new PropertyValueFactory<>("viewVal"));
        //添加到tableview
        tvCsrRegister.getColumns().addAll(regName, viewAddr, viewVal);
        tvCsrRegister.setItems(tvCsrRegisterDataWrapper);
    }


    public <T> void formatColOfRender(Map<String, String> map, RegData e) {
        if (!StrUtil.isEmpty(map.get("viewVal"))) {
            if (map.get("viewVal").equals(FormatType.HEX)) {
                e.setViewVal(formmatData(e.getVal(), FormatType.HEX));
            } else if (map.get("viewVal").equals(FormatType.BIN)) {
                e.setViewVal(formmatData(e.getVal(), FormatType.BIN));
            }
        }

        if (!StrUtil.isEmpty(map.get("viewAddr"))) {
            if (map.get("viewAddr").equals(FormatType.HEX)) {
                e.setViewAddr(formmatData(e.getAddr(), FormatType.HEX));
            } else if (map.get("viewAddr").equals(FormatType.BIN)) {
                e.setViewAddr(formmatData(e.getAddr(), FormatType.BIN));
            }
        }
    }

    public void addDataToGeneralRegisterTv(List<RegData> regData) {
        tvGeneralRegisterDataWrapper.clear();
        regData.forEach(e -> {
            formatColOfRender(generalColFormat, e);
            tvGeneralRegisterDataWrapper.add(e);
        });
    }


    public void addDataToCsrRegisterTv(List<RegData> regData) {
        tvCsrRegisterDataWrapper.clear();
        regData.forEach(e -> {
            formatColOfRender(csrColFormat, e);
            tvCsrRegisterDataWrapper.add(e);
        });
    }


    public void setPcval(String pc) {
        txtPcVal.setText(pc);
    }

}
