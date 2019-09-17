package com.paascloud.common.util.wrapper;

public class WrapMapper {

    public static <E> Wrapper<E> wrap(int code, String message, E o) {
        return new Wrapper<>(code, message, o);
    }

    public static Wrapper wrap(int code, String message) {
        return new Wrapper<>(code, message);
    }
}
