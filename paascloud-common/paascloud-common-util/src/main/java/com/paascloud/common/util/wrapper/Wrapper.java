package com.paascloud.common.util.wrapper;


public class Wrapper<T> {


    /**
     * 成功码.
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 成功信息.
     */
    public static final String SUCCESS_MESSAGE = "操作成功";

    /**
     * 错误码.
     */
    public static final int ERROR_CODE = 500;

    /**
     * 错误信息.
     */
    public static final String ERROR_MESSAGE = "内部异常";

    /**
     * 错误码：参数非法
     */
    public static final int ILLEGAL_ARGUMENT_CODE_ = 100;

    /**
     * 错误信息：参数非法
     */
    public static final String ILLEGAL_ARGUMENT_MESSAGE = "参数非法";

    /**
     * 编号.
     */
    private int code;

    /**
     * 信息.
     */
    private String message;

    /**
     * 结果数据
     */
    private T result;

    Wrapper(){
        this(SUCCESS_CODE,SUCCESS_MESSAGE);

    }

    Wrapper(int code, String message) {
        this(code,message,null);
    }

    Wrapper(int code, String message, T result) {
        super();
        this.code(code).message(message).result(result);
    }
    private Wrapper<T> code(int code){
        this.setCode(code);
        return this;
    }

    private Wrapper<T> message(String message){
        this.setMessage(message);
        return this;
    }

    private Wrapper<T> result(T result){
        this.setResult(result);
        return this;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
