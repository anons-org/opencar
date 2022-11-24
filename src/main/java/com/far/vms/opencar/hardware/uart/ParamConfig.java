package com.far.vms.opencar.hardware.uart;


public class ParamConfig {
    private String serialNumber;// 串口号
    private int baudRate; // 波特率
    private int checkoutBit; // 校验位
    private int dataBit; // 数据位
    private int stopBit; // 停止位

    public ParamConfig() {}

    /**
     * 构造方法
     * @param serialNumber 串口号
     * @param baudRate 波特率
     * @param checkoutBit 校验位
     * @param dataBit 数据位
     * @param stopBit 停止位
     */
    public ParamConfig(String serialNumber, int baudRate, int checkoutBit, int dataBit, int stopBit) {
        this.serialNumber = serialNumber;
        this.baudRate = baudRate;
        this.checkoutBit = checkoutBit;
        this.dataBit = dataBit;
        this.stopBit = stopBit;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getCheckoutBit() {
        return checkoutBit;
    }

    public void setCheckoutBit(int checkoutBit) {
        this.checkoutBit = checkoutBit;
    }

    public int getDataBit() {
        return dataBit;
    }

    public void setDataBit(int dataBit) {
        this.dataBit = dataBit;
    }

    public int getStopBit() {
        return stopBit;
    }

    public void setStopBit(int stopBit) {
        this.stopBit = stopBit;
    }
}