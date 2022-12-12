package com.far.vms.opencar.ui.event;

import com.far.vms.opencar.ui.entity.SettingDatas;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/*
 * @description: 通知管理
 *  通知是异步的，通知会先存储，直道有消费
 * @author mike/Fang.J
 * @data 2022/12/11
 */
public class Notify {


    public interface INotifyHandler<T> {
        public void onEvent(int evtType, NotifyData<?> data);
    }

    public static class NotifyData<T> {
        private T data;

        public NotifyData(T d) {
            this.data = d;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }


    public static class MessageData {
        int type;
        NotifyData<?> data;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public NotifyData<?> getData() {
            return data;
        }

        public void setData(NotifyData<?> data) {
            this.data = data;
        }
    }

    private static List<MessageData> messageDataList = new ArrayList<>();


    public static ReentrantLock takeLock = new ReentrantLock();


    public static void sendMessage() {
        if (messageDataList.size() > 0) {
            for (int i = 0; i < messageDataList.size(); i++) {
                int evtType = messageDataList.get(i).getType();
                NotifyData<?> datas = messageDataList.get(i).getData();
                final int idx = i;

                var notifyList = handlerLists.stream().filter(e -> {
                    return e.containsKey(evtType);
                }).collect(Collectors.toList());

                if (notifyList.size() > 0) {//有消费者再处理
                    for (int j = 0; j < notifyList.size(); j++) {
                        notifyList.get(j).get(evtType).onEvent(evtType, datas);
                    }
                    //所有消费者都接受了才删除
                    messageDataList.remove(idx);
                }


            }
        }
    }

    public static void startBackstageLoop() {

        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    if (takeLock.tryLock()) {
                        sendMessage();
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    takeLock.unlock();
                }
            }
        });
        thread.setName("Event");
        thread.start();
    }


    private static List<Map<Integer, INotifyHandler<?>>> handlerLists = new ArrayList<>();

    @Deprecated
    public static void add(int evtType, INotifyHandler<?> handler) {
        addEventHandler(evtType, handler);
    }

    public static void on(int evtType, INotifyHandler<?> handler) {
        addEventHandler(evtType, handler);
    }

    public static void rm(int evtType, INotifyHandler<?> handler) {
        removeEventHandler(evtType, handler);
    }


    public static void emit(int evtType, NotifyData<?> datas) {
        MessageData messageData = new MessageData();
        messageData.setType(evtType);
        messageData.setData(datas);
        messageDataList.add(messageData);
    }



    private static <T> void addEventHandler(int evtType, INotifyHandler<T> handler) {
        Map<Integer, INotifyHandler<?>> handlerMap = new HashMap<>(1);
        handlerMap.put(evtType, handler);
        handlerLists.add(handlerMap);
    }

    private static <T> void removeEventHandler(int evtType, INotifyHandler<T> handler) {

        for (int i = 0; i < handlerLists.size(); i++) {
            Optional<? extends INotifyHandler<?>> optional = handlerLists.get(i).entrySet().stream().filter(map -> {
                return map.getValue().hashCode() == handler.hashCode() && map.getKey() == evtType;
            }).map(e2 -> e2.getValue()).findAny();
            if (optional.isPresent()) {
                handlerLists.remove(handlerLists.get(i));
            }
        }

    }

    public static void main(String[] args) {

//
        INotifyHandler<SettingDatas> iEventHandler = new INotifyHandler<SettingDatas>() {
            @Override
            public void onEvent(int evtType, NotifyData<?> data) {
                System.out.println("1111");
            }
        };
        Notify.startBackstageLoop();
        Notify.emit(SysType.LOAD_CONF_BEFORE, new NotifyData<>(new SettingDatas()));
        Notify.add(SysType.LOAD_CONF_BEFORE, iEventHandler);
//      //  Event.rm(Type.LOAD_CONF_BEFORE, iEventHandler);

////        System.out.println(111);

    }


    public static class SysType {
        //配置文件加载完成
        public static short LOAD_CONF_AFTER = 1;
        public static short LOAD_CONF_BEFORE = 2;
    }


}
