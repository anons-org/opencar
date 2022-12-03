package com.far.vms.opencar.ui.net;

import cn.hutool.core.util.ByteUtil;
import com.far.vms.opencar.utils.exception.FarException;
import com.far.vms.opencar.utils.SckUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * @author mike/Fang.J
 * @description: 负责和模拟器通讯
 * @return:
 * @data 2022/12/3
 */
public class DbgClient {

    //socket对象初始化
    Socket socket = null;
    //输出流 os对象初始化
    OutputStream os = null;

    InputStream ins = null;

    private Function<String, Boolean> onMessageCall;

    public Function<String, Boolean> getOnMessageCall() {
        return onMessageCall;
    }

    public void setOnMessageCall(Function<String, Boolean> onMessageCall) {
        this.onMessageCall = onMessageCall;
    }

    /**
     * @description: 发送消息
     * @return:
     * @author mike/Fang.J
     * @data 2022/12/3
     */

    public void sendMessage(String msg) {
        try {
            os.write(SckUtil.builderOfHeaderLen(msg));
        } catch (IOException e) {
            throw new FarException(FarException.Code.RUNNABLE, e.getMessage(), e);
        }
    }

    public void start() {
        try {


            if (null == onMessageCall) throw new FarException(FarException.Code.RUNNABLE, "需要设置消息回调函数!");


            InetAddress inet = InetAddress.getByName("127.0.0.1");
            socket = new Socket(inet, 1234);//inet是服务端ip
            //有数据立刻输出
            socket.setTcpNoDelay(true);
            //2、获取一
            //个输出流，用于写出要发送的数据
            os = socket.getOutputStream();
            ins = socket.getInputStream();

            InputStream finalIns = ins;
            Socket finalSocket = socket;
            Thread takeMessageThread = new Thread(() -> {
                byte[] pckHead = new byte[4];

                while (true) {
                    if (!finalSocket.isConnected()) continue;
                    try {
                        if (finalIns.read(pckHead) != -1) {//read 进入阻塞状态
                            System.out.println("服务端数据的长度" + pckHead);
                            int dataLen = ByteUtil.bytesToInt(pckHead, ByteOrder.BIG_ENDIAN);
                            if (dataLen > 2048) {
                                System.out.println("服务端发送的包太长！");
                                continue;
                            }
                            byte[] data = new byte[dataLen];

                            finalIns.read(data);

                            String s = new String(data, StandardCharsets.UTF_8);
                            onMessageCall.apply(s);
                        }
                    } catch (Exception e) {
                        throw new FarException(FarException.Code.CRASH, e.getMessage(), e);
                    }

                }
            });
            takeMessageThread.start();

        } catch (Exception e) {

            if (socket != null) {
                try {
                    socket.close();//关闭
                } catch (IOException e1) {
                    throw new FarException(FarException.Code.CRASH, e1.getMessage(), e1);
                }
            }
            if (os != null) {
                try {
                    os.close();//关闭
                } catch (IOException e2) {
                    throw new FarException(FarException.Code.CRASH, e2.getMessage(), e2);
                }
            }

            if (ins != null) {
                try {
                    ins.close();//关闭
                } catch (IOException e4) {
                    throw new FarException(FarException.Code.CRASH, e4.getMessage(), e4);
                }
            }
            throw new FarException(FarException.Code.CRASH, e.getMessage(), e);


        }
    }


}
