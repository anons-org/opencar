package com.far.vms.opencar.hardware.uart;



import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Objects;
import java.util.TooManyListenersException;

/**
 * 串口参数的配置 串口一般有如下参数可以在该串口打开以前进行配置： 包括串口号，波特率，输入/输出流控制，数据位数，停止位和奇偶校验。
 */
// 注：串口操作类一定要继承SerialPortEventListener

public class SerialPortUtils implements SerialPortEventListener{
    // 检测系统中可用的通讯端口类
    private CommPortIdentifier commPortId;
    // 枚举类型
    private Enumeration<CommPortIdentifier> portList;
    // 串口
    private SerialPort serialPort;
    // 输入流
    private InputStream inputStream;
    // 输出流
    private OutputStream outputStream;
    // 保存串口返回信息
    private String data;
    // 保存串口返回信息十六进制
    private String dataHex;
    /**
     * 发送报文
     */
    private String sendData;
    /**
     * 从机地址
     */
    private String machineAddr;
    /**
     * 湿度
     */
    private double humidity;
    /**
     * 温度
     */
    private double temperature;
    /**
     * TVOC 浓度
     */
    private double tvocConcentration;

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
    }

    public String getMachineAddr() {
        return machineAddr;
    }

    public void setMachineAddr(String machineAddr) {
        this.machineAddr = machineAddr;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTvocConcentration() {
        return tvocConcentration;
    }

    public void setTvocConcentration(double tvocConcentration) {
        this.tvocConcentration = tvocConcentration;
    }

    /**
     * 初始化串口
     * @Description: TODO
     * @param: paramConfig 存放串口连接必要参数的对象（会在下方给出类代码）
     * @return: void
     * @throws
     */

    public void init(ParamConfig paramConfig) {
        // 获取系统中所有的通讯端口
        this.portList = CommPortIdentifier.getPortIdentifiers();
        // 记录是否含有指定串口
        boolean isExsist = false;
        // 循环通讯端口
        while (this.portList.hasMoreElements()) {
            this.commPortId = this.portList.nextElement();
            // 判断是否是串口
            if (this.commPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // 比较串口名称是否是指定串口
                if (paramConfig.getSerialNumber().equals(commPortId.getName())) {
                    // 串口存在
                    isExsist = true;
                    // 打开串口
                    try {
                        this.serialPort = (SerialPort) commPortId.open(paramConfig.getSerialNumber(), 2000);
                        // 设置串口监听
                        this.serialPort.addEventListener(this);
                        // 设置串口数据时间有效(可监听)
                        this.serialPort.notifyOnDataAvailable(true);
                        // 设置当通信中断时唤醒中断线程
                        this.serialPort.notifyOnBreakInterrupt(true);
                        // 设置串口通讯参数:波特率，数据位，停止位,校验方式
                        this.serialPort.setSerialPortParams(paramConfig.getBaudRate(), paramConfig.getDataBit(),
                                paramConfig.getStopBit(), paramConfig.getCheckoutBit());
                    } catch (PortInUseException e) {
                        throw new RuntimeException("端口被占用");
                    } catch (TooManyListenersException e) {
                        throw new RuntimeException("监听器过多");
                    } catch (UnsupportedCommOperationException e) {
                        throw new RuntimeException("不支持的COMM端口操作异常");
                    }
                    // 结束循环
                    break;
                }
            }
        }
        // 若不存在该串口则抛出异常
        if (!isExsist) {
            throw new RuntimeException("不存在该串口！");
        }
    }

    /**
     * 实现接口SerialPortEventListener中的方法 读取从串口中接收的数据
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI: // 通讯中断
            case SerialPortEvent.OE: // 溢位错误
            case SerialPortEvent.FE: // 帧错误
            case SerialPortEvent.PE: // 奇偶校验错误
            case SerialPortEvent.CD: // 载波检测
            case SerialPortEvent.CTS: // 清除发送
            case SerialPortEvent.DSR: // 数据设备准备好
                System.out.println("数据设备准备好");
                break;
            case SerialPortEvent.RI: // 响铃侦测
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 输出缓冲区已清空
                System.out.println("输出缓冲区已清空");
                break;
            case SerialPortEvent.DATA_AVAILABLE: // 有数据到达
                System.out.println("有数据到达...");
                // 调用读取数据的方法
                readComm();
                break;
            default:
                break;
        }
    }

    /**
     * 读取串口返回信息
     * @return: void
     */
    public void readComm() {
        try {
            this.inputStream = this.serialPort.getInputStream();
            // 通过输入流对象的available方法获取数组字节长度
            byte[] readBuffer = new byte[this.inputStream.available()];
            // 从线路上读取数据流
            int len = 0;
            while ((len = this.inputStream.read(readBuffer)) != -1) {// 直接获取到的数据
                this.data = new String(readBuffer, 0, len).trim();// 转为十六进制数据
                this.dataHex = bytesToHexString(readBuffer);
                System.out.println("dataHex:" + dataHex);// 读取后置空流对象
                inputStream.close();
                inputStream = null;
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException("读取串口数据时发生IO异常");
        }
    }

    /**
     * 发送信息到串口
     * @param: data
     * @return: void
     * @throws
     */
    public void sendComm(String data) {
        byte[] writerBuffer = null;
        try {
            writerBuffer = hexToByteArray(data);
        } catch (NumberFormatException e) {
            throw new RuntimeException("命令格式错误！");
        }
        try {
            this.outputStream = this.serialPort.getOutputStream();
            this.outputStream.write(writerBuffer);
            this.outputStream.flush();
        } catch (NullPointerException e) {
            throw new RuntimeException("找不到串口。");
        } catch (IOException e) {
            throw new RuntimeException("发送信息到串口时发生IO异常");
        }
    }

    /**
     * 关闭串口
     * @Description: 关闭串口
     * @param:
     * @return: void
     * @throws
     */
    public void closeSerialPort() {
        if (this.serialPort != null) {
            this.serialPort.notifyOnDataAvailable(false);
            this.serialPort.removeEventListener();
            if (this.inputStream != null) {
                try {
                    this.inputStream.close();
                    this.inputStream = null;
                } catch (IOException e) {
                    throw new RuntimeException("关闭输入流时发生IO异常");
                }
            }
            if (this.outputStream != null) {
                try {
                    this.outputStream.close();
                    this.outputStream = null;
                } catch (IOException e) {
                    throw new RuntimeException("关闭输出流时发生IO异常");
                }
            }
            this.serialPort.close();
            this.serialPort = null;
        }
    }

    /**
     * 十六进制串口返回值获取
     */
    public String getDataHex() {
        String result = dataHex;
        // 置空执行结果
        dataHex = null;
        // 返回执行结果
        return result;
    }

    /**
     * 串口返回值获取
     */
    public String getData() {
        String result = data;
        // 置空执行结果
        data = null;
        // 返回执行结果
        return result;
    }

    /**
     * Hex字符串转byte
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte
     */
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * hex字符串转byte数组
     * @param inHex 待转换的Hex字符串
     * @return 转换后的byte数组结果
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 数组转换成十六进制字符串
     * @param bArray
     * @return HexString
     */
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2){
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 处理从机返回值
     * @param
     * @throws InterruptedException
     */
    public void ProcessingReturnedData(){
        if(Objects.isNull(this.dataHex)) return;
        this.setMachineAddr(covert(this.dataHex.substring(0,2),1) < 10 ? "0"+(int)covert(this.dataHex.substring(0,2),1) : (int)covert(this.dataHex.substring(0,2),1)+"");
        this.setHumidity(covert(this.dataHex.substring(6,10),0.1));
        this.setTemperature(covert(this.dataHex.substring(10,14),0.1));
        this.setTvocConcentration(covert(this.dataHex.substring(30,34),1));
    }

    /**
     * 把十六进制字符串转十进制数值，然后乘以单位转化率
     * @param content
     * @return
     */
    public static double covert(String content, double rate){
        BigDecimal bd = new BigDecimal(new BigInteger(content, 16).doubleValue()*rate);
        return bd.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static void main(String[] args) throws InterruptedException {
        // 实例化串口操作类对象
        SerialPortUtils serialPort = new SerialPortUtils();
        // 创建串口必要参数接收类并赋值，赋值串口号，波特率，校验位，数据位，停止位
        ParamConfig paramConfig = new ParamConfig("COM1", 9600, 0, 8, 1);
        try{
            // 初始化设置,打开串口，开始监听读取串口数据
            serialPort.init(paramConfig);
            // 调用串口操作类的sendComm方法发送数据到串口
            serialPort.sendComm("FFFF010C030000000AC4D0");
            Thread.sleep(2000);
            serialPort.ProcessingReturnedData();
            System.out.println("服务器发送报文：" + "FFFF010C030000000AC4D0");
            System.out.println("从机返回值：" + serialPort.dataHex);
            System.out.println("从机地址：" + serialPort.getMachineAddr());
            System.out.println("湿度：" + serialPort.getHumidity());
            System.out.println("温度：" + serialPort.getTemperature());
            System.out.println("TVOC浓度：" + serialPort.getTvocConcentration());


            Thread waitThread  = new Thread(()->{
                while (true){
                    try {
                        Thread.sleep(100000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            waitThread.start();
            waitThread.join();

            // 关闭串口
            serialPort.closeSerialPort();

        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

    }

}






