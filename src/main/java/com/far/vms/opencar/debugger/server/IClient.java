package com.far.vms.opencar.debugger.server;

import com.far.net.interf.IProcessAgent;



public interface IClient {

    public void onRequest(int type, String msg, IProcessAgent<SessionManager.SessionAgent> sessionAgent);


}
