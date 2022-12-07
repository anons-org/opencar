package com.far.vms.opencar.ui.main.RightTablePanle;

import com.far.vms.opencar.OpenCarWindos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

import java.util.List;

public class RightTablePanle {
    private OpenCarWindos ctx;

    TabPane rightTabPane;

    //当前PC显示
    TextField txtPcVal;
    //通用寄存器
    TableView tvGeneralRegister;

    ObservableList<RegData> tvGeneralRegisterDataWrapper = FXCollections.observableArrayList();


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

    public void initControl() {

        SplitPane splitPane = (SplitPane) ctx.getRootMain().lookup("#spbox");//#ta是textarea的id号

        var scrollPane = splitPane.getItems();
        for (var e : scrollPane) {
            if (e instanceof ScrollPane) {
                ScrollPane scrollPane1 = ((ScrollPane) e);
                rightTabPane = (TabPane) ((AnchorPane) scrollPane1.getContent()).lookup("#tabRightGroup");
                rightTabPane.getTabs().forEach(item -> {
                    if ("tabItemRegInfo".equals(item.getId())) {
                        txtPcVal = (TextField) item.getContent().lookup("#txtPcVal");
                        tvGeneralRegister = (TableView) item.getContent().lookup("#tvGeneralRegister");
                    }
                });
            }
        }
        initGeneralRegisterTv();
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

//                cell.setOnKeyPressed(new EventHandler<KeyEvent>() {
//                    @Override
//                    public void handle(KeyEvent keyEvent) {
//
//                        if (keyEvent.getCode() == KeyCode.C) {
//                            // final Clipboard clipboard = Clipboard.getSystemClipboard();
//                            final ClipboardContent content = new ClipboardContent();
//                            content.putString(cell.getItem());
//                        }
//                    }
//                });

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
                                    cell.itemProperty().setValue("0x" + Long.toHexString(regData.getAddr()));
                                }
                            });

                            MenuItem bin = new MenuItem("二进制");
                            bin.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    cell.itemProperty().setValue("0b" + Long.toBinaryString(regData.getAddr()));
                                }
                            });
                            MenuItem orc = new MenuItem("十进制");
                            orc.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    cell.itemProperty().setValue(String.valueOf(regData.getAddr()));
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
                                    cell.itemProperty().setValue(regData.toHexStr());
                                }
                            });

                            MenuItem bin = new MenuItem("二进制");
                            bin.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    cell.itemProperty().setValue(regData.toBinStr());
                                }
                            });
                            MenuItem orc = new MenuItem("十进制");
                            orc.setOnAction(new EventHandler<ActionEvent>() {
                                @Override
                                public void handle(ActionEvent event) {
                                    cell.itemProperty().setValue(String.valueOf(regData.getVal()));
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

    public void addDataToGeneralRegisterTv(List<RegData> regData) {
        tvGeneralRegisterDataWrapper.clear();
        regData.forEach(e -> {
            tvGeneralRegisterDataWrapper.add(e);
        });
    }


    public void setPcval(String pc) {
        txtPcVal.setText(pc);
    }

}
