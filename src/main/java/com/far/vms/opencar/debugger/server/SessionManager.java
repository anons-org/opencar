package com.far.vms.opencar.debugger.server;

import com.far.net.interf.IProcessAgent;
import com.far.net.server.session.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionManager {


    private Map<String, SessionAgent> sessionAgentMap = new HashMap<>();

    public void addSeesion(Session session) {
        SessionAgent sessionAgent = new SessionAgent();
        sessionAgent.sessionId = session.getSeesionId();
        sessionAgent.setSession(session);
        sessionAgentMap.put(session.getSeesionId(), sessionAgent);
    }

    public SessionAgent getSesssionAgent(String sessionId) {
        Optional<SessionAgent> sessionAgent = sessionAgentMap.entrySet().stream().filter((entry) -> {
            return entry.getKey().equals(sessionId);
        }).map(entry -> entry.getValue()).findFirst();
        return sessionAgent.get();
    }

    public void removeSessionAgent(String sessionId) {
        sessionAgentMap.remove(sessionId);
    }


    public static class SessionAgent implements IProcessAgent<SessionAgent> {
        //ide的session
        private String ideSessionId;
        //连接用的
        private String sessionId;

        private Session session;


        public Session getSession() {
            return session;
        }

        @Override
        public String getSessionId() {
            return sessionId;
        }

        @Override
        public IProcessAgent<SessionAgent> setSession(Session e) {
            this.session = e;
            return this;
        }


        @Override
        public void sendMessage(String message) {
            session.sendBytes(message);
        }

        @Override
        public SessionAgent getVisitor() {
            return null;
        }


        public String getIdeSessionId() {
            return ideSessionId;
        }

        public void setIdeSessionId(String ideSessionId) {
            this.ideSessionId = ideSessionId;
        }


        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
    }


}
