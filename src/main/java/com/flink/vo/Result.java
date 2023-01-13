package com.flink.vo;


import java.util.Date;
import java.util.List;

public class Result<T> {

    private List<T> data;

    private String msg;

    private Long time;

    private boolean success;

    public static Result success(String message) {
        Result<?> result = new Result<>();
        result.setMsg(message);
        result.setSuccess(true);
        return result;
    }

    public static Result success() {
        Result<?> result = new Result<>();
        result.setSuccess(true);
        return result;
    }

    public static Result fail() {
        Result<?> result = new Result<>();
        result.setSuccess(false);
        return result;
    }

    public static Result fail(String message) {
        Result<?> result = new Result<>();
        result.setMsg(message);
        result.setSuccess(false);
        return result;
    }

    public Result() {
        time = new Date().getTime();
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public Long getTime() {
        return time;
    }

    public boolean isSuccess() {
        return success;
    }
}
