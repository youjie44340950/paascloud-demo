package com.paascloud.common.util.wrapper;

public class WrapMapper {

    public static <E> Wrapper<E> wrap(int code, String message, E o) {
        return new Wrapper<>(code, message, o);
    }

    public static Wrapper wrap(int code, String message) {
        return new Wrapper<>(code, message);
    }

    public static Wrapper errWrap() {
        return new Wrapper<>(500, "内部错误");
    }

    public static Wrapper timeOutWrap() {
        return new Wrapper<>(500, "Feign Time Out");
    }

    public static <E> Wrapper<E> success(E o) {
        return new Wrapper<>(200, "成功", o);
    }
}
