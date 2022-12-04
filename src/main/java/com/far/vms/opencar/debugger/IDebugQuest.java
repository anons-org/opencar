package com.far.vms.opencar.debugger;

import com.far.net.interf.IProcessAgent;
import com.far.vms.opencar.board.Cpu;
import com.far.vms.opencar.debugger.server.SessionManager;

public interface IDebugQuest {

    //寄存器写入事件
    public void onDebugRequest(String msg, IProcessAgent<SessionManager.SessionAgent> sessionAgent);

    public IDebuger getDebugger() ;
}
