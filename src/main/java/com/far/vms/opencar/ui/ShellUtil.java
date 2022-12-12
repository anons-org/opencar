package com.far.vms.opencar.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Function;

/*
 * @description:
 *
 * 调用操作系统的Shell 需要兼容mac linux win
 *
 * @author mike/Fang.J
 * @data 2022/11/29
 */
public class ShellUtil {


    public static class RunShellThread implements Runnable {

        private InputStream inputStream;

        private Function<String, Integer> caller;

        public Function<String, Integer> getCaller() {
            return caller;
        }

        public RunShellThread setCaller(Function<String, Integer> caller) {
            this.caller = caller;
            return this;
        }

        public InputStream getInputStream() {
            return inputStream;
        }

        public RunShellThread setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
            return this;
        }

        @Override
        public void run() {

            InputStreamReader inputStreamReader = null;
            BufferedReader br = null;
            try {
                inputStreamReader = new InputStreamReader(inputStream);
                br = new BufferedReader(inputStreamReader);
                // 打印信息
                String line = null;
                while ((line = br.readLine()) != null) {
                    caller.apply(line);
                }
            } catch (IOException ioe) {
                caller.apply(ioe.getMessage());
                ioe.printStackTrace();
            } finally {
                try {
                    br.close();
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @param shellProgPath
     * @param args
     * @param caller
     * @description:
     * @return: void
     * @author mike/Fang.J
     * @data 2022/11/29
     */
    public static void runShell(String shellProgPath, String[] args, Function<String, Integer> caller) {

        try {
            Process p = Runtime.getRuntime().exec(args);
            //caller没有考虑线程安全
            new Thread(new RunShellThread().setCaller(caller).setInputStream(p.getInputStream())).start();
            new Thread(new RunShellThread().setCaller(caller).setInputStream(p.getErrorStream())).start();
            p.waitFor();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
