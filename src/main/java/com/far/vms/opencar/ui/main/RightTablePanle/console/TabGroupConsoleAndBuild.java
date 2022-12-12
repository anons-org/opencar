package com.far.vms.opencar.ui.main.RightTablePanle.console;

import com.far.vms.opencar.OpenCarWindos;
import com.far.vms.opencar.ui.event.Notify;
import com.far.vms.opencar.ui.event.NotifyType;
import javafx.application.Platform;
import javafx.scene.control.*;

public class TabGroupConsoleAndBuild {


    TextArea txtAreaConsole;
    TextArea txtAreaBuild;

    OpenCarWindos ctx;
    Tab tabInstance;

    public TextArea getTxtAreaConsole() {
        return txtAreaConsole;
    }

    public void setTxtAreaConsole(TextArea txtAreaConsole) {
        this.txtAreaConsole = txtAreaConsole;
    }

    public TextArea getTxtAreaBuild() {
        return txtAreaBuild;
    }

    public void setTxtAreaBuild(TextArea txtAreaBuild) {
        this.txtAreaBuild = txtAreaBuild;
    }

    public OpenCarWindos getCtx() {
        return ctx;
    }

    public void setCtx(OpenCarWindos ctx) {
        this.ctx = ctx;
    }

    public void setTabInstance(Tab tabInstance) {
        this.tabInstance = tabInstance;
    }

    public void bindControl() {
        txtAreaConsole = (TextArea) tabInstance.getContent().lookup("#txtSys");

        Notify.on(NotifyType.UI.APPEND_CONSOLE_TXT, new Notify.INotifyHandler<String>() {
            @Override
            public void onEvent(int evtType, Notify.NotifyData<?> data) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        String dd = (String) data.getData();
                        txtAreaConsole.appendText(dd + "\n");
                    }
                });
            }
        });


    }

    public void initControl() {
        bindControl();
    }
}
