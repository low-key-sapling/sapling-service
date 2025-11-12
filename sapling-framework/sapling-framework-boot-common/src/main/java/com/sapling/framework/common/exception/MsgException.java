package com.sapling.framework.common.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 不打印堆栈消息,抛出消息类错误,用于前台页面的处理(代替JsonResult.fail())
 *
 * @author DuWei
 */
public class MsgException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String _msg;

    private int code;

    public static final MsgException exception() {
        return new MsgException();
    }

    public static final MsgException exception(int code) {
        return new MsgException(code);
    }

    public static final MsgException exception(String msg) {
        return new MsgException(msg);
    }

    public static final MsgException exception(String msg, int code) {
        return new MsgException(msg, code);
    }

    public MsgException() {
        _msg = "系统错误";
        code = 500;
    }

    /**
     * @param msg 用于显示的消息
     */
    public MsgException(String msg) {
        this();
        _msg = msg;
    }

    /**
     * @param code 错误码
     */
    public MsgException(int code) {
        this();
        this.code = code;
    }

    /**
     * @param msg  用于显示的消息
     * @param code 错误码
     */
    public MsgException(String msg, int code) {
        _msg = msg;
        this.code = code;
    }

    @Override
    public void printStackTrace() {
        return;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        //s.print(_msg);
        return;
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        return;
        //s.print(_msg);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return null;
    }

    @Override
    public String getMessage() {
        return _msg;
    }

    @Override
    public String getLocalizedMessage() {
        return _msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "{code: " + code + ", " +
                "message: " + _msg + "}";
    }
}
