package com.star.common;

import lombok.Getter;
import lombok.Setter;

/**
 * 通用响应
 *
 *  
 * @param <T> 泛型
 */
@Setter
@Getter
public class CommonResult<T> extends Result<T> {

    /**
     * 响应数据
     */
    private T data;

    /**
     * 数据总页，分页使用
     */
    private Integer total;

    public CommonResult(Integer code) {
        super(code, "操作成功");
    }

    public CommonResult(Integer code, String msg) {
        super(code, msg);
    }

    public CommonResult(Integer code, String msg, T data) {
        super(code, msg);
        this.data = data;
    }

    public static <T> Result<T> success() {
        CommonResult<T> result = new CommonResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(null);
        return result;
    }

    public static <T> Result<T> success(T data) {
        CommonResult<T> result = new CommonResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(data);
        return result;
    }

    /**
     * 分页响应，返回数据列表，总记录数
     *
     * @param data  数据列表，泛型
     * @param total 总页
     * @param <T>   泛型
     */
    public static <T> Result<T> success(T data, Integer total) {
        CommonResult<T> result = new CommonResult<>(ResultCode.REQUEST_SUCCESS.getCode());
        result.setData(data);
        result.setTotal(total);
        return result;
    }

    public static <T> Result<T> success(String msg) {
        return new Result<>(ResultCode.REQUEST_SUCCESS.getCode(), msg);
    }

    public static <T> Result<T> success(String msg, T data) {
        return new CommonResult<T>(ResultCode.REQUEST_SUCCESS.getCode(), msg, data);
    }

    public static <T> Result<T> error(String msg) {
        return new Result<T>(ResultCode.REQUEST_ERROR.getCode(), msg);
    }


    public CommonResult(T data, Integer total) {
        this.data = data;
        this.total = total;
    }

    public CommonResult(Integer code, String msg, T data, Integer total) {
        super(code, msg);
        this.data = data;
        this.total = total;
    }
}
