package com.far.vms.opencar.test.net;

import cn.hutool.core.io.BufferUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ByteUtil;
import cn.hutool.db.Db;
import com.far.vms.opencar.debugger.server.DServer;

import com.far.vms.opencar.ui.net.DbgClient;
import com.far.vms.opencar.utils.SckUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class NetTest {


    public static void main(String[] args) {


        DServer.startDserver();






        DbgClient dbgClient = new DbgClient();

        dbgClient.setOnMessageCall((s) -> {
            System.out.println("收到消息" + s);
            return true;

        });

        dbgClient.start();


        dbgClient.sendMessage("测试下封装...");

    }


}
