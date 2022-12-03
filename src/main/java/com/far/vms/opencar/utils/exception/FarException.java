package com.far.vms.opencar.utils.exception;

public class FarException extends RuntimeException {


    public static class Code {
        //可运行
        public static int RUNNABLE = 1;
        public static int CRASH = 2;

    }


    public FarException(int code, String message, Throwable cause) {
        super(message, cause);
        cause.printStackTrace();
    }

    public FarException(int code, String message) {
        super(message);

    }

}
