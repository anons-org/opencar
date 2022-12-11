package com.far.vms.opencar.ui.event;

import com.far.vms.opencar.ui.entity.SettingDatas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @description: 同步事件
 * @author mike/Fang.J
 * @data 2022/12/11
 */
public class Event {


    public static interface IEventHandler {
        public <T> void onEvent(short evtType, EventData<T> data);
    }

    public static class EventData<T> {
        private T data;

        public EventData(T d){
            this.data = d;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }


    private static List<Map<Short, IEventHandler>> handlerLists = new ArrayList<>();


    public static class Type {
        //配置文件加载完成
        public static short LOAD_CONF_AFTER = 1;
    }

    public static  <T> void emit(short evtType,  EventData<T> datas) {
        handlerLists.forEach(e -> {
            e.entrySet().forEach(map -> {
                if (map.getKey() == evtType) {
                    map.getValue().onEvent(evtType, datas);
                }
            });
        });
    }

    public void addEventHandler(short evtType, IEventHandler handler) {
        Map<Short, IEventHandler> handlerMap = new HashMap<>(1);
        handlerLists.add(handlerMap);
    }

    public void removeEventHandler(short evtType, IEventHandler handler) {

        handlerLists.forEach(e -> {
            e.entrySet().forEach(map -> {
                if (map.getValue().hashCode() == handler.hashCode() && map.getKey() == evtType) {
                    handlerLists.remove(map);
                }
            });
        });
    }

    public static void main(String[] args) {
        Event.emit(Type.LOAD_CONF_AFTER,new EventData<>(new SettingDatas()));
    }


}
