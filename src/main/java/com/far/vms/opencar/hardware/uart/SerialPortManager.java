package com.far.vms.opencar.hardware.uart;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SerialPortManager {


    private SerialPort serialPort;

    //查找所有可用端口
    public  List<String> findPorts() {
        // 获得当前所有可用串口
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        List<String> portNameList = new ArrayList<String>();
        // 将可用串口名添加到List并返回该List
        for (SerialPort serialPort : serialPorts) {
            portNameList.add(serialPort.getSystemPortName());
        }
        //去重
        portNameList = portNameList.stream().distinct().collect(Collectors.toList());
        return portNameList;
    }

    /**
     * 打开串口
     *
     * @param portName 端口名称
     * @param baudRate 波特率
     * @return 串口对象
     */
    public SerialPort openPort(String portName, Integer baudRate) {
        this.serialPort = SerialPort.getCommPort(portName);
        if (baudRate != null) {
            serialPort.setBaudRate(baudRate);
        }
        if (!serialPort.isOpen()) { //开启串口
            serialPort.openPort(1000);
        } else {
            return serialPort;
        }
        // 设置一下串口的波特率等参数
        // 数据位：8
        // 停止位：1
        // 校验位：None
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        serialPort.setComPortParameters(baudRate, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);

        addListener();

        return serialPort;
    }


    /**
     * 关闭串口
     *
     * @param serialPort 待关闭的串口对象
     */
    public  void closePort(SerialPort serialPort) {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
        }
    }

    /**
     * 往串口发送数据
     *

     * @param content    待发送数据
     */
    public  void sendToPort( byte[] content) {
        if (!serialPort.isOpen()) {
            return;
        }
        serialPort.writeBytes(content, content.length);
    }

    /**
     * 从串口读取数据
     *
     * @param serialPort 当前已建立连接的SerialPort对象
     * @return 读取到的数据
     */
    public  byte[] readFromPort(SerialPort serialPort) {
        byte[] reslutData = null;
        try {
            if (!serialPort.isOpen()) {
                return null;
            }
            ;
            int i = 0;
            while (serialPort.bytesAvailable() > 0 && i++ < 5) Thread.sleep(20);
            byte[] readBuffer = new byte[serialPort.bytesAvailable()];
            int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
            if (numRead > 0) {
                reslutData = readBuffer;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return reslutData;
    }

    /**
     * 添加监听器
     */
    public void addListener() {
        try {

            serialPort.addDataListener(new SerialPortDataListener() {
                @Override
                public int getListeningEvents() {
                    return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    //返回要监听的事件类型，以供回调函数使用。
                    //可发回的事件包括：SerialPort.LISTENING_EVENT_DATA_AVAILABLE，
                    //SerialPort.LISTENING_EVENT_DATA_WRITTEN,SerialPort.LISTENING_EVENT_DATA_RECEIVED。分别对应有数据在串口（不论是读的还是写的），有数据写入串口，从串口读取数据。如果AVAILABLE和RECEIVED同时被监听，优先触发RECEIVED

                }

                @Override
                public void serialEvent(SerialPortEvent event) {
                    String data = "";
                    if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                        System.out.println("event "+event.getEventType());
                        return;//判断事件的类型
                    }
                    while (serialPort.bytesAvailable() != 0) {
                        byte[] newData = new byte[serialPort.bytesAvailable()];
                        int numRead = serialPort.readBytes(newData, newData.length);
                        String newDataString = new String(newData);
                        data = data + newDataString;
                        sendToPort(newData);
                        System.out.println("input " + data);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }//同样使用循环读取法读取所有数据

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SerialPortManager serialPortManager = new SerialPortManager();
        serialPortManager.openPort("COM1", 9600);

        System.out.println("okoko");
        Thread waitThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(100000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        waitThread.start();
        try {
            waitThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
