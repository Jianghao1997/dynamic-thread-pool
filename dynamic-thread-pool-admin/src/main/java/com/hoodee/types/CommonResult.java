package com.hoodee.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 通用返回
 *
 * @param <T> 数据泛型
 * @author hoodee
 */
@Data
public class CommonResult<T> implements Serializable, Runnable {

    /**
     * 错误码
     */
    private Integer code;
    /**
     * 返回数据
     */
    private T data;
    /**
     * 错误提示，用户可阅读
     */
    private String msg;

    /**
     * 将传入的 result 对象，转换成另外一个泛型结果的对象
     * 因为 A 方法返回的 CommonResult 对象，不满足调用其的 B 方法的返回，所以需要进行转换。
     *
     * @param result 传入的 result 对象
     * @param <T>    返回的泛型
     * @return 新的 CommonResult 对象
     */
    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = message;
        return result;
    }

    public static <T> CommonResult<T> error(Integer code, String message, T data) {
        CommonResult<T> result = error(code, message);
        result.data = data;
        return result;
    }

    public static <T> CommonResult<T> error(String message) {
        CommonResult<T> result = new CommonResult<>();
        result.code = Code.FAIL.getCode();
        result.msg = message;
        return result;
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = Code.SUCCESS.getCode();
        result.data = data;
        result.msg = "";
        return result;
    }

    public static <T> CommonResult<T> success(T data, String msg) {
        CommonResult<T> result = new CommonResult<>();
        result.code = Code.SUCCESS.getCode();
        result.data = data;
        result.msg = msg;
        return result;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, Code.SUCCESS.getCode());
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isSuccess() {
        return isSuccess(code);
    }

    @JsonIgnore // 避免 jackson 序列化
    public boolean isError() {
        return !isSuccess();
    }

    @Override
    public void run() {

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public enum Code {
        SUCCESS(0, "调用成功"),
        FAIL(-1, "调用失败"),
        ILLEGAL_PARAMETER(-2, "非法参数"),
        ;

        private Integer code;
        private String info;
    }

}
