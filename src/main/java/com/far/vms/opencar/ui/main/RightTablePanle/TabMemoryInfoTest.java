package com.far.vms.opencar.ui.main.RightTablePanle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;

public class TabMemoryInfoTest {

    TabPane tabMemory;

    TableView tvMemoryGrid;

    ObservableList<Map<String, MemoryData.InnerMemVal>> tvMemoryGridDataWrapper = FXCollections.observableArrayList();

    public TabPane getTabMemory() {
        return tabMemory;
    }

    public void setTabMemory(TabPane tabMemory) {
        this.tabMemory = tabMemory;
    }

    public TableView getTvMemoryGrid() {
        return tvMemoryGrid;
    }

    public void setTvMemoryGrid(TableView tvMemoryGrid) {
        this.tvMemoryGrid = tvMemoryGrid;
    }


    public void initControl() {
        tvMemoryGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
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
