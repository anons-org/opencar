package com.far.vms.opencar.ui.main.RightTablePanle;

import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.sax.SheetRidReader;
import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.protocol.debug.QuestData;
import com.far.vms.opencar.protocol.debug.QuestType;
import com.far.vms.opencar.protocol.debug.mode.QuestMemoryData;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabMemoryInfo {

//    TabPane tabMemory;

    TableView tvMemoryGrid;
    private OpenCarWindos ctx;

    TextField txtFindMemAddr;
    TextField txtFindMemUnit;

    //页面标签实例
    Tab tabInstance;


    ObservableList<Map<String, MemoryData.InnerMemVal>> tvMemoryGridDataWrapper = FXCollections.observableArrayList();


    public OpenCarWindos getCtx() {
        return ctx;
    }

    public void setCtx(OpenCarWindos ctx) {
        this.ctx = ctx;
    }

    public TableView getTvMemoryGrid() {
        return tvMemoryGrid;
    }

    public void setTvMemoryGrid(TableView tvMemoryGrid) {
        this.tvMemoryGrid = tvMemoryGrid;
    }

    public Tab getTabInstance() {
        return tabInstance;
    }

    public void setTabInstance(Tab tabInstance) {
        this.tabInstance = tabInstance;
    }


    //发送内存查看请求
    public void sendFindMemQuest() {

        ctx.getDchUtil().findMemData(
                HexUtil.hexToLong(txtFindMemAddr.getText()),
                Integer.valueOf(txtFindMemUnit.getText())
        );

    }

    public void bindControl() {
        setTvMemoryGrid((TableView) tabInstance.getContent().lookup("#tvMemoryGrid"));
        txtFindMemAddr = (TextField) tabInstance.getContent().lookup("#findMemAddr");
        txtFindMemUnit = (TextField) tabInstance.getContent().lookup("#findMemUnit");
    }

    public void initControl() {


//        tabInstance.setGraphic(getCircle());
//        tabInstance.getTabPane().setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent mouseEvent) {
//                tabInstance.getGraphic().setVisible(false);
//            }
//        });

        bindControl();

        tvMemoryGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        txtFindMemUnit.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    //发送信息给服务器
                    sendFindMemQuest();
                }
            }
        });

        txtFindMemAddr.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode() == KeyCode.ENTER) {
                    //发送信息给服务器
                    sendFindMemQuest();
                }
            }
        });


        String colName = "";
        for (int i = -1; i < 16; i++) {
            colName = i == -1 ? "offset" : String.valueOf(i);
            TableColumn<Map, String> tableColumn = new TableColumn<>(colName);
            if (i == -1) tableColumn.setPrefWidth(tvMemoryGrid.getPrefWidth() * 0.2);
            //  new MapValueFactory<>(colName);
            tableColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Map, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<Map, String> mapStringCellDataFeatures) {
                    MemoryData.InnerMemVal xxx = (MemoryData.InnerMemVal) mapStringCellDataFeatures.getValue().get(tableColumn.getText());
                    ObservableValue<String> s = new SimpleStringProperty(xxx.getViewVal());
                    return s;
                }
            });
            tvMemoryGrid.getColumns().add(tableColumn);
        }

        tvMemoryGrid.setItems(tvMemoryGridDataWrapper);


        Map<String, MemoryData.InnerMemVal> innerMemValMap = new HashMap<>();
        String key;
        for (int i = -1; i < 16; i++) {
            key = i == -1 ? "offset" : String.valueOf(i);
            MemoryData.InnerMemVal memVal = new MemoryData.InnerMemVal();
            memVal.setViewVal("xxx");
            innerMemValMap.put(key, memVal);
        }

        tvMemoryGridDataWrapper.add(innerMemValMap);
        tvMemoryGridDataWrapper.add(innerMemValMap);

    }

}
