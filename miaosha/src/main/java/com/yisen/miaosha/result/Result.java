package com.yisen.miaosha.result;

public class Result<T> {

    private int code;
    private String msg;
    private T data;

    //成功时调用
    public static <T> Result<T> success(T data){
        return new Result<T>(data);
    }

    //失败时调用
    public static <T> Result<T> error(CodeMsg codeMsg){
        return new Result<T>(codeMsg);
    }

    //构造函数
    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }
    //构造函数
    private Result(CodeMsg codeMsg){
        if(codeMsg == null) return;
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
}
